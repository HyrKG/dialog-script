package cn.hyrkg.lib.dialogscript.player;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.DialogSection;
import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;

public interface IScriptPlayer {
    default void play(DialogScript script) {
        handleSection(script.getEntrySection());
    }

    void handleSyntax(IScriptSyntax syntax);

    void handleSection(DialogSection section);
}
