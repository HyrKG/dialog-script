package cn.hyrkg.lib.dialogscript;

import cn.hyrkg.lib.dialogscript.extra.Condition;
import cn.hyrkg.lib.dialogscript.extra.Setter;
import cn.hyrkg.lib.dialogscript.extra.TmpCondition;
import cn.hyrkg.lib.dialogscript.extra.TmpSetter;
import cn.hyrkg.lib.dialogscript.factory.StateParser;
import cn.hyrkg.lib.dialogscript.syntax.*;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 此类会把所有的语法加载，并交给状态处理器解析
 */
public class DialogScriptManager {

    @Getter
    protected HashMap<String, Class<? extends ScriptSyntax>> registeredSyntax = new HashMap<>();
    @Getter
    protected HashMap<String, Setter> registeredSetters = new HashMap<>();
    @Getter
    protected HashMap<String, Condition> registeredConditions = new HashMap<>();

    protected StateParser stateParser = new StateParser(this);

    public DialogScriptManager() {
        //初始化基础语法
        this.registerSyntax("@", ActionSyntax.class);
        this.registerSyntax("..", DialogSyntax.class);
        this.registerSyntax("->", OptionSyntax.class);
        this.registerSyntax(">", JumpSyntax.class);
        this.registerSyntax("set", SetSyntax.class);
        this.registerSyntax("sleep", SleepSyntax.class);

        this.registerConditionHandler("tmp", new TmpCondition());
        this.registerSetterHandler("tmp", new TmpSetter());
    }

    /**
     * 注册语法
     */
    public DialogScriptManager registerSyntax(String tag, Class<? extends ScriptSyntax> syntaxClass) {
        registeredSyntax.put(tag, syntaxClass);
        return this;
    }

    public DialogScriptManager registerConditionHandler(String type, Condition handler) {
        registeredConditions.put(type, handler);
        return this;
    }

    public DialogScriptManager registerSetterHandler(String scope, cn.hyrkg.lib.dialogscript.extra.Setter handler) {
        registeredSetters.put(scope, handler);
        return this;
    }


    @SneakyThrows
    public ScriptSyntax createSyntax(String tag) {
        Preconditions.checkState(registeredSyntax.containsKey(tag), "undefined syntax " + tag);
        ScriptSyntax syntax = registeredSyntax.get(tag).newInstance();
        return syntax;
    }


    public HashMap<String, DialogScript> parser(@Nonnull File file) throws IOException {
        Preconditions.checkNotNull(file, "file cannot be null");
        Preconditions.checkState(file.exists(), "file is not exists");
        return parser(Files.readAllLines(file.toPath()));
    }

    public HashMap<String, DialogScript> parser(@Nonnull String str) {
        Preconditions.checkNotNull(str, "str cannot be null");

        String[] args = str.split("\\n");
        List<String> strList = new ArrayList<>();
        strList.addAll(Arrays.asList(args));
        return parser(strList);
    }


    public HashMap<String, DialogScript> parser(List<String> strList) {
        Preconditions.checkNotNull(strList, "list cannot be null");
        stateParser.parser(strList);
        return stateParser.produceMap;
    }

    public DialogScript parserSingleScript(File file) throws IOException {
        return parser(file).get(StateParser.DEFAULT_SCRIPT_KEY);
    }
}
