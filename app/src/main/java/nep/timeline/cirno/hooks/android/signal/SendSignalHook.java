package nep.timeline.cirno.hooks.android.signal;

import android.os.Process;
import de.robv.android.xposed.XC_MethodHook;
import nep.timeline.cirno.framework.AbstractMethodHook;
import nep.timeline.cirno.framework.MethodHook;
import nep.timeline.cirno.services.ProcessService;
import nep.timeline.cirno.virtuals.ProcessRecord;

public class SendSignalHook extends MethodHook {
  public SendSignalHook(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  public String getTargetClass() {
    return Process.class.getTypeName();
  }

  @Override
  public String getTargetMethod() {
    return "killProcess";
  }

  @Override
  public Object[] getTargetParam() {
    return new Object[] {int.class};
  }

  @Override
  public XC_MethodHook getTargetHook() {
    return new AbstractMethodHook() {
      @Override
      protected void beforeMethod(MethodHookParam param) {
        int pid = (int) param.args[0];

        ProcessRecord processRecord = ProcessService.getProcessRecordByPid(pid);
        if (processRecord == null || processRecord.isDeathProcess()) return;

        ProcessService.removeProcessRecord(processRecord);
      }
    };
  }
}
