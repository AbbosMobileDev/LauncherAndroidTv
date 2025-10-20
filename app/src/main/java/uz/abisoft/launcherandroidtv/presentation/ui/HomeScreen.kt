package uz.abisoft.launcherandroidtv.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uz.abisoft.launcherandroidtv.presentation.HomeIntent
import uz.abisoft.launcherandroidtv.presentation.HomeViewModel
import uz.abisoft.launcherandroidtv.presentation.ui.components.AppCard
import uz.abisoft.launcherandroidtv.utils.LauncherUtils
import uz.abisoft.launcherandroidtv.utils.launchApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(vm: HomeViewModel) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current
    val configuration = LocalConfiguration.current
    val gridState = rememberLazyGridState()

    // Vaqt va sana state lari
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDate by remember { mutableStateOf(getCurrentDateUzbek()) }
    var isDefaultLauncher by remember { mutableStateOf(LauncherUtils.isDefaultLauncher(ctx)) }
    // Vaqtni yangilash
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentTime()
            currentDate = getCurrentDateUzbek()
            if (System.currentTimeMillis() % 10000 == 0L) {
                isDefaultLauncher = LauncherUtils.isDefaultLauncher(ctx)
            }
        }
    }
    val isTv = configuration.smallestScreenWidthDp >= 600
    val padding = if (isTv) 24.dp else 16.dp
    val cardSize = if (isTv) 220.dp else 160.dp

    val darkGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1a2a6c),  // To'q ko'k
            Color(0xFF3C43CE),  // Moviy
            Color(0xFF2D30FD)   // Ko'k
        )
    )

    val tvGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0f0c29),
            Color(0xFF302b63),
            Color(0xFF24243e)
        )
    )

    val gradientBrush = if (isTv) tvGradient else darkGradient

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dasturlar",
                    style = TextStyle(
                        fontSize = if (isTv) 28.sp else 24.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )

                )
                if (!isDefaultLauncher) {
                    Text(
                        text = "⚠️ Default emas",
                        color = Color.Yellow,
                        fontSize = 12.sp
                    )
                }


                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currentTime,
                        style = TextStyle(
                            fontSize = if (isTv) 20.sp else 16.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = currentDate,
                        style = TextStyle(
                            fontSize = if (isTv) 14.sp else 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { vm.dispatch(HomeIntent.Search(it)) },
                    label = { Text("Qidirish", color = Color.White) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.weight(1f),
                )
                Button(
                    onClick = {
                        if (isDefaultLauncher) {
                            LauncherUtils.clearLauncherDefaults(ctx)
                        } else {
                            LauncherUtils.openLauncherSettings(ctx)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDefaultLauncher) Color.Green else Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(height = 48.dp, width = if (isTv) 140.dp else 120.dp)
                ) {
                    Text(
                        text = if (isDefaultLauncher) "✅ Default" else "Set\nLauncher",
                        fontSize = if (isTv) 12.sp else 10.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }
                Button(
                    onClick = { vm.dispatch(HomeIntent.Load) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue.copy(alpha = 0.9f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Yangilash")
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(16.dp))
                            Text("Ilovalar yuklanmoqda...", color = Color.White)
                        }
                    }
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Xato: ${state.error}", color = Color(0xFFFF6B6B))
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { vm.dispatch(HomeIntent.Load) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Qayta urinish")
                        }
                    }
                }

                state.filtered.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentTime,
                                style = TextStyle(
                                    fontSize = if (isTv) 48.sp else 36.sp,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = currentDate,
                                style = TextStyle(
                                    fontSize = if (isTv) 20.sp else 16.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Ilovalar topilmadi",
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            if (state.filtered.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = cardSize),
                    state = gridState,
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.filtered) { app ->
                        AppCard(
                            modifier = Modifier.padding(4.dp),
                            item = app,
                            onClick = {
                                val ok = launchApp(ctx, app.packageName, app.className)
                                if (ok) vm.dispatch(HomeIntent.Launched(app.packageName))
                            },
                            onLongClick = {
                                vm.dispatch(HomeIntent.ToggleFav(app.packageName))
                                val message =
                                    if (app.isFavorite) "✗ Favoritdan olindi" else "★ Favoritga qo'shildi"
                                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return timeFormat.format(Date())
}

private fun getCurrentDateUzbek(): String {
    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("uz", "UZ"))
    return dateFormat.format(Date())
}