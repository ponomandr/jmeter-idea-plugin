package idea.plugin.jmeter.domain;

public class Assertion {
    private String name;
    private boolean failure;
    private boolean error;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public SampleResult.State getState() {
        if (failure) {
            return SampleResult.State.failed;
        }
        if (error) {
            return SampleResult.State.error;
        }
        return SampleResult.State.success;
    }
}
