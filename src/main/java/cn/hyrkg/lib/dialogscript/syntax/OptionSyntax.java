package cn.hyrkg.lib.dialogscript.syntax;

import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import com.google.common.base.Preconditions;

public class OptionSyntax implements ScriptSyntax {

    public static final OptionSyntax WAIT_FOR_CLICK = new OptionSyntax();

    static {
        WAIT_FOR_CLICK.message = "(继续)";
    }

    public String message = null;
    public String jumpTo = null;

    public boolean hasMessage() {
        return message != null;
    }

    @Override
    public void parser(String line) {
        String[] args = line.split("->");
        String rawMessage = args[0].trim();

        if (RegexPatternUtil.PATTERN_QUOTE.matcher(rawMessage).matches()) {
            message = rawMessage.substring(1, rawMessage.length() - 1);
        }

        if (args.length > 1) {
            String rawJumpSection = args[1].trim();
            Preconditions.checkState(RegexPatternUtil.PATTERN_BRACE.matcher(rawJumpSection).matches(), "missing section " + line);

            jumpTo = rawJumpSection.substring(1, rawJumpSection.length() - 1);
        }

    }
}
