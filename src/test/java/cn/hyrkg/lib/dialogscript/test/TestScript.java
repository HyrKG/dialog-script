package cn.hyrkg.lib.dialogscript.test;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.DialogScriptManager;
import lombok.SneakyThrows;

import java.io.File;

public class TestScript {
    @SneakyThrows
    public static void main(String[] args) {
        File testFile = new File("demo.ds");
        System.out.println(testFile.getAbsolutePath());
        DialogScriptManager dialogScriptManager = new DialogScriptManager();

        DialogScript script = dialogScriptManager.parserSingleScript(testFile);

        ConsoleDialogPlayer dialogPlayer = new ConsoleDialogPlayer(dialogScriptManager);
        dialogPlayer.play(script);
    }
}
