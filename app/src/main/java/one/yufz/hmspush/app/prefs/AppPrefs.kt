package one.yufz.hmspush.app.prefs

import android.content.Context
import androidx.core.content.edit

class AppPrefs(val context: Context) {
    companion object {
        private const val DEFAULT_ICON_URL = "https://raw.githubusercontent.com/fankes/AndroidNotifyIconAdapt/main/APP/NotifyIconsSupportConfig.json"
    }

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var customIconUrl: String
        get() = prefs.getString("custom_icon_url", null)?.takeIf { it.isNotBlank() } ?: DEFAULT_ICON_URL
        set(value) = prefs.edit { putString("custom_icon_url", value) }
}