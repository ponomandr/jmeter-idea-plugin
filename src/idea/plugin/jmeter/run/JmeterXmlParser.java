package idea.plugin.jmeter.run;

import com.google.common.base.Preconditions;
import idea.plugin.jmeter.domain.Assertion;
import idea.plugin.jmeter.domain.SampleResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;

public class JmeterXmlParser {

    private final InputStream inputStream;
    private final JmeterConsoleView console;

    private SampleResult sampleResult;
    private Assertion assertion;
    private StringBuilder bodyBuilder;
    private Deque<String> path = new LinkedList<String>();

    public JmeterXmlParser(InputStream inputStream, JmeterConsoleView console) {
        this.inputStream = inputStream;
        this.console = console;
    }

    public void parse() {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(inputStream, new MyDefaultHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class MyDefaultHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            bodyBuilder = new StringBuilder();
            path.addLast(qName);

            if ("sample".equals(qName) || "httpSample".equals(qName)) {
                sampleResult = new SampleResult();
                sampleResult.setName(attributes.getValue("lb"));
            }

            if ("assertionResult".equals(qName)) {
                assertion = new Assertion();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            bodyBuilder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String remove = path.removeLast();
            Preconditions.checkState(qName.equals(remove));

            if ("sample".equals(qName) || "httpSample".equals(qName)) {
                console.addSampleResult(sampleResult);
            }

            if ("assertionResult".equals(qName)) {
                sampleResult.addAssertion(assertion);
            }

            if ("samplerData".equals(qName)) {
                sampleResult.setSamplerData(bodyBuilder.toString());
            }

            if ("responseData".equals(qName)) {
                sampleResult.setResponseData(bodyBuilder.toString());
            }

            if ("name".equals(qName) && "assertionResult".equals(path.getLast())) {
                assertion.setName(bodyBuilder.toString());
            }

            if ("failure".equals(qName) && "assertionResult".equals(path.getLast())) {
                assertion.setFailure(Boolean.valueOf(bodyBuilder.toString()));
            }

            if ("error".equals(qName) && "assertionResult".equals(path.getLast())) {
                assertion.setError(Boolean.valueOf(bodyBuilder.toString()));
            }

            if ("failureMessage".equals(qName) && "assertionResult".equals(path.getLast())) {
                assertion.setFailureMessage(bodyBuilder.toString());
            }
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
