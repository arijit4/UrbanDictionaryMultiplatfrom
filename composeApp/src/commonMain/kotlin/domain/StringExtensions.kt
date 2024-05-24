package domain

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle


@Composable
fun String.urbanize() = buildAnnotatedString {
    val pattern = "\\[(.*?)]".toRegex()
    val extractedWords = pattern.findAll(this@urbanize).map { it.groupValues[1] }

    this@urbanize.split(pattern).forEachIndexed { index, text ->
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
            append(text)
        }
        if (index < extractedWords.count()) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                val term = extractedWords.elementAt(index).trim()
                pushStringAnnotation(
                    tag = "term",
                    annotation = term
                )
                append(term)
                pop()
            }
        }
    }
}
