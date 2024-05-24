package presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.AutoCompleteItemList
import data.WordList
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WordViewModel : ViewModel() {
    //    private var _wordListJSON: MutableStateFlow<String?> = MutableStateFlow(null)
    private var randomWordListStateFlow = MutableStateFlow<WordList>(WordList(emptyList()))
    val randmoWordListState = randomWordListStateFlow.asStateFlow()

    private var quickSearchStateFlow = MutableStateFlow<WordList>(WordList(emptyList()))
    val quickSearchListState = quickSearchStateFlow.asStateFlow()

    private var searchStateFlow = MutableStateFlow<WordList>(WordList(emptyList()))
    val searchListState = searchStateFlow.asStateFlow()

    private var autoCompleteListStateFlow = MutableStateFlow<AutoCompleteItemList>(AutoCompleteItemList(emptyList()))
    val searchAutoCompleteListState = autoCompleteListStateFlow.asStateFlow()

    var errorHasOccured by mutableStateOf(false)

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    /**
     * @param trailingUrl url after "https://api.urbandictionary.com/v0/"
     * */
    private suspend inline fun <reified T> fetch(trailingUrl: String): T? {
        return try {
            httpClient
                .get("https://api.urbandictionary.com/v0/$trailingUrl")
                .body<T>()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun updateRandom() {
        viewModelScope.launch {
            val random = fetch<WordList>("random")
            if (random != null) {
                randomWordListStateFlow.update {
                    it.copy(list = random.list)
                }
            } else {
                errorHasOccured = true
            }
        }
    }

    suspend fun updateQuickSearchResults(term: String) {
        viewModelScope.launch {
            val random = fetch<WordList>("define?term=$term")
            if (random != null) {
                quickSearchStateFlow.update {
                    it.copy(list = random.list)
                }
            } else {
                errorHasOccured = true
            }
        }
    }

    suspend fun updateSearchResults(term: String) {
        viewModelScope.launch {
            val random = fetch<WordList>("define?term=$term")
            if (random != null) {
                searchStateFlow.update {
                    it.copy(list = random.list)
                }
            } else {
                errorHasOccured = true
            }
        }
    }

    suspend fun updateSearchAutoCompleteList(term: String) {
        viewModelScope.launch {
            val random = fetch<AutoCompleteItemList>("autocomplete-extra?term=$term")
            if (random != null) {
                autoCompleteListStateFlow.update {
                    it.copy(results = random.results)
                }
            } else {
                errorHasOccured = true
            }
        }
    }

    override fun onCleared() = httpClient.close()

}

