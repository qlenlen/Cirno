package nep.timeline.cirno.hooks.android.broadcast;

import android.content.Intent;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nep.timeline.cirno.GlobalVars;
import nep.timeline.cirno.entity.AppRecord;
import nep.timeline.cirno.framework.AbstractMethodHook;
import nep.timeline.cirno.log.Log;
import nep.timeline.cirno.services.AppService;
import nep.timeline.cirno.services.FreezerService;

public class BroadcastIntentHook {
    public BroadcastIntentHook(ClassLoader classLoader) {
       try {
           Class<?> clazz = XposedHelpers.findClassIfExists("com.android.server.am.ActivityManagerService", classLoader);

           if (clazz == null) {
               Log.e("无法监听广播意图!");
               return;
           }

           String methodName = (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ? "broadcastIntentLocked" : "broadcastIntentLockedTraced");

           Method targetMethod = null;
           for (Method method : clazz.getDeclaredMethods())
               if (method.getName().equals(methodName) && (targetMethod == null || targetMethod.getParameterTypes().length < method.getParameterTypes().length))
                   targetMethod = method;

           ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(targetMethod.getParameterTypes()));
           arrayList.add(new AbstractMethodHook() {
               @Override
               protected void beforeMethod(MethodHookParam param) {
                   int intentArgsIndex = 3;

                   int userIdIndex = 19;
                   if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                       userIdIndex = 20;
                   if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
                       userIdIndex = 21;

                   Intent intent = (Intent) param.args[intentArgsIndex];
                   int userId = (int) param.args[userIdIndex];
                   if (intent != null) {
                       String action = intent.getAction();

                       if (action == null || !action.endsWith(".android.c2dm.intent.RECEIVE"))
                           return;

                       String packageName = (intent.getComponent() == null ? intent.getPackage() : intent.getComponent().getPackageName());

                       if (packageName == null)
                           return;

                       AppRecord appRecord = AppService.get(packageName, userId);
                       if (appRecord == null)
                           return;

                       FreezerService.temporaryUnfreezeIfNeed(appRecord, "FCM PUSH", 1500);
                   }
               }
           });

           XposedHelpers.findAndHookMethod(clazz, targetMethod.getName(), arrayList.toArray());

           Log.i("监听广播意图");
       } catch (Throwable throwable) {
           XposedBridge.log(GlobalVars.TAG + " -> 无法监听广播意图, 异常:");
           XposedBridge.log(throwable);
           Log.e("监听广播意图失败", throwable);
       }
    }
}