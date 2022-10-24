package cn.hyrkg.lib.dialogscript.test;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.parser.DialogScriptParser;
import cn.hyrkg.lib.dialogscript.player.test.ConsoleDialogPlayer;
import lombok.SneakyThrows;

import java.io.File;

public class TestScript {
    @SneakyThrows
    public static void main(String[] args) {
        File testFile = new File("test/test.ds.ini");
        DialogScriptParser dialogScriptParser = new DialogScriptParser();

        DialogScript script = dialogScriptParser.parserSingleScript(testFile);

        ConsoleDialogPlayer dialogPlayer = new ConsoleDialogPlayer();
        dialogPlayer.play(script);
    }
}
