package cn.hyrkg.lib.dialogscript.extra;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;

public class TmpDialogSetter implements DialogSetter {
    @Override
    public void set(BaseScriptPlayer player, String key, Object value) {
        player.setTmpValue(key, value);
    }
}

