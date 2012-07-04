package idea.plugin.jmeter.run;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import idea.plugin.jmeter.run.tailer.TailerListenerAdapter;

import java.util.ArrayList;
import java.util.List;

class LogfileTailerListener extends TailerListenerAdapter {

    private enum State {inTestResults, inSample, inAssertion}

    private final ConsoleView console;

    private State state = State.inTestResults;
    private String sampleName;
    private Assertion assertion;
    private List<Assertion> failedAssertions;


    public LogfileTailerListener(ConsoleView console) {
        this.console = console;
    }

    @Override
    public void handle(String line) {
        if (state == State.inTestResults && line.contains("lb=")) {
            state = State.inSample;
            sampleName = extractSampleName(line);
            failedAssertions = new ArrayList<Assertion>();
            assertion = new Assertion();
        }

        if (state == State.inSample && line.contains("<assertionResult>")) {
            state = State.inAssertion;
        }

        if (state == State.inAssertion && line.contains("<name>")) {
            assertion.name = extractAssertionName(line);
        }

        if (state == State.inAssertion && line.contains("<failure>")) {
            assertion.failure = extractAssertionFailure(line);
        }

        if (state == State.inAssertion && line.contains("<error>")) {
            assertion.error = extractAssertionError(line);
        }

        if (state == State.inAssertion && line.contains("</assertionResult>")) {
            state = State.inSample;
            if (assertion.error || assertion.failure) {
                failedAssertions.add(assertion);
            }
        }

        if (state == State.inSample && line.contains("</")) {
            state = State.inTestResults;
            printSampleResult();
        }
    }

    private void printSampleResult() {
        if (failedAssertions.isEmpty()) {
            console.print(sampleName + " - OK\n", ConsoleViewContentType.USER_INPUT);
        } else {
            console.print(sampleName + " - FAILED\n", ConsoleViewContentType.ERROR_OUTPUT);
            for (Assertion assertion : failedAssertions) {
                String type = (assertion.error ? "ERROR" : "FAILURE");
                console.print("    " + assertion.name + " - " + type + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            }
        }
    }

    @Override
    public void handle(Exception ex) {
        console.print(ex.toString(), ConsoleViewContentType.ERROR_OUTPUT);
    }

    private String extractSampleName(String line) {
        int start = line.indexOf("lb=\"") + "lb=\"".length();
        int end = line.indexOf('"', start + 1);
        return line.substring(start, end);
    }

    private String extractAssertionName(String line) {
        return extractTagBody(line, "name");
    }

    private boolean extractAssertionFailure(String line) {
        return Boolean.valueOf(extractTagBody(line, "failure"));
    }

    private boolean extractAssertionError(String line) {
        return Boolean.valueOf(extractTagBody(line, "error"));
    }

    private String extractTagBody(String line, String name) {
        String tag = "<" + name + ">";
        int start = line.indexOf(tag) + tag.length();
        int end = line.indexOf("</" + name + ">", start + 1);
        return line.substring(start, end);
    }

    private static class Assertion {
        String name;
        boolean failure;
        boolean error;
    }
}
