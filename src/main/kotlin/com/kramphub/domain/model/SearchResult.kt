package com.kramphub.domain.model

data class SearchResult(
    val books: List<Book>,
    val albums: List<Album>
)
