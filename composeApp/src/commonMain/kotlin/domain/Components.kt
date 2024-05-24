package domain

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import data.AutoCompleteItem
import data.Word

@Composable
fun IconButton(
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}


@Composable
fun QuickSearchCard(
    term: String = "",
    modifier: Modifier,
    wordList: List<Word>,
    sidebarUpdater: (String) -> Unit,
    platformIsAndroid: Boolean
) {
    var index by remember(term) { mutableStateOf(0) }

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Green.copy(alpha = 0.1f))
            .padding(12.dp)
            .animateContentSize()
    ) {
        Text(
            text = wordList[index].word,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height((MaterialTheme.typography.labelLarge.fontSize.value * 1.2).dp))

        lateinit var definition: AnnotatedString
        ClickableText(
            text = wordList[index].definition.urbanize().also { definition = it },
            style = MaterialTheme.typography.labelLarge,
            onClick = { offset ->
                definition
                    .getStringAnnotations(tag = "term", start = offset, end = offset)
                    .firstOrNull()?.let {
                        sidebarUpdater(it.item)
                    }
            }
        )
        if (!platformIsAndroid) {
            Spacer(Modifier.height((MaterialTheme.typography.labelLarge.fontSize.value * 1.2).dp))
            Text(
                text = "e.g.:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = wordList[index].example.urbanize(),
                style = MaterialTheme.typography.labelLarge
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                //            modifier = Modifier.,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val alpha = 0.33f
                IconButton(onClick = { index = (index - 1 + wordList.size) % wordList.size }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = null,
                        tint = LocalContentColor.current.copy(alpha = alpha)
                    )
                }
                Box(Modifier.size(4.dp).clip(CircleShape).background(Color.Green.copy(alpha = alpha)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color.Green.copy(alpha = alpha)))
                Box(Modifier.size(4.dp).clip(CircleShape).background(Color.Green.copy(alpha = alpha)))

                IconButton(onClick = { index = (index + 1 + wordList.size) % wordList.size }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = LocalContentColor.current.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

@Composable
fun WordCard(
    modifier: Modifier = Modifier,
    word: Word,
    shortCard: Boolean = false,
    sidebarUpdater: (String) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .padding(12.dp)
    ) {
        Text(
            text = word.word,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height((MaterialTheme.typography.labelLarge.fontSize.value * 1.2).dp))

        lateinit var definition: AnnotatedString

        ClickableText(
            text = word.definition.urbanize().also { definition = it },
            style = MaterialTheme.typography.labelLarge,
            onClick = { offset ->
                definition
                    .getStringAnnotations(tag = "term", start = offset, end = offset)
                    .firstOrNull()?.let {
                        sidebarUpdater(it.item)
                    }
            }
        )
        if (!shortCard) {
            Spacer(Modifier.height((MaterialTheme.typography.labelLarge.fontSize.value * 1.2).dp))
            Text(
                text = "e.g.:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = word.example.urbanize(),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
    onDismissRequest: () -> Unit,
    onQueryUpdate: (String) -> Unit,
    autocompletion: List<AutoCompleteItem>,
    onQuerySubmission: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val btnIsEnabled by remember { derivedStateOf { query.trim().isNotEmpty() } }

    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Column(
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.End
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = {
                    query = it
                    onQueryUpdate(it)
                }
            )
            if (autocompletion.isNotEmpty()) {
                val t = mutableListOf<AutoCompleteItem>()

                for (i in 0 until min(4, autocompletion.size)) {
                    t.add(autocompletion[i])
                }

                t.forEach {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onDismissRequest()
                                onQuerySubmission(it.term)
                            }
                            .padding(16.dp),
                        text = it.term
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Row {
                TextButton(
                    onClick = { onDismissRequest() }
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        onQuerySubmission(query.trim())
                        onDismissRequest()
                    },
                    enabled = btnIsEnabled
                ) {
                    Text("Search")
                }
            }
        }
    }
}

fun min(a: Int, b: Int): Int {
    if (a < b) return a
    return b
}

@Composable
fun ErrorCard(scaffoldPadding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .width(400.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.42f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning, null
                )
                Text("An error occured!")
            }
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Some error occured. Please make sure,\n"
                            + "    - you have stable internet connection\n"
                            + "    - the website (urbandictionary.com) is up\n"
                            + "    - you haven't lost contact with the developer\n"
                            + "\n"
                            + "If you are still having to struggle,\n"
                            + "    - close and re-open this app\n"
                            + "    - file an issue on github"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactAuthorDialog(
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Column(
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = buildAnnotatedString {
                    val coloredSpanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    append("This app is build for ease in using the ")
                    withStyle(coloredSpanStyle) { append("Urban Dictionaty") }
                    append(" by ")
                    withStyle(coloredSpanStyle) { append("off_by_a_bit") }

                    append(" for personal usage.")
                    append("\n\n\n")

                    append(
                        "If you are using this and you are not the developer," +
                                " you should know the developer by face. If you have any" +
                                " inquiry or feature request, feel free to offer the " +
                                "developer a coffee and discuss your thoughts."
                    )
                },
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Sure!")
            }
        }
    }
}