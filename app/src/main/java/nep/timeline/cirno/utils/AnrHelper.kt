package nep.timeline.cirno.utils

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import nep.timeline.cirno.services.ProcessService

object AnrHelper {
  @JvmStatic
  fun processingAnr(param: MethodHookParam, app: Any?) {
    if (app == null) return
    val processRecord = ProcessService.getProcessRecord(app)
    if (processRecord == null) return
    val appRecord = processRecord.getAppRecord()
    if (appRecord == null) return
    if (!appRecord.isSystem) param.setResult(null)
  }
}
