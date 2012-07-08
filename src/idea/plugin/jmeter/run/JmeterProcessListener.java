package idea.plugin.jmeter.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import idea.plugin.jmeter.run.tailer.Tailer;
import idea.plugin.jmeter.run.tailer.TailerListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class JmeterProcessListener extends ProcessAdapter {

    private static final int CHECK_INTERVAL_MILLIS = 500;

    private final File logFile;
    private final PipedOutputStream outputStream;
    private final JmeterConsoleView jmeterConsoleView;
    private final PipedInputStream inputStream;

    private Tailer tailer;

    public JmeterProcessListener(File logFile, JmeterConsoleView jmeterConsoleView) {
        try {
            this.logFile = logFile;
            this.jmeterConsoleView = jmeterConsoleView;
            outputStream = new PipedOutputStream();
            inputStream = new PipedInputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startNotified(ProcessEvent event) {
        new ParserThread().start();
        tailer = Tailer.create(logFile, new MyTailerListenerAdapter(), CHECK_INTERVAL_MILLIS);
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
        // Give tailer a chance to finish its job
        sleep(tailer.getDelay() * 3 / 2);
        tailer.stop();
        Tailer.closeQuietly(outputStream);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }


    private class ParserThread extends Thread {
        public ParserThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            new JmeterXmlParser(inputStream, jmeterConsoleView).parse();
        }
    }

    private class MyTailerListenerAdapter extends TailerListenerAdapter {
        @Override
        public void handle(String line) {
            try {
                outputStream.write(line.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
