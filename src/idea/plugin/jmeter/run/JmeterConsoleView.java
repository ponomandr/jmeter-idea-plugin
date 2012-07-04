package idea.plugin.jmeter.run;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import idea.plugin.jmeter.run.tailer.Tailer;

import java.io.File;

class JmeterConsoleView extends ConsoleViewImpl {

    private final File logFile;

    public JmeterConsoleView(Project project, File logFile) {
        super(project, GlobalSearchScope.projectScope(project), false, null);
        this.logFile = logFile;

    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        final Tailer tailer = Tailer.create(logFile, new LogfileTailerListener(this), 500);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
                // Give tailer a chance to finish its job
                try {
                    Thread.sleep(tailer.getDelay() * 3 / 2);
                } catch (InterruptedException e) {
                    // Do nothing
                }
                tailer.stop();
            }
        });

        super.attachToProcess(processHandler);
    }

    @Override
    public boolean canPause() {
        return false;
    }
}
