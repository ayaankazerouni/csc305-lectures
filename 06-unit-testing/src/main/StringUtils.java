package main;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    /**
     * Example code from Apache commons-lang main.StringUtils library.
     * (<a href="https://github.com/apache/commons-lang/blob/e0b474c0d/src/main/java/org/apache/commons/lang3/StringUtils.java#L8810">...</a>)
     *
     * <p>Searches a String for substrings delimited by a start and an end tag,
     * returning all matching substrings in an array.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open/close returns {@code null} (no match).</p>
     *
     * @param str the String containing the substrings, null returns null, empty returns empty
     * @param open the String identifying the start of the substring, empty returns null
     * @param close the String identifying the end of the substring, empty returns null
     * @return a String Array of substrings, or {@code null} if no match
     */
    public static String[] substringsBetween(final String str, final String open, final String close) {
        if (str == null || open.isEmpty() || close.isEmpty()) {
            return null;
        }

        int strLen = str.length();
        if (strLen == 0) {
            return new String[0];
        }

        int closeLen = close.length();
        int openLen = open.length();
        List<String> list = new ArrayList<>();
        int pos = 0;

        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);

            if (start < 0) {
                break;
            }

            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }

            list.add(str.substring(start, end));
            pos = end + closeLen;
        }

        if (list.isEmpty()) {
            return null;
        }

        return list.toArray(new String[0]);
    }
}
