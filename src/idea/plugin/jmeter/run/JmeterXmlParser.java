package idea.plugin.jmeter.run;

import com.google.common.base.Preconditions;
import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;
import idea.plugin.jmeter.util.Path;
import idea.plugin.jmeter.util.XmlHack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class JmeterXmlParser {

    private final InputStream inputStream;
    private final JmeterConsoleView console;

    private SampleResult sampleResult;
    private Assertion assertion;
    private StringBuilder bodyBuilder;
    private Path path = new Path();

    public JmeterXmlParser(InputStream inputStream, JmeterConsoleView console) {
        this.inputStream = inputStream;
        this.console = console;
    }

    public void parse() {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(inputStream, new MyHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class MyHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            bodyBuilder = new StringBuilder();
            path.push(qName);

            if (path.is("/testResults/sample") || path.is("/testResults/httpSample")) {
                sampleResult = new SampleResult();
                sampleResult.setName(attributes.getValue("lb"));
            }

            if (path.is("/testResults/*/assertionResult")) {
                assertion = new Assertion();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            bodyBuilder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String tagBody = XmlHack.decode(bodyBuilder);

            if (path.is("/testResults/sample")) {
                console.addSampleResult(sampleResult);
            }

            if (path.is("/testResults/httpSample")) {
                console.addSampleResult(sampleResult);
            }

            if (path.is("/testResults/*/assertionResult")) {
                sampleResult.addAssertion(assertion);
            }

            if (path.is("/testResults/*/samplerData")) {
                sampleResult.setSamplerData(tagBody);
            }

            if (path.is("/testResults/*/responseHeader")) {
                sampleResult.setResponseHeader(tagBody);
            }

            if (path.is("/testResults/*/responseData")) {
                sampleResult.setResponseData(tagBody);
            }

            if (path.is("/testResults/*/assertionResult/name")) {
                assertion.setName(tagBody);
            }

            if (path.is("/testResults/*/assertionResult/failure")) {
                assertion.setFailure(Boolean.valueOf(tagBody));
            }

            if (path.is("/testResults/*/assertionResult/error")) {
                assertion.setError(Boolean.valueOf(tagBody));
            }

            if (path.is("/testResults/*/assertionResult/failureMessage")) {
                assertion.setFailureMessage(tagBody);
            }

            String remove = path.pop();
            Preconditions.checkState(qName.equals(remove));
        }
    }


    /*
        writer.addAttribute("t", Long.toString(res.getTime()));
        writer.addAttribute("it", Long.toString(res.getIdleTime()));
        writer.addAttribute("lt", Long.toString(res.getLatency()));
        writer.addAttribute("ts", Long.toString(res.getTimeStamp()));
        writer.addAttribute("s", Boolean.toString(res.isSuccessful()));
        writer.addAttribute("lb", ConversionHelp.encode(res.getSampleLabel()));
        writer.addAttribute("rc", ConversionHelp.encode(res.getResponseCode()));
        writer.addAttribute("rm", ConversionHelp.encode(res.getResponseMessage()));
        writer.addAttribute("tn", ConversionHelp.encode(res.getThreadName()));
        writer.addAttribute("dt", ConversionHelp.encode(res.getDataType()));
        writer.addAttribute("de", ConversionHelp.encode(res.getDataEncodingNoDefault()));
        writer.addAttribute("by", String.valueOf(res.getBytes()));
        writer.addAttribute("sc", String.valueOf(res.getSampleCount()));
        writer.addAttribute("ec", String.valueOf(res.getErrorCount()));
        writer.addAttribute("ng", String.valueOf(res.getGroupThreads()));
        writer.addAttribute("na", String.valueOf(res.getAllThreads()));
        writer.addAttribute("hn", event.getHostname());

        for (int i = 0; i < SampleEvent.getVarCount(); i++){
            writer.addAttribute(SampleEvent.getVarName(i), ConversionHelp.encode(event.getVarValue(i)));
        }
    }
*/

}
