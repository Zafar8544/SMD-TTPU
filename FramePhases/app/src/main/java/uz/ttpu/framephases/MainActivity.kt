package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FramePhasesTheme {
                FramePhasesApp()
            }
        }
    }
}

@Composable
fun FramePhasesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FramePhasesApp() {
    val demos = listOf(
        "LectureTree",
        "PaddingDemo",
        "OffsetDemo",
        "ColorDemo",
        "ParallaxSlow",
        "ParallaxFast",
        "ScrollToTopNaive",
        "Parent",
        "CyclicDemo",
        "CyclicFixed",
        "StackBelow",
    )

    var selected by remember { mutableStateOf(demos.first()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FramePhases") },
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(demos) { demo ->
                        AssistChip(
                            onClick = { selected = demo },
                            label = { Text(demo) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selected) {
                    "LectureTree" -> LectureTree()
                    "PaddingDemo" -> PaddingDemo()
                    "OffsetDemo" -> OffsetDemo()
                    "ColorDemo" -> ColorDemo()
                    "ParallaxSlow" -> ParallaxSlow()
                    "ParallaxFast" -> ParallaxFast()
                    "ScrollToTopNaive" -> ScrollToTopNaive()
                    "Parent" -> Parent()
                    "CyclicDemo" -> CyclicDemo()
                    "CyclicFixed" -> CyclicFixed()
                    "StackBelow" -> StackBelow(
                        modifier = Modifier,
                        spacing = 8.dp,
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(androidx.compose.ui.graphics.Color(0xFF6650A4)),
                        )
                        androidx.compose.material3.Text("I'm below the image")
                    }
                }
            }
        },
    )
}
