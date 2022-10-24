package cn.hyrkg.lib.dialogscript.utils;

import java.util.regex.Pattern;

public class RegexPatternUtil {
    public static final Pattern PATTERN_QUOTE = Pattern.compile("\"[^\"]+\"");
    public static final Pattern PATTERN_BRACE = Pattern.compile("\\{.+\\}");

    public static String checkAndGetQuoteContent(String input) {
        if (!PATTERN_QUOTE.matcher(input).matches()) {
            return null;
        }
        return input.substring(1, input.length() - 1);
    }
}
