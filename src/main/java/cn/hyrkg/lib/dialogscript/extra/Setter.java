package cn.hyrkg.lib.dialogscript.extra;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;

public interface Setter {
    /**
     * @param player 当前播放器
     * @param key    变量名
     * @param value  变量值
     */
    void set(BaseScriptPlayer player, String key, Object value);
}

