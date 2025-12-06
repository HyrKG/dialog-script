package cn.hyrkg.lib.dialogscript.syntax;

import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import lombok.Getter;

public class SetSyntax implements ScriptSyntax {
    @Getter
    protected String scope; // 新增 scope 字段，例如 "tmp"
    @Getter
    protected String key;
    @Getter
    protected String valueRaw;
    @Getter
    protected Object value;

    @Override
    public void parser(String line) {
        line = line.trim();
        // 格式: set scope key value  或者  set scope key (默认true)
        // 旧格式不再支持: set key value
        
        String[] parts = line.split("\\s+", 3); // 最多分割成3部分: scope, key, [value]
        
        if (parts.length >= 2) {
            scope = parts[0];
            key = parts[1];
            
            if (parts.length == 3) {
                valueRaw = parts[2];
                value = parseValue(valueRaw);
            } else {
                valueRaw = "true";
                value = true;
            }
        } else {
            // 格式错误，或者只有 scope? 暂不处理，或者抛错
            // 假设至少要有 scope 和 key
            scope = "tmp"; // Fallback? 或者抛异常
            key = line;
            valueRaw = "true";
            value = true;
        }
    }

    private Object parseValue(String raw) {
        if (RegexPatternUtil.PATTERN_QUOTE.matcher(raw).matches()) {
            return raw.substring(1, raw.length() - 1);
        }
        if ("true".equalsIgnoreCase(raw)) return true;
        if ("false".equalsIgnoreCase(raw)) return false;
        
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            // ignore
        }
        
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            // ignore
        }

        return raw;
    }
}

