package nep.timeline.cirno.entity;

import de.robv.android.xposed.XposedHelpers;
import lombok.Getter;

@Getter
public class PendingIntentKey {
    private final Object instance;
    private final String packageName;
    private final int userId;

    public PendingIntentKey(Object key) {
        this.instance = key;
        this.packageName = (String) XposedHelpers.getObjectField(key, "packageName");
        this.userId = XposedHelpers.getIntField(key, "userId");
    }
}