package uz.abisoft.launcherandroidtv

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import uz.abisoft.launcherandroidtv.data.Prefs
import uz.abisoft.launcherandroidtv.data.repository.AppRepositoryImpl
import uz.abisoft.launcherandroidtv.domain.repository.AppRepository
import uz.abisoft.launcherandroidtv.domain.usecase.GetLaunchableAppsUseCase
import uz.abisoft.launcherandroidtv.domain.usecase.SaveLastLaunchedUseCase
import uz.abisoft.launcherandroidtv.domain.usecase.ToggleFavoriteUseCase
import uz.abisoft.launcherandroidtv.presentation.HomeViewModel

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val appModule = module {
            single { Prefs(androidContext()) }
            single<AppRepository> { AppRepositoryImpl(androidContext(), get()) }
            factory { GetLaunchableAppsUseCase(get()) }
            factory { ToggleFavoriteUseCase(get()) }
            factory { SaveLastLaunchedUseCase(get()) }
            viewModel { HomeViewModel(get(), get(), get()) }
        }
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}