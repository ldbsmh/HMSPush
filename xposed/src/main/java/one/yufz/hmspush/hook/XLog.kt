package one.yufz.hmspush.hook

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Method

object XLog {

    /** 全局开关：true 时只输出错误日志 */
    var ERROR_ONLY = true

    fun d(tag: String, message: String?) {
        // 忽略普通调试日志
        if (!ERROR_ONLY) XposedBridge.log("[HMSPush]  $tag  $message")
    }

    fun i(tag: String, message: String?) {
        // 忽略普通信息日志
        if (!ERROR_ONLY) XposedBridge.log("[HMSPush]  $tag  $message")
    }

    fun e(tag: String, message: String?, throwable: Throwable?) {
        XposedBridge.log("[HMSPush]  $tag  ERROR: $message")
        if (throwable != null) {
            XposedBridge.log(Log.getStackTraceString(throwable))
        }
    }

    fun XC_MethodHook.MethodHookParam.logMethod(tag: String, stackTrace: Boolean = false) {
        if (!ERROR_ONLY) {
            d(tag, "╔═══════════════════════════════════════════════════════")
            d(tag, method.toString())
            d(tag, "${method.name} called with ${args.contentDeepToString()}")
            if (stackTrace) {
                d(tag, Log.getStackTraceString(Throwable()))
            }
            if (hasThrowable()) {
                e(tag, "${method.name} thrown", throwable)
            } else if (method is Method && (method as Method).returnType != Void.TYPE) {
                d(tag, "${method.name} return $result")
            }
            d(tag, "╚═══════════════════════════════════════════════════════")
        } else if (hasThrowable()) {
            e(tag, "${method.name} thrown", throwable)
        }
    }
}