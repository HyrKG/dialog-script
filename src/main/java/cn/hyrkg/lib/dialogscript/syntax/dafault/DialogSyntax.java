package cn.hyrkg.lib.dialogscript.syntax.dafault;

import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import com.google.common.base.Preconditions;

import java.util.regex.Matcher;

public class DialogSyntax implements IScriptSyntax {

    public String text = "...";

    @Override
    public void parser(String line) {
        Matcher matcher = RegexPatternUtil.PATTERN_QUOTE.matcher(line);
        Preconditions.checkState(matcher.matches(), "wrong syntax " + line);
        String text = matcher.group().substring(1, matcher.group().length() - 1);
        this.text = text;
    }
}
