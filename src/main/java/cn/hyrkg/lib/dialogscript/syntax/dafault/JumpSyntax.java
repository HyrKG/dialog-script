package cn.hyrkg.lib.dialogscript.syntax.dafault;

import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import com.google.common.base.Preconditions;
import lombok.Getter;

public class JumpSyntax implements IScriptSyntax {

    @Getter
    protected String section = null;

    @Override
    public void parser(String line) {
        Preconditions.checkState(RegexPatternUtil.PATTERN_BRACE.matcher(line).matches(), "wrong section " + line);
        String section = line.substring(1, line.length() - 1);
        this.section = section;
    }
}
