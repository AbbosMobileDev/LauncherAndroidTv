package uz.abisoft.launcherandroidtv.domain.usecase

import uz.abisoft.launcherandroidtv.domain.model.AppEntry
import uz.abisoft.launcherandroidtv.domain.repository.AppRepository

class GetLaunchableAppsUseCase(private val repo: AppRepository) {
    suspend operator fun invoke(): List<AppEntry> =
        repo.getLaunchableApps()
            .sortedWith(
                compareByDescending<AppEntry> { it.isFavorite }
                    .thenBy { it.label.lowercase() }
            )
}
