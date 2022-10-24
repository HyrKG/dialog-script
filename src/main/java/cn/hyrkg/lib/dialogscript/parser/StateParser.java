package cn.hyrkg.lib.dialogscript.parser;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.DialogSection;
import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 该类主要复杂将一行行的脚本具体解读为脚本，并且将其返回
 */
public class StateParser {

    public static final String DEFAULT_SCRIPT_KEY = "default";

    public DialogScriptParser creator;

    public String scriptName = null;
    public DialogScript script = null;
    public DialogSection workingSection = null;

    public HashMap<String, DialogScript> produceMap = new HashMap<>();

    public StateParser(DialogScriptParser creator) {
        this.creator = creator;
        switchNewScrip(DEFAULT_SCRIPT_KEY);
    }

    public void switchNewScrip(String newScripName) {
        //FIXME 逻辑不完善
        if (scriptName != null && script != null && script.isEmpty()) {
            produceMap.put(scriptName, script);
        }
        scriptName = newScripName;
        script = new DialogScript();
        switchNewSection(DialogScript.ENTRY_SECTION);
    }

    public void switchNewSection(String newSectionName) {
        workingSection = script.createSection(newSectionName);

    }

    public void completeSyntax(IScriptSyntax syntax) {
        workingSection.getSyntaxList().add(syntax);
    }

    public void parser(List<String> list) {
        list.stream()
                .map(j -> j.trim())//剔除空格，逐行读取
                .filter(j -> (!j.isEmpty() && !j.startsWith("#")))//如果开头为#,则解读为注释，跳过
                .forEach(this::readLine);//传递读取
        produceMap.put(scriptName, script);
    }


    public void readLine(String string) {
        if (string.startsWith("$")) {
            produceMap.put(scriptName, script);
            String newScriptName = substring(string, 1);
            switchNewScrip(newScriptName);
        } else if (RegexPatternUtil.PATTERN_BRACE.matcher(string).matches()) {
            switchNewSection(string.substring(1, string.length() - 1));
        } else {
            List<String> availableTagList = creator.getRegisteredSyntax()
                    .keySet()
                    .stream()
                    .filter(tag -> string.startsWith(tag))
                    .collect(Collectors.toList());

            //TODO 当前只支持1个syntax，过多过少都报错
            Preconditions.checkState(availableTagList.size() > 0, "tag undefined! " + string);
            Preconditions.checkState(availableTagList.size() == 1, "tag conflict! " + string);
            String usingTag = availableTagList.get(0);
            IScriptSyntax syntax = creator.createSyntax(usingTag);
            syntax.parser(substring(string, usingTag.length()));
            completeSyntax(syntax);
        }

    }


    public String substring(String input, int length) {
        return input.substring(length).trim();
    }


}
