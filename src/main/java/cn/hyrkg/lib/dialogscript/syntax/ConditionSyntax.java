package cn.hyrkg.lib.dialogscript.syntax;

import lombok.Getter;
import lombok.Setter;

public class ConditionSyntax implements ScriptSyntax {
    @Getter
    protected String type;
    @Getter
    protected String params;
    
    @Getter
    @Setter
    protected int jumpIndexWhenFalse = -1;

    @Override
    public void parser(String line) {
        line = line.trim();
        int firstSpace = line.indexOf(' ');
        if (firstSpace == -1) {
            type = line;
            params = "";
        } else {
            type = line.substring(0, firstSpace);
            params = line.substring(firstSpace + 1).trim();
        }
    }
}
