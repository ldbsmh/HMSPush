package one.yufz.hmspush.hook.fakedevice

import de.robv.android.xposed.callbacks.XC_LoadPackage
import one.yufz.hmspush.common.BridgeWrap
import one.yufz.hmspush.hook.XLog
import one.yufz.xposed.onApplicationAttachContext
import java.lang.reflect.Modifier

object FakeDevice {
    private const val TAG = "FakeDevice"

    private val Default = arrayOf(Common::class.java)

    private val FakeDeviceConfig: Map<String, Array<Class<out IFakeDevice>>> = mapOf(
        "com.coolapk.market" to arrayOf(CoolApk::class.java),
        "com.tencent.mobileqq" to arrayOf(QQ::class.java),
        "com.tencent.tim" to arrayOf(QQ::class.java),
        "com.sankuai.meituan" to arrayOf(FakeEmuiOnly::class.java),
        "com.sankuai.meituan.takeoutnew" to arrayOf(FakeEmuiOnly::class.java),
        "com.dianping.v1" to arrayOf(FakeEmuiOnly::class.java),
        "com.eg.android.AlipayGphone" to arrayOf(Alipay::class.java),
        "com.xunmeng.pinduoduo" to arrayOf(PinDuoDuo::class.java),
        "com.ss.android.ugc.aweme" to arrayOf(DouYin::class.java),
        "com.tencent.tmgp.sgame" to arrayOf(XGPush::class.java),
    )

    fun fake(lpparam: XC_LoadPackage.LoadPackageParam) {
        XLog.d(TAG, "fake() called with: packageName = ${lpparam.packageName}, processName = ${lpparam.processName}")

        // 忽略 WebView
        if (lpparam.packageName == "com.google.android.webview") {
            XLog.d(TAG, "ignore ${lpparam.packageName}")
            return
        }

        val fakes = FakeDeviceConfig[lpparam.packageName] ?: Default

        fakes.forEach { clazz ->
            try {
                // ❗过滤非法类（核心修复）
                if (Modifier.isAbstract(clazz.modifiers) || clazz.isInterface) {
                    XLog.e(TAG, "Skip abstract/interface: $clazz")
                    return@forEach
                }

                // ❗安全实例化（替换 newInstance）
                val instance = try {
                    clazz.getDeclaredConstructor().newInstance()
                } catch (e: Throwable) {
                    XLog.e(TAG, "Create instance failed: $clazz", e)
                    null
                }

                instance?.fake(lpparam)

            } catch (t: Throwable) {
                XLog.e(TAG, "FakeDevice exec failed: $clazz", t)
            }
        }

        // 其他 hook（加保护）
        try {
            fakeOthers(lpparam)
        } catch (t: Throwable) {
            XLog.e(TAG, "fakeOthers crash", t)
        }
    }

    private fun fakeOthers(lpparam: XC_LoadPackage.LoadPackageParam) {
        onApplicationAttachContext {
            XLog.d(TAG, "${this}.attachBaseContext() called")

            try {
                if (BridgeWrap.isDisableSignature(this)) {
                    FakeHmsSignature.hook(lpparam)
                }
            } catch (t: Throwable) {
                XLog.e(TAG, "disable signature error", t)
            }

            try {
                HookHmsDeviceId.hook(lpparam)
            } catch (t: Throwable) {
                XLog.e(TAG, "HookHmsDeviceId error", t)
            }
        }
    }
}