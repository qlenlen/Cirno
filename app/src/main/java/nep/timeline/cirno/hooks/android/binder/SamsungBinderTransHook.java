package nep.timeline.cirno.hooks.android.binder;

import de.robv.android.xposed.XC_MethodHook;
import nep.timeline.cirno.framework.AbstractMethodHook;
import nep.timeline.cirno.framework.MethodHook;
import nep.timeline.cirno.services.BinderService;
import nep.timeline.cirno.services.FreezerService;
import nep.timeline.cirno.utils.SystemChecker;

public class SamsungBinderTransHook extends MethodHook {
  public SamsungBinderTransHook(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  public String getTargetClass() {
    return "com.android.server.am.FreecessController";
  }

  @Override
  public String getTargetMethod() {
    return "reportBinderUid";
  }

  @Override
  public Object[] getTargetParam() {
    return new Object[] {int.class, int.class, int.class, int.class, int.class, String.class};
  }

  @Override
  public XC_MethodHook getTargetHook() {
    return new AbstractMethodHook() {
      @Override
      protected void beforeMethod(MethodHookParam param) {
        if (BinderService.received) {
          unhook();
          return;
        }

        int flags = (int) param.args[3];
        if (flags == 1) return; // Async binder
        int uid = (int) param.args[0];

        FreezerService.temporaryUnfreezeIfNeed(uid, "Binder", 3000);
      }
    };
  }

  @Override
  public boolean isIgnoreError() {
    return !SystemChecker.isSamsung(classLoader);
  }
}
