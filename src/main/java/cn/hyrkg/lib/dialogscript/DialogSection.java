package cn.hyrkg.lib.dialogscript;

import cn.hyrkg.lib.dialogscript.syntax.IScriptSyntax;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DialogSection {
    @Getter
    protected List<IScriptSyntax> syntaxList = new ArrayList<>();


    public IScriptSyntax findPrevious(IScriptSyntax syntax) {
        int index = findIndex(syntax);
        if (index == -1 || index == 0) {
            return null;
        }
        return syntaxList.get(index - 1);
    }

    public IScriptSyntax findNext(IScriptSyntax syntax) {
        int index = findIndex(syntax);
        if (index == -1 || index == syntaxList.size() - 1) {
            return null;
        }
        return syntaxList.get(index + 1);
    }

    public int findIndex(IScriptSyntax syntax) {
        for(int i=0;i<syntaxList.size();i++)
        {
            if(syntaxList.get(i)==syntax)
            {
                return i;
            }
        }
        return -1;
    }
}
