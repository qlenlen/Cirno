package nep.timeline.cirno.hooks.android.intent;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nep.timeline.cirno.entity.AppRecord;
import nep.timeline.cirno.entity.PendingIntentKey;
import nep.timeline.cirno.framework.AbstractMethodHook;
import nep.timeline.cirno.framework.MethodHook;
import nep.timeline.cirno.services.AppService;
import nep.timeline.cirno.services.FreezerService;
import nep.timeline.cirno.utils.ReflectUtils;

public class PendingIntentHook extends MethodHook {
    public PendingIntentHook(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public String getTargetClass() {
        return "com.android.server.am.PendingIntentRecord";
    }

    @Override
    public String getTargetMethod() {
        return "sendInner";
    }

    @Override
    public Object[] getTargetParam() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
            return ReflectUtils.findParameterTypesOrDefault(XposedHelpers.findClassIfExists(getTargetClass(), classLoader), getTargetMethod(), "android.app.IApplicationThread", int.class, Intent.class, String.class, IBinder.class, "android.content.IIntentReceiver", String.class, IBinder.class, String.class, int.class, int.class, int.class, Bundle.class);
        return ReflectUtils.findParameterTypesOrDefault(XposedHelpers.findClassIfExists(getTargetClass(), classLoader), getTargetMethod(), int.class, Intent.class, String.class, IBinder.class, "android.content.IIntentReceiver", String.class, IBinder.class, String.class, int.class, int.class, int.class, Bundle.class);
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void beforeMethod(MethodHookParam param) {
                synchronized (XposedHelpers.getObjectField(XposedHelpers.getObjectField(param.thisObject, "controller"), "mLock")) {
                    if (XposedHelpers.getBooleanField(param.thisObject, "canceled"))
                        return;

                    Object key = XposedHelpers.getObjectField(param.thisObject, "key");
                    if (key == null)
                        return;

                    PendingIntentKey pendingIntentKey = new PendingIntentKey(key);

                    AppRecord appRecord = AppService.get(pendingIntentKey.getPackageName(), pendingIntentKey.getUserId());

                    if (appRecord == null || !appRecord.isFrozen())
                        return;

                    FreezerService.temporaryUnfreezeIfNeed(appRecord, "Intent", 3000);
                }
            }
        };
    }
}