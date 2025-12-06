package cn.hyrkg.lib.dialogscript.extra;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;

public interface DialogGetter {
    Object get(BaseScriptPlayer player, String key);
}
