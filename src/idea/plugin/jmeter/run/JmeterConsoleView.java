package idea.plugin.jmeter.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import idea.plugin.jmeter.run.tailer.Tailer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

class JmeterConsoleView extends JSplitPane implements ConsoleView, DataProvider {
    private final ConsoleViewImpl console;
    private final File logFile;

    public JmeterConsoleView(Project project, File logFile, JmeterRunConfiguration runConfiguration) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        console = new ConsoleViewImpl(project, GlobalSearchScope.projectScope(project), false, null);
        this.logFile = logFile;
        console.addCustomConsoleAction(new RunJmeterGuiAction(runConfiguration));

        add(new JLabel("test"));
        add(console);
        setDividerLocation(400);
    }

    @Override
    public void print(String s, ConsoleViewContentType contentType) {
        console.print(s, contentType);
    }

    @Override
    public void clear() {
        console.clear();
    }

    @Override
    public void scrollTo(int offset) {
        console.scrollTo(offset);
    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        final Tailer tailer = Tailer.create(logFile, new LogfileTailerListener(this), 500);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
                // Give tailer a chance to finish its job
                sleep(tailer.getDelay() * 3 / 2);
                tailer.stop();
            }
        });

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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public Object getData(@NonNls String dataId) {
        if (LangDataKeys.CONSOLE_VIEW.is(dataId)) {
            return this;
        }
        return null;
    }
}