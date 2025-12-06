package cn.hyrkg.lib.dialogscript.syntax;

import lombok.Getter;

public class SleepSyntax implements ScriptSyntax {
    @Getter
    protected int sleepTimeInMs = 10;

    @Override
    public void parser(String line) {
        try {
            sleepTimeInMs = Integer.parseInt(line);
        } catch (Exception e) {
            throw new RuntimeException("wrong number " + line);
        }
    }
}
