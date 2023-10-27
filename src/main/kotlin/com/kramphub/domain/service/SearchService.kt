package com.kramphub.domain.service

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.model.SearchResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SearchService(
    private val booksService: BooksService,
    private val albumsService: AlbumsService,
) {

    fun search(criteria: SearchCriteria): Mono<SearchResult> {
        val books: Mono<List<Book>> = booksService.search(criteria).collectList()
        val albums: Mono<List<Album>> = albumsService.search(criteria).collectList()

        return Mono.zip(books, albums).map { tuple -> SearchResult(tuple.t1, tuple.t2) }
    }

}