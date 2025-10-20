package uz.abisoft.launcherandroidtv.presentation

import uz.abisoft.launcherandroidtv.domain.model.AppEntry


data class HomeState(
    val query: String = "",
    val apps: List<AppEntry> = emptyList(),
    val filtered: List<AppEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)