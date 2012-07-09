package idea.plugin.jmeter.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathTest {
    @Test
    public void testPathComparison() throws Exception {
        assertTrue(new Path().is("/"));
        assertTrue(new Path().push("root").is("/root"));
        assertTrue(new Path().push("root").is("/*"));
        assertTrue(new Path().push("root").push("child").is("/root/child"));
        assertTrue(new Path().push("root").push("child").is("/root/*"));
        assertTrue(new Path().push("root").push("child").is("/*/child"));
        assertTrue(new Path().push("root").push("child").is("/*/*"));

        assertFalse(new Path().is("/root"));
        assertFalse(new Path().push("root").is("/"));
        assertFalse(new Path().push("root").push("child").is("/*"));
    }
}
