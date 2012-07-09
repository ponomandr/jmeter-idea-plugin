package idea.plugin.jmeter.util;

import org.junit.Test;

import static idea.plugin.jmeter.util.XmlHack.decode;
import static idea.plugin.jmeter.util.XmlHack.encode;
import static org.junit.Assert.assertEquals;

public class XmlHackTest {

    @Test
    public void testEncoder() {
        assertEquals("\0", decode(encode("&#x0;")));
        assertEquals("\1", decode(encode("&#x1;")));
        assertEquals("\u000f", decode(encode("&#xf;")));
        assertEquals("test\0\1test", decode(encode("test&#x0;&#x1;test")));
        assertEquals("&\0&\1&", decode(encode("&&#x0;&&#x1;&")));
    }

}
