package cn.hyrkg.lib.dialogscript.syntax;

import lombok.Getter;

public class ActionSyntax implements ScriptSyntax {
    @Getter
    protected String action;

    @Override
    public void parser(String line) {
        action = line;
    }

}
