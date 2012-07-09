package idea.plugin.jmeter.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.apache.commons.lang.StringUtils;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class Path {
    private Deque<String> path = new LinkedList<String>();

    public String pop() {
        return path.removeLast();
    }

    public Path push(String tag) {
        path.addLast(tag);
        return this;
    }

    public boolean is(String pathStr) {
        Preconditions.checkArgument(pathStr.startsWith("/"), "Path must start with /");

        pathStr = pathStr.substring(1);

        if (StringUtils.isBlank(pathStr)) {
            return this.path.isEmpty();
        }

        Iterator<String> expectedIterator = this.path.iterator();
        Iterator<String> actualIterator = Splitter.on("/").split(pathStr).iterator();

        while (actualIterator.hasNext() && expectedIterator.hasNext()) {
            String actual = actualIterator.next();
            String expected = expectedIterator.next();

            if (!actual.equals("*") && !actual.equals(expected)) {
                return false;
            }
        }

        return !actualIterator.hasNext() && !expectedIterator.hasNext();
    }

}
