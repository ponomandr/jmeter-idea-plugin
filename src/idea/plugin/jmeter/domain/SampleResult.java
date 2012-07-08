package idea.plugin.jmeter.domain;

import java.util.List;

public class SampleResult {
    private final String name;
    private final List<Assertion> assertions;

    public SampleResult(String name, List<Assertion> assertions) {
        this.name = name;
        this.assertions = assertions;
    }

    public String getName() {
        return name;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public State getState() {
        boolean hasFailures = false;
        for (Assertion assertion : assertions) {
            if (assertion.getState() == State.error) {
                return State.error;
            }
            hasFailures |= (assertion.getState() == State.failed);
        }
        return hasFailures ? State.failed : State.success;
    }

    public enum State {
        failed, error, success
    }
}
