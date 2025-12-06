package cn.hyrkg.lib.dialogscript.extra;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;

/**
 * 用于自定义条件判断逻辑的接口
 */
public interface Condition {
    /**
     * @param player 当前的播放器实例 (可以通过它访问 sharedParamMap 等)
     * @param type   条件类型 (例如 "has", "level_gt")
     * @param params 条件参数 (例如 "key_item", "10")
     * @return true 表示条件满足，继续执行；false 表示条件不满足，跳过下一行
     */
    boolean check(BaseScriptPlayer player, String type, String params);
}
