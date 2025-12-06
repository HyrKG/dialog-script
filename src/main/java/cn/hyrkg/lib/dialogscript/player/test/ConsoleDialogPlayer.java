package cn.hyrkg.lib.dialogscript.player.test;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;
import cn.hyrkg.lib.dialogscript.player.annotation.ListenSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.ActionSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.DialogSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.OptionSyntax;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ConsoleDialogPlayer extends BaseScriptPlayer {

    private HashMap<Integer, OptionSyntax> currentOptions = new HashMap<>();

    public ConsoleDialogPlayer() {
        useAnnotationFramework();
    }


    @ListenSyntax
    public void dialog(DialogSyntax dialogSyntax) {
        System.out.println("'" + dialogSyntax.getText() + "'");

    }

    @ListenSyntax
    public void action(ActionSyntax actionSyntax) {
        System.out.println("触发行动" + actionSyntax.getAction());
    }

    @Override
    public void playOptions(List<OptionSyntax> options) {
        System.out.println("");
        int index = 0;
        for (OptionSyntax syntax : options) {
            currentOptions.put(index++, syntax);
            System.out.println(index + ". " + syntax.message);
        }


        Scanner scanner = new Scanner(System.in);
        System.out.print("输入你的选项: ");
        int result = -1;
        while (result == -1) {
            try {
                result = Integer.parseInt(scanner.next());
                if (!currentOptions.containsKey(result - 1))
                    throw new Exception();
            } catch (Exception e) {
                result = -1;
                System.out.print("输入有误,请重新输入: ");
            }
        }
        System.out.println("");
        if (currentOptions.get(result - 1).jumpTo != null)
            jumpSection(currentOptions.get(result - 1).jumpTo);

    }

    @Override
    public void onFinish() {

    }

}
