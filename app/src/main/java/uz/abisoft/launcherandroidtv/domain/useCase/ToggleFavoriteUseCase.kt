package uz.abisoft.launcherandroidtv.domain.usecase

import uz.abisoft.launcherandroidtv.domain.repository.AppRepository

class ToggleFavoriteUseCase(private val repo: AppRepository) {
    operator fun invoke(pkg: String): Boolean = repo.toggleFavorite(pkg)
}
