package com.kramphub.api

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchResult

data class SearchResponse(
    val books: List<BookResponse>,
    val albums: List<AlbumResponse>
) {

    companion object {

        fun from(searchResult: SearchResult): SearchResponse = SearchResponse(
            books = searchResult.books.map { BookResponse.from(it) },
            albums = searchResult.albums.map { AlbumResponse.from(it) }
        )
    }
}

data class BookResponse(val title: String, val authors: List<String>?) {

    companion object {

        fun from(book: Book): BookResponse = BookResponse(book.title, book.authors)
    }
}

data class AlbumResponse(val title: String, val artist: String?) {

    companion object {

        fun from(album: Album): AlbumResponse = AlbumResponse(album.title, album.artist)
    }
}
