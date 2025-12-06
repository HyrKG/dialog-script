package cn.hyrkg.lib.dialogscript.syntax;

import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DialogSyntax implements ScriptSyntax {
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
