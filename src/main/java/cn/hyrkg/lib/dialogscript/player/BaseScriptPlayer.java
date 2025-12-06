package cn.hyrkg.lib.dialogscript.player;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.DialogSection;
import cn.hyrkg.lib.dialogscript.syntax.ScriptSyntax;
import cn.hyrkg.lib.dialogscript.syntax.SetSyntax;
import cn.hyrkg.lib.dialogscript.syntax.ConditionSyntax;
import cn.hyrkg.lib.dialogscript.syntax.GotoSyntax;
import cn.hyrkg.lib.dialogscript.syntax.JumpSyntax;
import cn.hyrkg.lib.dialogscript.syntax.OptionSyntax;
import cn.hyrkg.lib.dialogscript.syntax.SleepSyntax;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseScriptPlayer {

    protected DialogScript playingScrip = null;
    protected DialogSection playingSection = null;
    protected UUID playingSectionUuid = UUID.randomUUID();

    public HashMap<String, Object> tmpStorage = new HashMap<>();
    private HashMap<Class<? extends ScriptSyntax>, Method> methodReflectionMap = new HashMap<>();


    public int syntaxPointer = 0;

    public boolean stopFlag = false;
    public boolean waitFlag = false;

    public void stop() {
        stopFlag = true;
        waitFlag = false;
    }


    public void useAnnotationFramework() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ListenSyntax.class) || method.getParameterCount() != 1) {
                continue;
            }
            if (!ScriptSyntax.class.isAssignableFrom(method.getParameters()[0].getType())) {
                continue;
            }
            methodReflectionMap.put((Class<? extends ScriptSyntax>) method.getParameters()[0].getType(), method);
        }
    }

    public void setTmpValue(String key, Object value) {
        this.tmpStorage.put(key, value);
    }

    public Object getTmpValue(String key) {
        return this.tmpStorage.get(key);
    }

    public void play(DialogScript script) {
        playingScrip = script;
        handleSection(script.getEntrySection());
    }

    public void jumpSection(String sectionName) {
        Preconditions.checkState(playingScrip.getSection(sectionName) != null, "undefined section " + sectionName);
        playingSection = playingScrip.getSection(sectionName);
        playingSectionUuid = UUID.randomUUID();
    }

    @SneakyThrows
    //FIXME 未对脚本错误进行正确处理
    public void handleSyntax(ScriptSyntax syntax) {

        if (methodReflectionMap.containsKey(syntax.getClass())) {
            // 如果有使用 @ListenSyntax 注册的方法，优先调用反射方法
            methodReflectionMap.get(syntax.getClass()).invoke(this, syntax);
        } else {
            // 否则进行默认的语法处理
            if (syntax instanceof OptionSyntax) {
                handleOptionSyntax((OptionSyntax) syntax);
            } else if (syntax instanceof JumpSyntax) {
                jumpSection(((JumpSyntax) syntax).getSection());
            } else if (syntax instanceof SleepSyntax) {
                Thread.sleep(((SleepSyntax) syntax).getSleepTimeInMs());
            } else if (syntax instanceof ConditionSyntax) {
                handleConditionSyntax((ConditionSyntax) syntax);
            } else if (syntax instanceof GotoSyntax) {
                handleGotoSyntax((GotoSyntax) syntax);
            } else if (syntax instanceof SetSyntax) {
                SetSyntax setSyntax = (SetSyntax) syntax;
                handleSetSyntax(setSyntax);
            }
        }
    }

    /**
     * 处理选项语法
     */
    private void handleOptionSyntax(OptionSyntax syntax) {
        List<OptionSyntax> collectedOptions = new ArrayList<>();
        ScriptSyntax nextSyntax = syntax;
        // 收集连续的选项
        while (nextSyntax != null && nextSyntax instanceof OptionSyntax) {
            collectedOptions.add((OptionSyntax) nextSyntax);
            nextSyntax = playingSection.findNext(nextSyntax);
        }
        // 更新指针，跳过已收集的选项
        syntaxPointer += collectedOptions.size() - 1;
        playOptions(collectedOptions);
    }

    /**
     * 处理条件分支语法
     */
    private void handleConditionSyntax(ConditionSyntax cond) {
        if (!checkCondition(cond.getScope(), cond.getCondition(), cond.getParams())) {
            // 如果条件不满足
            if (cond.getJumpIndexWhenFalse() != -1) {
                // 如果有跳转目标，直接跳转
                syntaxPointer = cond.getJumpIndexWhenFalse();
                // 抵消循环末尾的 pointer++
                syntaxPointer--;
            } else {
                // 兼容逻辑：跳过下一行
                syntaxPointer++;
            }
        }
    }

    /**
     * 处理 Goto 跳转语法
     */
    private void handleGotoSyntax(GotoSyntax syntax) {
        syntaxPointer = syntax.getTargetIndex();
        // 抵消循环末尾的 pointer++
        syntaxPointer--;
    }

    /**
     * 处理 Set 语法
     */
    private void handleSetSyntax(SetSyntax setSyntax) {
        String scope = setSyntax.getScope();
        if (playingScrip.getManager().getRegisteredSetters().containsKey(scope)) {
            playingScrip.getManager().getRegisteredSetters().get(scope).set(this, setSyntax.getKey(), setSyntax.getValue());
        } else {
            // 如果未找到处理器，可以选择忽略或打印警告
            System.err.println("Warning: No setter handler found for scope: " + scope);
        }
    }

    /**
     * 检查条件是否满足
     */
    public boolean checkCondition(String scope, String condition, String params) {
        // 1. 优先使用注册的处理器
        if (playingScrip.getManager().getRegisteredConditions().containsKey(scope)) {
            return playingScrip.getManager().getRegisteredConditions().get(scope).check(this, scope, condition, params);
        } else {
            // 2. 未找到处理器，打印警告并返回 false
            System.err.println("Warning: No condition handler found for type: " + scope);
            return false;
        }
    }

    @SneakyThrows
    public void handleSection(DialogSection section) {
        UUID sectionUuid = UUID.randomUUID();
        playingSectionUuid = sectionUuid;
        playingSection = section;
        syntaxPointer = 0;
        while (playingSectionUuid == sectionUuid && syntaxPointer < section.getSyntaxList().size() && !stopFlag) {
            handleSyntax(section.getSyntaxList().get(syntaxPointer));
            syntaxPointer += 1;
            while (waitFlag && !stopFlag) {
                Thread.sleep(100);
            }
            if (sectionUuid != playingSectionUuid && !stopFlag) {
                handleSection(playingSection);
                return;
            }
        }
        onFinish();
    }

    public abstract void onFinish();

    /**
     * 播放选项，我们期望在此进行线程堵塞，并且等待反馈再执行下一步
     */
    public abstract void playOptions(List<OptionSyntax> options);
}
