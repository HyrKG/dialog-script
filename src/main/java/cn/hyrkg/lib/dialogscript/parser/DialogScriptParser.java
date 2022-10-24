package cn.hyrkg.lib.dialogscript.parser;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.*;
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
public class DialogScriptParser {

    @Getter
    protected HashMap<String, Class<? extends IScriptSyntax>> registeredSyntax = new HashMap<>();

    protected StateParser stateParser = new StateParser(this);

    public DialogScriptParser() {
        //初始化基础语法
        this.registerSyntax("@", ActionSyntax.class);
        this.registerSyntax("..", DialogSyntax.class);
        this.registerSyntax("->", OptionSyntax.class);
        this.registerSyntax(">", JumpSyntax.class);
        this.registerSyntax("sleep", SleepSyntax.class);
    }

    /**
     * 注册语法
     */
    public DialogScriptParser registerSyntax(String tag, Class<? extends IScriptSyntax> syntaxClass) {
        registeredSyntax.put(tag, syntaxClass);
        return this;
    }

    @SneakyThrows
    public IScriptSyntax createSyntax(String tag) {
        Preconditions.checkState(registeredSyntax.containsKey(tag), "undefined syntax " + tag);
        IScriptSyntax syntax = registeredSyntax.get(tag).newInstance();
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
