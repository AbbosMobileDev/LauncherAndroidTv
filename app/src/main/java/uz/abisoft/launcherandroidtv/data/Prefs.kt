package uz.abisoft.launcherandroidtv.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(context: Context) {
    private val p: SharedPreferences =
        context.getSharedPreferences("tv_launcher_prefs", Context.MODE_PRIVATE)

    fun getFavorites(): MutableSet<String> =
        p.getStringSet("favorites", emptySet())?.toMutableSet() ?: mutableSetOf()

    fun setFavorites(set: Set<String>) {
        p.edit { putStringSet("favorites", set) }
    }

    fun setLastLaunched(pkg: String) {
        p.edit { putString("last_launched", pkg) }
    }

    fun getLastLaunched(): String? = p.getString("last_launched", null)
}
