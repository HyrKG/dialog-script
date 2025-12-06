package cn.hyrkg.lib.dialogscript.syntax;

import lombok.Getter;
import lombok.Setter;

public class ConditionSyntax implements ScriptSyntax {
    @Getter
    protected String scope;
    @Getter
    protected String condition;
    @Getter
    protected String params;

    @Getter
    @Setter
    protected int jumpIndexWhenFalse = -1;

    @Override
    public void parser(String line) {
        line = line.trim();
        String[] args = line.split("\\s+", 3);
        this.scope = args[0];
        this.condition = args.length > 1 ? args[1] : "";
        this.params = args.length > 2 ? args[2] : "";
    }
}
