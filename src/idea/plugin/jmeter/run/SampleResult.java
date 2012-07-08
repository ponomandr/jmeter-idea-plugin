package idea.plugin.jmeter.run;

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
}
