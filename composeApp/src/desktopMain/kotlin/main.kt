import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import urbandictionary.composeapp.generated.resources.Res
import urbandictionary.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Urban Dictionary",
        icon = painterResource(Res.drawable.compose_multiplatform)
    ) {
        App()
    }
}