package cn.hyrkg.lib.dialogscript.extra;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;

/**
 * 用于自定义条件判断逻辑的接口
 */
public interface DialogCondition {
    /**
     * @param player 当前的播放器实例 (可以通过它访问 sharedParamMap 等)
     * @param scope  当前条件所在的作用域 (例如 "global", "local")
     * @param condition 条件名称 (例如 "has_item", "level_at_least")
     * @param params 条件参数 (例如 "key_item", "10")
     * @return true 表示条件满足，继续执行；false 表示条件不满足，跳过下一行
     */
    boolean check(BaseScriptPlayer player, String scope,String condition, String params);
}
