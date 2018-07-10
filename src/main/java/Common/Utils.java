package Common;

import org.apache.commons.lang.RandomStringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Utils {
    public static long formatFileSizeToByte(String formatSize) {
        formatSize = formatSize.toLowerCase();
        Matcher m = Pattern.compile("(\\d+)\\s*([a-z]*)").matcher(formatSize);

        if (!m.find())
            return -1;
        long num = Long.valueOf(m.group(1));
        String unit = m.group(2);

        double powNum = 0.0;
        if (unit != null) {
            unit = unit.trim();
            if (unit.length() > 0) {
                Character firstChar = unit.charAt(0);
                if (firstChar == 'k') {
                    powNum = 1.0;
                } else if (firstChar == 'm') {
                    powNum = 2.0;
                } else if (firstChar == 'g') {
                    powNum = 3.0;
                } else if (firstChar == 't') {
                    powNum = 4.0;
                }
            }
        }
        return (long) (num * (Math.pow(1024.0, powNum)));
    }

    public static String getFileNameWOPostfix(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return fileName.substring(dotIndex);
    }

}
