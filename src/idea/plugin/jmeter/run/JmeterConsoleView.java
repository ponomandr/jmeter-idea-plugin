package idea.plugin.jmeter.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.LangDataKeys;
import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;
import idea.plugin.jmeter.run.tailer.Tailer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class JmeterConsoleView extends JSplitPane implements ConsoleView, DataProvider {
    private final File logFile;
    private final JmeterTreeView treeView;
    private final JTextArea samplerData;
    private final JTextArea responseData;
    private final PipedOutputStream outputStream;
    private PipedInputStream inputStream;
    private final Thread thread;

    public JmeterConsoleView(File logFile) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.logFile = logFile;
//        console = new ConsoleViewImpl(project, GlobalSearchScope.projectScope(project), false, null);
//        console.addCustomConsoleAction(new RunJmeterGuiAction(runConfiguration));

        treeView = new JmeterTreeView(this);
        add(new JScrollPane(treeView));

        samplerData = new JTextArea();
        responseData = new JTextArea();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Request", samplerData);
        tabbedPane.add("Response Data", responseData);
        add(tabbedPane);

        outputStream = new PipedOutputStream();
        inputStream = null;
        try {
            inputStream = new PipedInputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final JmeterXmlParser parser = new JmeterXmlParser(inputStream, this);

        thread = new Thread() {
            @Override
            public void run() {
                parser.parse();
            }
        };
        thread.setDaemon(true);

        setDividerLocation(500);
    }

    @Override
    public void print(String s, ConsoleViewContentType contentType) {
    }

    @Override
    public void clear() {
        samplerData.setText("");
        responseData.setText("");
        treeView.clear();
    }

    @Override
    public void scrollTo(int offset) {
    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        processHandler.addProcessListener(new JmeterProcessListener(logFile, outputStream));
        thread.start();
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
        return samplerData;
    }

    @Override
    public void dispose() {
        treeView.clear();
        Tailer.closeQuietly(inputStream);
        Tailer.closeQuietly(outputStream);
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
        samplerData.setText(assertion.getFailureMessage());
        responseData.setText("");
    }

    public void onSampleResultSelected(SampleResult sampleResult) {
        samplerData.setText(sampleResult.getSamplerData());
        responseData.setText(sampleResult.getResponseData());
    }
}