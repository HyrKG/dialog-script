package cn.hyrkg.lib.dialogscript.syntax.dafault;

import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

public class DialogSyntax implements IScriptSyntax {
    private static final Random RANDOM = new Random();

    private List<String> texts = new ArrayList<>();

    @Override
    public void parser(String line) {
        texts.addAll(RegexPatternUtil.getQuoteMultiContent(line));
    }

    public String getText() {
        if (texts.isEmpty()) {
            return "...";
        }
        return texts.get(RANDOM.nextInt(texts.size()));
    }
}
