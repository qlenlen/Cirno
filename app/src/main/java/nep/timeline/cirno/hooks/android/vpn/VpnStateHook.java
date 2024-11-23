package nep.timeline.cirno.hooks.android.vpn;

import android.net.NetworkInfo;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nep.timeline.cirno.entity.AppRecord;
import nep.timeline.cirno.framework.AbstractMethodHook;
import nep.timeline.cirno.framework.MethodHook;
import nep.timeline.cirno.log.Log;
import nep.timeline.cirno.services.AppService;
import nep.timeline.cirno.services.FreezerService;
import nep.timeline.cirno.threads.FreezerHandler;
import nep.timeline.cirno.utils.PKGUtils;

public class VpnStateHook extends MethodHook {
    public VpnStateHook(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public String getTargetClass() {
        return "com.android.server.connectivity.Vpn";
    }

    @Override
    public String getTargetMethod() {
        return "updateState";
    }

    @Override
    public Object[] getTargetParam() {
        return new Object[] { NetworkInfo.DetailedState.class, String.class };
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void beforeMethod(XC_MethodHook.MethodHookParam param) {
                String state = param.args[0].toString();
                int uid = XposedHelpers.getIntField(param.thisObject, "mOwnerUID");
                String packageName = (String) XposedHelpers.getObjectField(param.thisObject, "mPackage");

                AppRecord appRecord = AppService.get(packageName, PKGUtils.getUserId(uid));
                if (appRecord != null) {
                    if (appRecord.isSystem())
                        return;

                    if ("CONNECTED".equals(state) && appRecord.getAppState().setVpn(true)) {
                        Log.d(appRecord.getPackageNameWithUser() + " 连接至VPN");
                        FreezerService.thaw(appRecord);
                    }

                    if (("DISCONNECTED".equals(state) || "FAILED".equals(state)) && appRecord.getAppState().setVpn(false)) {
                        Log.d(appRecord.getPackageNameWithUser() + " 从VPN断开连接");
                        FreezerHandler.sendFreezeMessageIgnoreMessages(appRecord, 3000);
                    }
                }
            }
        };
    }
}
