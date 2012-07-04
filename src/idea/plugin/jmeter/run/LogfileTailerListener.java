package idea.plugin.jmeter.run;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import idea.plugin.jmeter.run.tailer.TailerListenerAdapter;

class LogfileTailerListener extends TailerListenerAdapter {
    private final ConsoleView console;

    public LogfileTailerListener(ConsoleView console) {
        this.console = console;
    }

    @Override
    public void handle(String line) {
        console.print(line + '\n', ConsoleViewContentType.NORMAL_OUTPUT);
    }

    @Override
    public void handle(Exception ex) {
        console.print(ex.toString(), ConsoleViewContentType.ERROR_OUTPUT);
    }
}
