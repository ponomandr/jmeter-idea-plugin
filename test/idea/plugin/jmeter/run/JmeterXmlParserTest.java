package idea.plugin.jmeter.run;

import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JmeterXmlParserTest {

    private JmeterConsoleView consoleView = mock(JmeterConsoleView.class);

    @Test
    public void testParseSamplerData() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testResults version=\"1.2\">\n" +
                "<sample t=\"127\" lt=\"0\" ts=\"1341755332108\" s=\"true\" lb=\"Login as u1\" rc=\"200\" rm=\"OK\" tn=\"Thread Group 1-1\" dt=\"text\" by=\"15\">\n" +
                "  <samplerData class=\"java.lang.String\">Login as u1 with password p1</samplerData>\n" +
                "</sample>\n" +
                "</testResults>";
        JmeterXmlParser parser = new JmeterXmlParser(toInputStream(xml), consoleView);

        // When
        parser.parse();

        // Then
        ArgumentCaptor<SampleResult> argument = ArgumentCaptor.forClass(SampleResult.class);
        verify(consoleView).addSampleResult(argument.capture());

        assertThat(argument.getValue().getName(), is("Login as u1"));
        assertThat(argument.getValue().getSamplerData(), is("Login as u1 with password p1"));
        assertThat(argument.getValue().getResponseData(), nullValue());
    }


    @Test
    public void testParseHttpSampleWithFailedAssertion() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testResults version=\"1.2\">\n" +
                "<httpSample t=\"6973\" lt=\"6611\" ts=\"1341770750464\" s=\"false\" lb=\"HTTP Request\" rc=\"200\" rm=\"OK\" tn=\"Thread Group 1-1\" dt=\"text\" by=\"36433\">\n" +
                "  <assertionResult>\n" +
                "    <name>Response Assertion</name>\n" +
                "    <failure>true</failure>\n" +
                "    <error>false</error>\n" +
                "    <failureMessage>Test failed: text expected to contain /1Apache Software Foundation/</failureMessage>\n" +
                "  </assertionResult>\n" +
                "  <cookies class=\"java.lang.String\"></cookies>\n" +
                "  <method class=\"java.lang.String\">GET</method>\n" +
                "  <queryString class=\"java.lang.String\"></queryString>\n" +
                "</httpSample>\n" +
                "</testResults>";
        JmeterXmlParser parser = new JmeterXmlParser(toInputStream(xml), consoleView);

        // When
        parser.parse();

        // Then
        ArgumentCaptor<SampleResult> argument = ArgumentCaptor.forClass(SampleResult.class);
        verify(consoleView).addSampleResult(argument.capture());

        assertThat(argument.getValue().getName(), is("HTTP Request"));
        assertThat(argument.getValue().getAssertions().size(), is(1));

        Assertion assertion = argument.getValue().getAssertions().get(0);
        assertThat(assertion.getName(), is("Response Assertion"));
        assertThat(assertion.getState(), is(SampleResult.State.failed));
        assertThat(assertion.getFailureMessage(), is("Test failed: text expected to contain /1Apache Software Foundation/"));
    }

    @Test
    public void testParseHttpSampleWithSuccessfulAssertion() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testResults version=\"1.2\">\n" +
                "<httpSample t=\"6973\" lt=\"6611\" ts=\"1341770750464\" s=\"false\" lb=\"HTTP Request\" rc=\"200\" rm=\"OK\" tn=\"Thread Group 1-1\" dt=\"text\" by=\"36433\">\n" +
                "  <assertionResult>\n" +
                "    <name>Response Assertion 2</name>\n" +
                "    <failure>false</failure>\n" +
                "    <error>false</error>\n" +
                "  </assertionResult>\n" +
                "  <cookies class=\"java.lang.String\"></cookies>\n" +
                "  <method class=\"java.lang.String\">GET</method>\n" +
                "  <queryString class=\"java.lang.String\"></queryString>\n" +
                "</httpSample>\n" +
                "</testResults>";
        JmeterXmlParser parser = new JmeterXmlParser(toInputStream(xml), consoleView);

        // When
        parser.parse();

        // Then
        ArgumentCaptor<SampleResult> argument = ArgumentCaptor.forClass(SampleResult.class);
        verify(consoleView).addSampleResult(argument.capture());

        assertThat(argument.getValue().getName(), is("HTTP Request"));
        assertThat(argument.getValue().getAssertions().size(), is(1));

        Assertion assertion = argument.getValue().getAssertions().get(0);
        assertThat(assertion.getName(), is("Response Assertion 2"));
        assertThat(assertion.getState(), is(SampleResult.State.success));
        assertThat(assertion.getFailureMessage(), nullValue());
    }


    private InputStream toInputStream(String xml) {
        return new ByteArrayInputStream(xml.getBytes());
    }
}
