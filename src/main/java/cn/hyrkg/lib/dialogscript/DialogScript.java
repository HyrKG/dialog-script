package cn.hyrkg.lib.dialogscript;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;

@RequiredArgsConstructor
public class DialogScript {

    public static final String ENTRY_SECTION = "default";

    @Getter
    private final DialogScriptManager manager;

    protected HashMap<String, DialogSection> sectionMap = new HashMap<>();

    public DialogSection createSection(String key) {
        sectionMap.put(key, new DialogSection());
        return sectionMap.get(key);
    }


    public DialogSection getEntrySection() {
        return sectionMap.get(ENTRY_SECTION);
    }

    public DialogSection getSection(String key) {
        return sectionMap.get(key);
    }

    public Collection<DialogSection> getSections() {
        return sectionMap.values();
    }


    public boolean isEmpty() {
        return sectionMap.isEmpty();
    }
}
