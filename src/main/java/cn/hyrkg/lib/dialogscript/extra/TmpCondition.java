package cn.hyrkg.lib.dialogscript.extra;

import cn.hyrkg.lib.dialogscript.player.BaseScriptPlayer;

public class TmpCondition implements Condition {
    @Override
    public boolean check(BaseScriptPlayer player, String type, String params) {
        // 对于 'tmp' 处理器，type 就是 "tmp"，params 是 "key value" 或者 "key"
        // 我们需要解析 params

        if (params == null || params.isEmpty()) {
            return false; // if tmp (空) -> false
        }

        String key;
        String expectedValue = null;

        String[] parts = params.split("\\s+", 2);
        key = parts[0];
        if (parts.length > 1) {
            expectedValue = parts[1];
        }

        // 开始检查
        if (expectedValue == null) {
            // if tmp key
            // 仅判断变量是否存在且为 true
            Object val = player.getTmpValue(key);
            if (val instanceof Boolean) {
                return (Boolean) val;
            }
            return val != null;
        } else {
            // if tmp key value
            // 判断相等
            Object val = player.getTmpValue(key);
            if (val == null) {
                return "null".equals(expectedValue);
            }
            // 简单转字符串比较
            return String.valueOf(val).equals(expectedValue);
        }
    }
}

