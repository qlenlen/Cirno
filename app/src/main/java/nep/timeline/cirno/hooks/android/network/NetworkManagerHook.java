package nep.timeline.cirno.hooks.android.network;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import nep.timeline.cirno.framework.AbstractMethodHook;
import nep.timeline.cirno.framework.MethodHook;
import nep.timeline.cirno.services.NetworkManagementService;

public class NetworkManagerHook extends MethodHook {
  public NetworkManagerHook(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  public String getTargetClass() {
    return "com.android.server.net.NetworkManagementService";
  }

  @Override
  public String getTargetMethod() {
    return "create";
  }

  @Override
  public Object[] getTargetParam() {
    return new Object[] {Context.class};
  }

  @Override
  public XC_MethodHook getTargetHook() {
    return new AbstractMethodHook() {
      @Override
      protected void afterMethod(XC_MethodHook.MethodHookParam param) {
        NetworkManagementService.setInstance(param.getResult(), classLoader);
      }
    };
  }
}
