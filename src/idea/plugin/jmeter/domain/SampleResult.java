package idea.plugin.jmeter.domain;

import java.util.ArrayList;
import java.util.List;

public class SampleResult {

    public enum State {
        failed, error, success
    }

    private String name;
    private String samplerData;
    private String responseData;
    private String responseHeader;
    private String requestHeader;
    private List<Assertion> assertions = new ArrayList<Assertion>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSamplerData() {
        return samplerData;
    }

    public void setSamplerData(String samplerData) {
        this.samplerData = samplerData;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public void addAssertion(Assertion assertion) {
        assertions.add(assertion);
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
}
