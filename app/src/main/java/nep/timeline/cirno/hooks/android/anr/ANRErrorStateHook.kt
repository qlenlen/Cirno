package nep.timeline.cirno.hooks.android.anr

import android.content.pm.ApplicationInfo
import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import nep.timeline.cirno.framework.AbstractMethodHook
import nep.timeline.cirno.framework.MethodHook
import nep.timeline.cirno.utils.AnrHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class ANRErrorStateHook(classLoader: ClassLoader) : MethodHook(classLoader) {

  override fun getTargetClass() =
    "com.android.server.am.ProcessErrorStateRecord"

  override fun getTargetMethod() =
    "appNotResponding"

  override fun getTargetParam() = arrayOf(
    String::class.java,
    ApplicationInfo::class.java,
    String::class.java,
    "com.android.server.wm.WindowProcessController",
    Boolean::class.javaPrimitiveType!!,
    "com.android.internal.os.TimeoutRecord",
    ExecutorService::class.java,
    Boolean::class.javaPrimitiveType!!,
    Boolean::class.javaPrimitiveType!!,
    Future::class.java
  )

  override fun getTargetHook(): XC_MethodHook = object : AbstractMethodHook() {
    override fun beforeMethod(param: MethodHookParam) {
      val app = XposedHelpers.getObjectField(param.thisObject, "mApp")
      if (app != null) {
        AnrHelper.processingAnr(param, app)
      }
    }
  }

  override fun getMinVersion() =
    Build.VERSION_CODES.S
}
