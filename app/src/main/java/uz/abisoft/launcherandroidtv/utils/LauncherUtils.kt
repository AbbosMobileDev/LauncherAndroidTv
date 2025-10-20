package uz.abisoft.launcherandroidtv.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.provider.Settings
import android.widget.Toast

object LauncherUtils {

    /**
     * Dastur default launcher ekanligini tekshiradi
     */
    fun isDefaultLauncher(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val currentLauncher = resolveInfo?.activityInfo?.packageName
        val ourPackageName = context.packageName

        println("DEBUG: Current default launcher: $currentLauncher")
        println("DEBUG: Our package: $ourPackageName")
        println("DEBUG: Is default: ${currentLauncher == ourPackageName}")

        return currentLauncher == ourPackageName
    }

    /**
     * Barcha mavjud launcherlarni olish
     */
    fun getAvailableLaunchers(context: Context): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

    /**
     * Bizning dastur launcherlar ro'yxatida bormi?
     */
    fun isOurAppInLauncherList(context: Context): Boolean {
        val launchers = getAvailableLaunchers(context)
        val ourPackageName = context.packageName

        println("=== DEBUG: LAUNCHERLAR RO'YXATI ===")
        launchers.forEach { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            val appName = resolveInfo.loadLabel(context.packageManager).toString()
            val activityName = resolveInfo.activityInfo.name
            println("DEBUG: - $appName ($packageName) - $activityName")
            println("DEBUG: - Is our app: ${packageName == ourPackageName}")
        }
        println("DEBUG: Bizning package: $ourPackageName")
        println("=== DEBUG TUGADI ===")

        return launchers.any { it.activityInfo.packageName == ourPackageName }
    }

    /**
     * Default launcher settings ga o'tish
     */
    fun openLauncherSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Alternative yondashuv
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                true
            } catch (e2: Exception) {
                false
            }
        }
    }

    /**
     * To'g'ridan-to'g'ri launcher tanlash oynasiga o'tish
     */


    /**
     * Foydalanuvchiga qanday default qilish kerakligini ko'rsatish
     */
    fun showDefaultLauncherInstructions(context: Context) {
        val isInList = isOurAppInLauncherList(context)
        val message = if (isInList) {
            "Quyidagi amallarni bajaring:\n" +
                    "1. 'TV Launcher' ni tanlang\n" +
                    "2. 'Always' yoki 'Har doim' ni bosing\n" +
                    "3. Orqaga tugmasini bosing"
        } else {
            "⚠️ TV Launcher ro'yxatda ko'rinmayapti\n" +
                    "Dasturni qayta o'rnating yoki sozlamalardan 'Clear defaults' qiling"
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    // LauncherUtils.kt ga qo'shing
    fun clearLauncherDefaults(context: Context): Boolean {
        return try {
            val packageName = context.packageName
            val packageManager = context.packageManager

            // Package manager orqali defaults ni tozalash
            packageManager.clearPackagePreferredActivities(packageName)

            // Intent filter larni qayta o'rnatish
            val componentName =
                ComponentName(packageName, "${packageName}.presentation.MainActivity")
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP
            )

            Toast.makeText(context, "Defaults tozalandi. Endi qayta tanlang.", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Xato: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}