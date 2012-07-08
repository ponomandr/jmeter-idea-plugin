package idea.plugin.jmeter.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class JmeterConsoleView extends JSplitPane implements ConsoleView, DataProvider {
    private final File logFile;
    private final ConsoleViewImpl console;
    private final JmeterTreeView treeView;

    public JmeterConsoleView(Project project, File logFile, JmeterRunConfiguration runConfiguration) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.logFile = logFile;
        console = new ConsoleViewImpl(project, GlobalSearchScope.projectScope(project), false, null);
        console.addCustomConsoleAction(new RunJmeterGuiAction(runConfiguration));

        treeView = new JmeterTreeView();
        add(new JScrollPane(treeView));
        add(console);
        setDividerLocation(500);
    }

    @Override
    public void print(String s, ConsoleViewContentType contentType) {
        console.print(s, contentType);
    }

    @Override
    public void clear() {
        console.clear();
        treeView.clear();
    }

    @Override
    public void scrollTo(int offset) {
        console.scrollTo(offset);
    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        processHandler.addProcessListener(new JmeterProcessListener(this, logFile));
        console.attachToProcess(processHandler);
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
        console.performWhenNoDeferredOutput(runnable);
    }

    @Override
    public void setHelpId(String helpId) {
        console.setHelpId(helpId);
    }

    @Override
    public void addMessageFilter(Filter filter) {
        console.addMessageFilter(filter);
    }

    @Override
    public void printHyperlink(String hyperlinkText, HyperlinkInfo info) {
        console.printHyperlink(hyperlinkText, info);
    }

    @Override
    public int getContentSize() {
        return console.getContentSize();
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @NotNull
    @Override
    public AnAction[] createConsoleActions() {
        return console.createConsoleActions();
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
        return console.getPreferredFocusableComponent();
    }

    @Override
    public void dispose() {
        console.dispose();
    }

    @Override
    public Object getData(@NonNls String dataId) {
        if (LangDataKeys.CONSOLE_VIEW.is(dataId)) {
            return this;
        }
        return null;
    }


    public void addTestOk(String sampleName) {
        treeView.addTestOk(sampleName);
    }

    public void addTestFailed(String sampleName, List<Assertion> failedAssertions) {
        treeView.addTestFailed(sampleName, failedAssertions);
    }
}