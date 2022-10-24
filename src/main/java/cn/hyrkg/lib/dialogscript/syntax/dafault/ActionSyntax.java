package cn.hyrkg.lib.dialogscript.syntax.dafault;

import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import lombok.Getter;

public class ActionSyntax implements IScriptSyntax {
    @Getter
    protected String action;

    @Override
    public void parser(String line) {
        action = line;
    }

}
