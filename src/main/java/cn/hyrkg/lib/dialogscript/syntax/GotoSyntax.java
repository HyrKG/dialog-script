package cn.hyrkg.lib.dialogscript.syntax;

import lombok.Getter;
import lombok.Setter;

public class GotoSyntax implements ScriptSyntax {
    
    @Getter
    @Setter
    protected int targetIndex = -1;

    @Override
    public void parser(String line) {
        // Goto is usually generated internally, but if parsed from file:
        try {
            targetIndex = Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            // ignore
        }
    }
}

