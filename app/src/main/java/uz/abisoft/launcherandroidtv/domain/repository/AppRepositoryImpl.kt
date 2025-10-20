package uz.abisoft.launcherandroidtv.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import uz.abisoft.launcherandroidtv.data.Prefs
import uz.abisoft.launcherandroidtv.domain.model.AppEntry
import uz.abisoft.launcherandroidtv.domain.repository.AppRepository
import androidx.core.graphics.createBitmap

class AppRepositoryImpl(
    private val context: Context,
    private val prefs: Prefs
) : AppRepository {

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun getLaunchableApps(): List<AppEntry> {
        val pm: PackageManager = context.packageManager
        val favs = prefs.getFavorites()

        val flags = PackageManager.MATCH_DISABLED_COMPONENTS or
                PackageManager.MATCH_UNINSTALLED_PACKAGES or
                PackageManager.GET_META_DATA

        val tvIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val tvApps = pm.queryIntentActivities(tvIntent, flags)

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val regularApps = pm.queryIntentActivities(launcherIntent, flags)

        val allApps = (tvApps + regularApps)
            .distinctBy { it.activityInfo.packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        return allApps.map { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo
            val packageName = activityInfo.packageName
            val label = resolveInfo.loadLabel(pm).toString()

            AppEntry(
                label = label,
                packageName = packageName,
                className = activityInfo.name,
                iconUri = getIconUri(resolveInfo, pm, label),
                isFavorite = packageName in favs,
                isTvOptimized = tvApps.any { it.activityInfo.packageName == packageName }
            )
        }
    }
    private fun getIconUri(resolveInfo: android.content.pm.ResolveInfo, pm: PackageManager, label: String): String? {
        return try {
            val drawable: Drawable = resolveInfo.loadIcon(pm)
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                saveTempIcon(bitmap, label)
            } else {
                val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
                val canvas = android.graphics.Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                saveTempIcon(bitmap, label)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun saveTempIcon(bitmap: Bitmap, label: String): String? {
        return try {
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "app_icon_${System.currentTimeMillis()}",
                "Icon for $label"
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun toggleFavorite(pkg: String): Boolean {
        val set = prefs.getFavorites()
        val added = if (pkg in set) {
            set.remove(pkg)
            false
        } else {
            set.add(pkg)
            true
        }
        prefs.setFavorites(set)
        return added
    }

    override fun isFavorite(pkg: String): Boolean = pkg in prefs.getFavorites()
    override fun saveLastLaunched(pkg: String) = prefs.setLastLaunched(pkg)
    override fun getLastLaunched(): String? = prefs.getLastLaunched()
}