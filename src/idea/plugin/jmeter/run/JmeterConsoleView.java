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
import java.awt.*;
import java.io.File;

public class JmeterConsoleView extends JSplitPane implements ConsoleView, DataProvider {
    public static final String SAMPLER_TABS = "samplerTabs";
    public static final String ASSERTION_TABS = "assertionTabs";
    private final File logFile;
    private final JmeterTreeView treeView;
    private final JTextArea samplerResult;
    private final JTextArea request;
    private final JTextArea responseData;
    private final JTextArea assertionResult;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

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
        assertionResult = new JTextArea();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);

        TabbedPaneWrapper samplerTabs = new TabbedPaneWrapper(this);
        samplerTabs.insertTab("Sampler result", null, new JScrollPane(samplerResult), null, 0);
        samplerTabs.insertTab("Request", null, new JScrollPane(request), null, 1);
        samplerTabs.insertTab("Response Data", null, new JScrollPane(responseData), null, 2);
        cardPanel.add(samplerTabs.getComponent(), SAMPLER_TABS);

        TabbedPaneWrapper assertionTabs = new TabbedPaneWrapper(this);
        assertionTabs.insertTab("Assertion result", null, new JScrollPane(assertionResult), null, 0);
        cardPanel.add(assertionTabs.getComponent(), ASSERTION_TABS);

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

    public void onSampleResultSelected(SampleResult result) {
        samplerResult.setText(getSamplerResultContent(result));
        request.setText(getResultContent(result));
        responseData.setText(getResponseDataContent(result));
        cardLayout.show(cardPanel, SAMPLER_TABS);
    }

    public void onAssertionSelected(Assertion assertion) {
        assertionResult.setText(assertion.getFailureMessage());
        cardLayout.show(cardPanel, ASSERTION_TABS);
    }

    private String getSamplerResultContent(SampleResult result) {
        StringBuilder samplerContent = new StringBuilder();
        samplerContent.append("Thread Name: ");
        samplerContent.append(result.getThreadName());
        samplerContent.append('\n');
        samplerContent.append("Sample Start: ");
        samplerContent.append(result.getSampleStart());
        samplerContent.append('\n');
        samplerContent.append("Load Time: ");
        samplerContent.append(result.getLoadTime());
        samplerContent.append('\n');
        samplerContent.append("Latency: ");
        samplerContent.append(result.getLatency());
        samplerContent.append('\n');
        samplerContent.append("Size in Bytes: ");
        samplerContent.append(result.getSizeInBytes());
        samplerContent.append('\n');
        samplerContent.append("Sample Count: ");
        samplerContent.append(result.getSampleCount());
        samplerContent.append('\n');
        samplerContent.append("Error Count: ");
        samplerContent.append(result.getErrorCount());
        samplerContent.append('\n');
        samplerContent.append("Response Code: ");
        samplerContent.append(result.getResponseCode());
        samplerContent.append(' ');
        samplerContent.append(result.getResponseMessage());
        samplerContent.append('\n');

        if (!StringUtils.isBlank(result.getResponseHeader())) {
            samplerContent.append("\nResponse Headers:\n");
            samplerContent.append(result.getResponseHeader());
            samplerContent.append('\n');
        }
        if (!StringUtils.isBlank(result.getSamplerData())) {
            samplerContent.append(result.getSamplerData());
        }
        return samplerContent.toString();
    }

    private String getResultContent(SampleResult result) {
        StringBuilder requestContent = new StringBuilder();
        requestContent.append(result.getMethod());
        requestContent.append(' ');
        requestContent.append(result.getUrl());
        requestContent.append("\n\n");

        if (!StringUtils.isBlank(result.getRequestHeader())) {
            requestContent.append("Cookies:\n");
            requestContent.append(result.getCookies());
            requestContent.append('\n');
        }

        if (!StringUtils.isBlank(result.getRequestHeader())) {
            requestContent.append("Request Headers:\n");
            requestContent.append(result.getRequestHeader());
            requestContent.append('\n');
        }
        return requestContent.toString();
    }

    private String getResponseDataContent(SampleResult result) {
        return result.getResponseData();
    }
}