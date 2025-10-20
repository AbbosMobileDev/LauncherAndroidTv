package uz.abisoft.launcherandroidtv.domain.usecase

import uz.abisoft.launcherandroidtv.domain.repository.AppRepository

class SaveLastLaunchedUseCase(private val repo: AppRepository) {
    operator fun invoke(pkg: String) = repo.saveLastLaunched(pkg)
}
