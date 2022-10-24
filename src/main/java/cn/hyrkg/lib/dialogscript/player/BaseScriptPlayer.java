package cn.hyrkg.lib.dialogscript.player;

import cn.hyrkg.lib.dialogscript.DialogScript;
import cn.hyrkg.lib.dialogscript.DialogSection;
import cn.hyrkg.lib.dialogscript.player.annotation.ListenSyntax;
import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.JumpSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.OptionSyntax;
import cn.hyrkg.lib.dialogscript.syntax.dafault.SleepSyntax;
import com.google.common.base.Preconditions;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class BaseScriptPlayer implements IScriptPlayer {

    protected DialogScript playingScrip = null;
    protected DialogSection playingSection = null;
    protected UUID playingSectionUuid = UUID.randomUUID();

    public HashMap<String, Object> sharedParamMap = new HashMap<>();
    private HashMap<Class<? extends IScriptSyntax>, Method> methodReflectionMap = new HashMap<>();

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
            if (!IScriptSyntax.class.isAssignableFrom(method.getParameters()[0].getType())) {
                continue;
            }
            methodReflectionMap.put((Class<? extends IScriptSyntax>) method.getParameters()[0].getType(), method);
        }
    }

    public void setSharedParam(String key, Object value) {
        this.sharedParamMap.put(key, value);
    }

    @Override
    public void play(DialogScript script) {
        playingScrip = script;
        IScriptPlayer.super.play(script);
    }

    public void jumpSection(String sectionName) {
        Preconditions.checkState(playingScrip.getSection(sectionName) != null, "undefined section " + sectionName);
        playingSection = playingScrip.getSection(sectionName);
        playingSectionUuid = UUID.randomUUID();
    }

    @Override
    @SneakyThrows
    //FIXME 未对脚本错误进行正确处理
    public void handleSyntax(IScriptSyntax syntax) {

        if (methodReflectionMap.containsKey(syntax.getClass())) {
            methodReflectionMap.get(syntax.getClass()).invoke(this, syntax);
        } else {
            //如果反射方法中不包含目标方法，则进行默认处理
            if (syntax instanceof OptionSyntax) {
                List<OptionSyntax> collectedOptions = new ArrayList<>();
                IScriptSyntax nextSyntax = syntax;
                while (nextSyntax != null && nextSyntax instanceof OptionSyntax) {
                    collectedOptions.add((OptionSyntax) nextSyntax);
                    nextSyntax = playingSection.findNext(nextSyntax);
                }
                syntaxPointer += collectedOptions.size() - 1;
                playOptions(collectedOptions);
            } else if (syntax instanceof JumpSyntax) {
                jumpSection(((JumpSyntax) syntax).getSection());
            } else if (syntax instanceof SleepSyntax) {
                Thread.sleep(((SleepSyntax) syntax).getSleepTimeInMs());
            }

        }
    }

    @Override
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
