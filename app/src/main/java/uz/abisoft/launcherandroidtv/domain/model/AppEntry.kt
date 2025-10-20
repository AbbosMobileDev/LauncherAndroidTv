package uz.abisoft.launcherandroidtv.domain.model

data class AppEntry(
    val label: String,
    val packageName: String,
    val className: String,
    val iconUri: String? = null,
    val isFavorite: Boolean = false,
    val isTvOptimized: Boolean = false
)
