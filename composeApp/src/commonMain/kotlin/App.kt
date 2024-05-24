import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import presentation.HomeScreen
import ui.darkScheme

@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkScheme
    ) {
        HomeScreen()
    }
}
