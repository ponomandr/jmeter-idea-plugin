package idea.plugin.jmeter.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import idea.plugin.jmeter.run.tailer.Tailer;
import idea.plugin.jmeter.run.tailer.TailerListener;
import idea.plugin.jmeter.run.tailer.TailerListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

class JmeterProcessListener extends ProcessAdapter {

    private static final int CHECK_INTERVAL_MILLIS = 500;

    private final File logFile;
    private final OutputStream outputStream;
    private Tailer tailer;

    public JmeterProcessListener(File logFile, OutputStream outputStream) {
        this.logFile = logFile;
        this.outputStream = outputStream;
    }

    @Override
    public void startNotified(ProcessEvent event) {
        TailerListener listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                try {
                    outputStream.write(line.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        tailer = Tailer.create(logFile, listener, CHECK_INTERVAL_MILLIS);
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
        // Give tailer a chance to finish its job
        sleep(tailer.getDelay() * 3 / 2);
        tailer.stop();
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }


}
