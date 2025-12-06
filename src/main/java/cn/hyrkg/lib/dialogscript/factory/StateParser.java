package cn.hyrkg.lib.dialogscript.factory;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.DialogScriptManager;
import cn.hyrkg.lib.dialogscript.DialogSection;
import cn.hyrkg.lib.dialogscript.syntax.ScriptSyntax;
import cn.hyrkg.lib.dialogscript.utils.RegexPatternUtil;
import com.google.common.base.Preconditions;

import cn.hyrkg.lib.dialogscript.syntax.ConditionSyntax;
import cn.hyrkg.lib.dialogscript.syntax.GotoSyntax;

import java.util.Stack;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 该类主要复杂将一行行的脚本具体解读为脚本，并且将其返回
 */
public class StateParser {

    public static final String DEFAULT_SCRIPT_KEY = "default";

    public DialogScriptManager creator;

    public String scriptName = null;
    public DialogScript script = null;
    public DialogSection workingSection = null;

    // 逻辑块上下文
    private static class BlockContext {
        ConditionSyntax currentActiveCondition;
        List<GotoSyntax> endJumps = new ArrayList<>();
    }

    private Stack<BlockContext> blockStack = new Stack<>();

    public HashMap<String, DialogScript> produceMap = new HashMap<>();

    public StateParser(DialogScriptManager creator) {
        this.creator = creator;
        switchNewScrip(DEFAULT_SCRIPT_KEY);
    }

    public void switchNewScrip(String newScripName) {
        //FIXME 逻辑不完善
        if (scriptName != null && script != null && script.isEmpty()) {
            produceMap.put(scriptName, script);
        }
        scriptName = newScripName;
        script = new DialogScript(creator);
        switchNewSection(DialogScript.ENTRY_SECTION);
    }

    public void switchNewSection(String newSectionName) {
        if (!blockStack.isEmpty()) {
            throw new RuntimeException("Section changed inside a block! Missing endif?");
        }
        workingSection = script.createSection(newSectionName);
    }

    public void completeSyntax(ScriptSyntax syntax) {
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
        } else if (string.startsWith("if ")) {
            handleIf(string.substring(3).trim());
        } else if (string.startsWith("elif ")) {
            handleElif(string.substring(5).trim());
        } else if (string.startsWith("else")) {
            handleElse();
        } else if (string.startsWith("endif")) {
            handleEndif();
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
            ScriptSyntax syntax = creator.createSyntax(usingTag);
            syntax.parser(substring(string, usingTag.length()));
            completeSyntax(syntax);
        }

    }

    private void handleIf(String conditionLine) {
        BlockContext context = new BlockContext();
        blockStack.push(context);

        ConditionSyntax syntax = new ConditionSyntax();
        syntax.parser(conditionLine);
        completeSyntax(syntax);

        context.currentActiveCondition = syntax;
    }

    private void handleElif(String conditionLine) {
        Preconditions.checkState(!blockStack.isEmpty(), "elif without if");
        BlockContext context = blockStack.peek();

        // 1. 上一个块结束，需要跳到 endif
        GotoSyntax endJump = new GotoSyntax();
        completeSyntax(endJump);
        context.endJumps.add(endJump);

        // 2. 上一个条件如果不满足，跳到这里 (当前是新条件的开始)
        // 这里的“这里”其实是指下一个 ConditionSyntax 的位置，也就是 workingSection.getSyntaxList().size()
        // 因为 endJump 已经添加进去了，size 已经增加了
        int currentIndex = workingSection.getSyntaxList().size();
        if (context.currentActiveCondition != null) {
            context.currentActiveCondition.setJumpIndexWhenFalse(currentIndex);
        }

        // 3. 新的条件
        ConditionSyntax syntax = new ConditionSyntax();
        syntax.parser(conditionLine);
        completeSyntax(syntax);

        context.currentActiveCondition = syntax;
    }

    private void handleElse() {
        Preconditions.checkState(!blockStack.isEmpty(), "else without if");
        BlockContext context = blockStack.peek();

        // 1. 上一个块结束，需要跳到 endif
        GotoSyntax endJump = new GotoSyntax();
        completeSyntax(endJump);
        context.endJumps.add(endJump);

        // 2. 上一个条件如果不满足，跳到这里 (else 块的开始)
        int currentIndex = workingSection.getSyntaxList().size();
        if (context.currentActiveCondition != null) {
            context.currentActiveCondition.setJumpIndexWhenFalse(currentIndex);
        }

        // else 块没有条件需要判断
        context.currentActiveCondition = null;
    }

    private void handleEndif() {
        Preconditions.checkState(!blockStack.isEmpty(), "endif without if");
        BlockContext context = blockStack.pop();

        int currentIndex = workingSection.getSyntaxList().size();

        // 1. 如果还有活跃的条件（说明没有 else），如果不满足则跳到这里
        if (context.currentActiveCondition != null) {
            context.currentActiveCondition.setJumpIndexWhenFalse(currentIndex);
        }

        // 2. 所有中间产生的跳转到末尾的指令，都更新为跳到这里
        for (GotoSyntax jump : context.endJumps) {
            jump.setTargetIndex(currentIndex);
        }
    }


    public String substring(String input, int length) {
        return input.substring(length).trim();
    }


}
