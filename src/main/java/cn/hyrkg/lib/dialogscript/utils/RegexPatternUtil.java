package cn.hyrkg.lib.dialogscript.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternUtil {
    public static final Pattern PATTERN_QUOTE_MULTI = Pattern.compile("\"([^\"]*)\"");
    public static final Pattern PATTERN_QUOTE = Pattern.compile("\"[^\"]+\"");
    public static final Pattern PATTERN_BRACE = Pattern.compile("\\{.+\\}");

    public static String checkAndGetQuoteContent(String input) {
        if (!PATTERN_QUOTE.matcher(input).matches()) {
            return null;
        }
        return input.substring(1, input.length() - 1);
    }

    public static List<String> getQuoteMultiContent(String input) {
        List<String> result = new ArrayList<>();
        Matcher matcher = PATTERN_QUOTE_MULTI.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }
}
