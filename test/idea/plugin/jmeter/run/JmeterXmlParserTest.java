package idea.plugin.jmeter.run;

import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JmeterXmlParserTest {

    private JmeterConsoleView consoleView = mock(JmeterConsoleView.class);

    @Test
    public void testParseSample() {
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
    public void testParseHttpSample() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testResults version=\"1.2\">\n" +
                "<httpSample t=\"6973\" lt=\"6611\" ts=\"1341770750464\" s=\"false\" lb=\"HTTP Request\" rc=\"200\" rm=\"OK\" tn=\"Thread Group 1-1\" dt=\"text\" by=\"36433\">\n" +
                "  <method class=\"java.lang.String\">GET</method>\n" +
                "  <requestHeader class=\"java.lang.String\">Connection: close\n" +
                "  </requestHeader>\n" +
                "  <cookies class=\"java.lang.String\"></cookies>\n" +
                "  <queryString class=\"java.lang.String\"></queryString>\n" +
                "  <java.net.URL>http://jmeter.apache.org/</java.net.URL>" +
                "</httpSample>\n" +
                "</testResults>";
        JmeterXmlParser parser = new JmeterXmlParser(toInputStream(xml), consoleView);

        // When
        parser.parse();

        // Then
        ArgumentCaptor<SampleResult> argument = ArgumentCaptor.forClass(SampleResult.class);
        verify(consoleView).addSampleResult(argument.capture());

        SampleResult result = argument.getValue();
        assertThat(result.getThreadName(), is("Thread Group 1-1"));
        assertThat(result.getLoadTime(), is("6611"));
        assertThat(result.getSampleStart(), is(new Date(1341770750464L)));
        assertThat(result.getResponseCode(), is("200"));
        assertThat(result.getResponseMessage(), is("OK"));
        assertThat(result.getMethod(), is("GET"));
        assertThat(result.getUrl(), is("http://jmeter.apache.org/"));
        assertThat(result.getCookies(), is(""));
        assertTrue(result.getRequestHeader().contains("Connection: close"));
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
    public void testParseHttpSampleWithResponseHeaders() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testResults version=\"1.2\">\n" +
                "<httpSample t=\"6973\" lt=\"6611\" ts=\"1341770750464\" s=\"false\" lb=\"HTTP Request\" rc=\"200\" rm=\"OK\" tn=\"Thread Group 1-1\" dt=\"text\" by=\"36433\">\n" +
                "  <responseHeader class=\"java.lang.String\">HTTP/1.1 200 OK\n" +
                "            Date: Thu, 12 Jul 2012 20:25:21 GMT\n" +
                "            Server: Apache/2.4.1 (Unix) OpenSSL/1.0.0g\n" +
                "            Last-Modified: Sun, 27 May 2012 21:39:19 GMT\n" +
                "            ETag: &quot;253f-4c10b6f108fc0&quot;\n" +
                "            Accept-Ranges: bytes\n" +
                "            Content-Length: 9535\n" +
                "            Vary: Accept-Encoding\n" +
                "            Connection: close\n" +
                "            Content-Type: text/html; charset=utf-8\n" +
                "  </responseHeader>\n" +
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
        assertTrue(argument.getValue().getResponseHeader().contains("Content-Length: 9535"));
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
