package idea.plugin.jmeter.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.ui.TabbedPaneWrapper;
import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class JmeterConsoleView extends JSplitPane implements ConsoleView, DataProvider {
    private final File logFile;
    private final JmeterTreeView treeView;
    private final JTextArea samplerResult;
    private final JTextArea request;
    private final JTextArea responseData;

    public JmeterConsoleView(File logFile) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.logFile = logFile;
//        console = new ConsoleViewImpl(project, GlobalSearchScope.projectScope(project), false, null);
//        console.addCustomConsoleAction(new RunJmeterGuiAction(runConfiguration));

        treeView = new JmeterTreeView(this);
        add(new JScrollPane(treeView));

        samplerResult = new JTextArea();
        request = new JTextArea();
        responseData = new JTextArea();

        TabbedPaneWrapper tabbedPane = new TabbedPaneWrapper(this);
        tabbedPane.insertTab("Sampler result", null, new JScrollPane(samplerResult), null, 0);
        tabbedPane.insertTab("Request", null, new JScrollPane(request), null, 1);
        tabbedPane.insertTab("Response Data", null, new JScrollPane(responseData), null, 2);
        add(tabbedPane.getComponent());

        setDividerLocation(500);
    }

    @Override
    public void print(String s, ConsoleViewContentType contentType) {
    }

    @Override
    public void clear() {
        samplerResult.setText("");
        request.setText("");
        responseData.setText("");
        treeView.clear();
    }

    @Override
    public void scrollTo(int offset) {
    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        processHandler.addProcessListener(new JmeterProcessListener(logFile, this));
    }

    @Override
    public void setOutputPaused(boolean value) {
    }

    @Override
    public boolean isOutputPaused() {
        return false;
    }

    @Override
    public boolean hasDeferredOutput() {
        return false;
    }

    @Override
    public void performWhenNoDeferredOutput(Runnable runnable) {
    }

    @Override
    public void setHelpId(String helpId) {
    }

    @Override
    public void addMessageFilter(Filter filter) {
    }

    @Override
    public void printHyperlink(String hyperlinkText, HyperlinkInfo info) {
    }

    @Override
    public int getContentSize() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @NotNull
    @Override
    public AnAction[] createConsoleActions() {
        return new AnAction[0];
    }

    @Override
    public void allowHeavyFilters() {
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public JComponent getPreferredFocusableComponent() {
        return samplerResult;
    }

    @Override
    public void dispose() {
        treeView.clear();
    }

    @Override
    public Object getData(@NonNls String dataId) {
        if (LangDataKeys.CONSOLE_VIEW.is(dataId)) {
            return this;
        }
        return null;
    }


    public void addSampleResult(SampleResult sampleResult) {
        treeView.addTestFailed(sampleResult);
    }

    public void onAssertionSelected(Assertion assertion) {
        samplerResult.setText(assertion.getFailureMessage());
        request.setText("");
        responseData.setText("");
    }

    public void onSampleResultSelected(SampleResult sampleResult) {
        StringBuilder samplerContent = new StringBuilder();
        if (!StringUtils.isBlank(sampleResult.getResponseHeader())) {
            samplerContent.append("Response headers:\n");
            samplerContent.append(sampleResult.getResponseHeader());
            samplerContent.append('\n');
        }
        if (!StringUtils.isBlank(sampleResult.getSamplerData())) {
            samplerContent.append(sampleResult.getSamplerData());
        }
        this.samplerResult.setText(samplerContent.toString());

        StringBuilder requestContent = new StringBuilder();
        if (!StringUtils.isBlank(sampleResult.getRequestHeader())) {
            requestContent.append("Request headers:\n");
            requestContent.append(sampleResult.getRequestHeader());
            requestContent.append('\n');
        }
        request.setText(requestContent.toString());

        responseData.setText(sampleResult.getResponseData());
    }
}