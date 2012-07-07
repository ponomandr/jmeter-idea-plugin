package idea.plugin.jmeter.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import idea.plugin.jmeter.run.tailer.Tailer;

import java.io.File;

class JmeterProcessListener extends ProcessAdapter {

    private static final int CHECK_INTERVAL_MILLIS = 500;

    private final JmeterConsoleView jmeterConsoleView;
    private final File logFile;
    private Tailer tailer;

    public JmeterProcessListener(JmeterConsoleView jmeterConsoleView, File logFile) {
        this.jmeterConsoleView = jmeterConsoleView;
        this.logFile = logFile;
    }

    @Override
    public void startNotified(ProcessEvent event) {
        tailer = Tailer.create(logFile, new LogfileTailerListener(jmeterConsoleView), CHECK_INTERVAL_MILLIS);
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
        // Give tailer a chance to finish its job
        sleep(tailer.getDelay() * 3 / 2);
        tailer.stop();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }


}
