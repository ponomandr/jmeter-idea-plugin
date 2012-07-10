package idea.plugin.jmeter.run;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import idea.plugin.jmeter.save.AssertionResult;
import idea.plugin.jmeter.save.HTTPSampleResult;
import idea.plugin.jmeter.save.SampleResult;
import idea.plugin.jmeter.save.TestResultWrapper;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class XstreamTest {

    private static Map<String, Class> aliasToClass = new HashMap<String, Class>() {{
//        put("hashTree", org.apache.jorphan.collections.ListedHashTree.class);
//        put("jmeterTestPlan", org.apache.jmeter.save.ScriptWrapper.class);
        put("sample", SampleResult.class);
        put("httpSample", HTTPSampleResult.class);
//        put("statSample", org.apache.jmeter.samplers.StatisticalSampleResult.class);
        put("testResults", TestResultWrapper.class);
        put("assertionResult", AssertionResult.class);
//        put("monitorStats", org.apache.jmeter.visualizers.MonitorStats.class);
//        put("sampleEvent", org.apache.jmeter.samplers.SampleEvent.class);
    }};

    private static Map<String, String> classToAlias = new HashMap<String, String>() {{
        put("org.apache.jorphan.collections.ListedHashTree", "hashTree");
        put("org.apache.jmeter.save.ScriptWrapper", "jmeterTestPlan");
        put("org.apache.jmeter.samplers.SampleResult", "sample");
        put("org.apache.jmeter.protocol.http.sampler.HTTPSampleResult", "httpSample");
        put("org.apache.jmeter.samplers.StatisticalSampleResult", "statSample");
        put("org.apache.jmeter.save.TestResultWrapper", "testResults");
        put("org.apache.jmeter.assertions.AssertionResult", "assertionResult");
        put("org.apache.jmeter.visualizers.MonitorStats", "monitorStats");
        put("org.apache.jmeter.samplers.SampleEvent", "sampleEvent");
    }};

    @Test
    public void test() throws FileNotFoundException {
        XStream JTLSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
        Object x = JTLSAVER.fromXML(new FileInputStream("test/sample.jtl"));
        assertNotNull(x);
    }


    private static final class XStreamWrapper extends XStream {
        private XStreamWrapper(ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        // Override wrapMapper in order to insert the Wrapper in the chain
        @Override
        protected MapperWrapper wrapMapper(MapperWrapper next) {
            // Provide our own aliasing using strings rather than classes
            return new MapperWrapper(next) {
                // Translate alias to classname and then delegate to wrapped class
                @Override
                public Class<?> realClass(String alias) {
                    String fullName = aliasToClass(alias);
                    if (fullName != null) {
//                        fullName = NameUpdater.getCurrentName(fullName);
                    }
                    return super.realClass(fullName == null ? alias : fullName);
                }

                // Translate to alias and then delegate to wrapped class
                @Override
                public String serializedClass(@SuppressWarnings("rawtypes") // superclass does not use types
                                                      Class type) {
                    if (type == null) {
                        return super.serializedClass(null); // was type, but that caused FindBugs warning
                    }
                    String alias = classToAlias(type.getName());
                    return alias == null ? super.serializedClass(type) : alias;
                }
            };
        }
    }

    public static String aliasToClass(String s) {
        Class r = aliasToClass.get(s);
        return r == null ? s : r.getName();
    }

    // For converters to use
    public static String classToAlias(String s) {
        String r = classToAlias.get(s);
        return r == null ? s : r;
    }


}
