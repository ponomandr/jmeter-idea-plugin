package idea.plugin.jmeter.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import idea.plugin.jmeter.run.tailer.Tailer;
import idea.plugin.jmeter.run.tailer.TailerListener;
import idea.plugin.jmeter.run.tailer.TailerListenerAdapter;

import java.io.File;

class JmeterProcessListener extends ProcessAdapter {

    private static final int CHECK_INTERVAL_MILLIS = 500;

    private final File logFile;
    private Tailer tailer;
    private JmeterLogParser parser;

    public JmeterProcessListener(JmeterConsoleView jmeterConsoleView, File logFile) {
        this.logFile = logFile;
        parser = new JmeterLogParser(jmeterConsoleView);
    }

    @Override
    public void startNotified(ProcessEvent event) {
        TailerListener listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                parser.parseLine(line);
            }
        };
        tailer = Tailer.create(logFile, listener, CHECK_INTERVAL_MILLIS);
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
