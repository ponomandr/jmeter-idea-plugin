package idea.plugin.jmeter.run;

public class TestResult {
    public enum State {
        success, failed, error
    }

    private final String sampleName;
    private final State state;

    public TestResult(String sampleName, State state) {
        this.sampleName = sampleName;
        this.state = state;
    }

    public String simpleName() {
        return sampleName;
    }

    public State state() {
        return state;
    }
}
