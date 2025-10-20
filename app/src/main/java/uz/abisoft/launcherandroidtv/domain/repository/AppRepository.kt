package uz.abisoft.launcherandroidtv.domain.repository

import uz.abisoft.launcherandroidtv.domain.model.AppEntry

interface AppRepository {
    suspend fun getLaunchableApps(): List<AppEntry>
    fun toggleFavorite(pkg: String): Boolean
    fun isFavorite(pkg: String): Boolean
    fun saveLastLaunched(pkg: String)
    fun getLastLaunched(): String?
}
