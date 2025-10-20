package uz.abisoft.launcherandroidtv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.abisoft.launcherandroidtv.domain.model.AppEntry
import uz.abisoft.launcherandroidtv.domain.usecase.GetLaunchableAppsUseCase
import uz.abisoft.launcherandroidtv.domain.usecase.SaveLastLaunchedUseCase
import uz.abisoft.launcherandroidtv.domain.usecase.ToggleFavoriteUseCase

class HomeViewModel(
    private val getApps: GetLaunchableAppsUseCase,
    private val toggleFav: ToggleFavoriteUseCase,
    private val saveLast: SaveLastLaunchedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        load()
    }

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> load()
            is HomeIntent.Search -> {
                _state.value = _state.value.copy(
                    query = intent.q,
                    filtered = filter(_state.value.apps, intent.q)
                )
            }
            is HomeIntent.ToggleFav -> {
                toggleFav(intent.pkg)
                refreshAll()
            }
            is HomeIntent.Launched -> saveLast(intent.pkg)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val apps = getApps()
                _state.value = _state.value.copy(
                    isLoading = false,
                    apps = apps,
                    filtered = apps
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ilovalar yuklanmadi: ${e.message}"
                )
                e.printStackTrace()
            }
        }
    }

    private fun filter(list: List<AppEntry>, q: String): List<AppEntry> {
        if (q.isBlank()) return list
        val query = q.trim().lowercase()
        return list.filter {
            it.label.lowercase().contains(query) ||
                    it.packageName.lowercase().contains(query)
        }
    }

    private fun refreshAll() {
        viewModelScope.launch {
            try {
                val apps = getApps()
                _state.value = _state.value.copy(
                    apps = apps,
                    filtered = filter(apps, _state.value.query)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Yangilashda xato: ${e.message}")
            }
        }
    }
}