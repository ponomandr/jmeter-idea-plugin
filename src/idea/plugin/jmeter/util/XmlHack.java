package idea.plugin.jmeter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * JMeter may produce JTL file which is invalid XML.
 * It happens when binary HTTP response is stored to JTL as an XML-escaped String.
 * This encoder is hack to solve this problem.
 * It replaces invalid characters like {@code &amp;#x0;} with a placeholder. So XML becomes valid.
 * When XML is parsed it replaces placeholders to appropriate characters.
 */
public class XmlHack {
    private static final String XML_NUMBER_REGEX = "&#x([0-9A-Fa-f]+);";
    private static final String NUMBER_PLACEHOLDER = "!!!_N_U_M_B_E_R_$1_!!!";
    public static final String NUMBER_REGEX = "!!!_N_U_M_B_E_R_([0-9A-Fa-f]+)_!!!";

    private XmlHack() {
    }

    public static String encode(String line) {
        return line.replaceAll(XML_NUMBER_REGEX, NUMBER_PLACEHOLDER);
    }

    public static String decode(CharSequence encodedText) {
        Pattern p = Pattern.compile(NUMBER_REGEX);

        StringBuilder decodedLine = new StringBuilder(encodedText);

        Matcher m = p.matcher(decodedLine);
        while (m.find()) {
            String number = m.group(1);
            int start = m.start(0);
            int len = NUMBER_PLACEHOLDER.length() - 2 + number.length();

            char ch = (char) Integer.parseInt(number, 16);
            decodedLine.replace(start, start + len, String.valueOf(ch));

            m = p.matcher(decodedLine); // TODO: optimize performance
        }

        return decodedLine.toString();
    }
}
