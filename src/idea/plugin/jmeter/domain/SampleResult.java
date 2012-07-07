package idea.plugin.jmeter.domain;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SampleResult {
    private final String name;
    private final String samplerData;
    private final String responseData;
    private final List<Assertion> assertions;

    public SampleResult(String sampleName, @Nullable String samplerData, @Nullable String responseData, List<Assertion> assertions) {
        this.name = sampleName;
        this.samplerData = samplerData;
        this.responseData = responseData;
        this.assertions = assertions;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getSamplerData() {
        return samplerData;
    }

    @Nullable
    public String getResponseData() {
        return responseData;
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
