package idea.plugin.jmeter.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SampleResult {

    public enum State {
        failed, error, success
    }

    private String name;
    private String threadName;
    private Date sampleStart;
    private String loadTime;
    private String samplerData;
    private String responseCode;
    private String responseMessage;
    private String responseData;
    private String responseHeader;
    private String requestHeader;
    private String method;
    private String url;
    private String cookies;
    private List<Assertion> assertions = new ArrayList<Assertion>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Date getSampleStart() {
        return sampleStart;
    }

    public void setSampleStart(Date sampleStart) {
        this.sampleStart = sampleStart;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getSamplerData() {
        return samplerData;
    }

    public void setSamplerData(String samplerData) {
        this.samplerData = samplerData;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
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
