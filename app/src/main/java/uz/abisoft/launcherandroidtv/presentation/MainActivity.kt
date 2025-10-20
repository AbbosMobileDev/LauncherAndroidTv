package uz.abisoft.launcherandroidtv.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.viewmodel.ext.android.viewModel
import uz.abisoft.launcherandroidtv.presentation.ui.HomeScreen
import uz.abisoft.launcherandroidtv.presentation.ui.theme.LauncherAndroidTvTheme
import uz.abisoft.launcherandroidtv.utils.LauncherUtils

class MainActivity : ComponentActivity() {
    private val vm: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!LauncherUtils.isDefaultLauncher(this)) {
            LauncherUtils.showDefaultLauncherInstructions(this)
        }
        setContent {
            LauncherAndroidTvTheme {
                LaunchedEffect(Unit) { vm.dispatch(HomeIntent.Load) }
                HomeScreen(vm = vm)
            }
        }
    }
}