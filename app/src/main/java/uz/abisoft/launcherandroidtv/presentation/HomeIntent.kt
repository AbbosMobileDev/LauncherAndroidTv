package uz.abisoft.launcherandroidtv.presentation

sealed interface HomeIntent {
    data object Load : HomeIntent
    data class Search(val q: String) : HomeIntent
    data class ToggleFav(val pkg: String) : HomeIntent
    data class Launched(val pkg: String) : HomeIntent
}