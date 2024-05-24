package presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import domain.*
import getPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var showSearchDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val platformIsAndroid = getPlatform().name.toLowerCase(Locale.current).contains("android")

    val viewModel = getViewModel(Unit, viewModelFactory { WordViewModel() })

    val randomWordList by viewModel.randmoWordListState.collectAsState()

    var quickSearchUsed by remember { mutableStateOf(false) }
    val quickSearchList by viewModel.quickSearchListState.collectAsState()
    var quickSearchQuery by remember { mutableStateOf("urbandictionary") }

    val searchAutoCompleteItemList by viewModel.searchAutoCompleteListState.collectAsState()

    var isShowingSearchResult by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResultsList by viewModel.searchListState.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.updateRandom()
        }
    }
    LaunchedEffect(quickSearchQuery) {
        withContext(Dispatchers.IO) {
            viewModel.updateQuickSearchResults(quickSearchQuery)
        }
    }
    LaunchedEffect(searchQuery) {
        withContext(Dispatchers.IO) {
            viewModel.updateSearchAutoCompleteList(searchQuery)
        }
    }
    LaunchedEffect(isShowingSearchResult) {
        withContext(Dispatchers.IO) {
            viewModel.updateSearchResults(searchQuery)
        }
    }
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceBright),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "UrbanDictionary.com",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                },
                actions = {
                    IconButton(
                        imageVector = Icons.Outlined.Search,
                        onClick = { showSearchDialog = true }
                    )
                }
            )
        }
    ) { scaffoldPadding ->
        if (viewModel.errorHasOccured) {
           ErrorCard(scaffoldPadding)
        } else {
            Column(Modifier.padding(scaffoldPadding)) {
                AnimatedVisibility(isShowingSearchResult) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                isShowingSearchResult = false
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }

                        Text(
                            buildAnnotatedString {
                                append("Showing results for \"")
                                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                                    append(searchQuery.trim())
                                }
                                append("\"")
                            }
                        )
                    }
                }
                AnimatedVisibility(platformIsAndroid && quickSearchUsed) {
                    QuickSearchCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        wordList = quickSearchList.list,
                        sidebarUpdater = {
                            quickSearchQuery = it
                            quickSearchUsed = true
                        },
                        platformIsAndroid = true
                    )
                }

                Row {
                    Column(
                        modifier = Modifier.fillMaxWidth(
                            if (!platformIsAndroid && quickSearchUsed) 0.80f else 1f
                        )
                    ) {
                        LazyVerticalStaggeredGrid(
                            modifier = Modifier.padding(8.dp),
                            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
                            verticalItemSpacing = 8.dp,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                when (isShowingSearchResult) {
                                    true -> searchResultsList.list
                                    false -> randomWordList.list
                                }
                            ) { word ->
                                WordCard(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    word = word,
                                    sidebarUpdater = {
                                        quickSearchQuery = it
                                        quickSearchUsed = true
                                    }
                                )
                            }
                        }
                    }
                    AnimatedVisibility(quickSearchList.list.isNotEmpty() && !platformIsAndroid && quickSearchUsed) {
                        QuickSearchCard(
                            modifier = Modifier
                                .then(
                                    when (platformIsAndroid) {
                                        false -> Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())

                                        else -> Modifier.fillMaxWidth()
                                    }
                                )
                                .padding(8.dp),
                            wordList = quickSearchList.list,
                            sidebarUpdater = {
                                quickSearchQuery = it
                                quickSearchUsed = true
                            },
                            platformIsAndroid = false
                        )
                    }
                }
                if (showAboutDialog) {
                    ContactAuthorDialog { showAboutDialog = false }
                }
                if (showSearchDialog) {
                    SearchDialog(
                        onDismissRequest = { showSearchDialog = false },
                        autocompletion = searchAutoCompleteItemList.results,
                        onQueryUpdate = { searchQuery = it },
                        onQuerySubmission = {
                            searchQuery = it
                            isShowingSearchResult = true
                        }
                    )
                }
            }
        }
    }
}

