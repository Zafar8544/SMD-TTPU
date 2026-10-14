package uz.ttpu.weatherpatterns

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.weatherpatterns.mvc.view.MvcActivity
import uz.ttpu.weatherpatterns.mvi.view.MviActivity
import uz.ttpu.weatherpatterns.mvp.view.MvpActivity
import uz.ttpu.weatherpatterns.mvvm.view.MvvmActivity
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPatternsTheme {
                MainLauncherScreen(
                    onOpenMvc = { startActivity(Intent(this, MvcActivity::class.java)) },
                    onOpenMvp = { startActivity(Intent(this, MvpActivity::class.java)) },
                    onOpenMvvm = { startActivity(Intent(this, MvvmActivity::class.java)) },
                    onOpenMvi = { startActivity(Intent(this, MviActivity::class.java)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLauncherScreen(
    onOpenMvc: () -> Unit,
    onOpenMvp: () -> Unit,
    onOpenMvvm: () -> Unit,
    onOpenMvi: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Weather Patterns — Architectures") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Select Architectural Pattern:",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onOpenMvc,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("1. MVC (Model-View-Controller)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOpenMvp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("2. MVP (Model-View-Presenter)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOpenMvvm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("3. MVVM (Model-View-ViewModel)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOpenMvi,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("4. MVI (Model-View-Intent)")
            }
        }
    }
}
