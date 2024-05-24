package data

import kotlinx.serialization.Serializable


@Serializable
data class WordList(
    val list: List<Word>
)

@Serializable
data class Word(
    val definition: String= "",
    val permalink: String= "",
    val thumbs_up: Int= 0,
    val author: String= "",
    val word: String= "",
    val defid: Int= 0,
    val current_vote: String= "",
    val written_on: String= "",
    val example: String= "",
    val thumbs_down: Int= 0,
)

@Serializable
data class AutoCompleteItem(
    val preview: String,
    val term: String
)

@Serializable
data class AutoCompleteItemList(
    val results: List<AutoCompleteItem>
)

