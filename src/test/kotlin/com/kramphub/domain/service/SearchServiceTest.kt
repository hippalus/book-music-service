package com.kramphub.domain.service

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.model.SearchResult
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class SearchServiceTest {

    val mockBooksService: BooksService = mock()
    val mockAlbumsService: AlbumsService = mock()

    @Test
    fun `test search method`() {
        val criteria = SearchCriteria("example query")

        val book1 = Book("Book 1", listOf("Author 1"))
        val book2 = Book("Book 2", listOf("Author 2"))
        val album1 = Album("Album 1", "Artist 1")
        val album2 = Album("Album 2", "Artist 2")
        val expectedSearchResult = SearchResult(listOf(book1, book2), listOf(album1, album2))

        whenever(mockBooksService.search(any())).thenReturn(Flux.just(book1, book2))
        whenever(mockAlbumsService.search(any())).thenReturn(Flux.just(album1, album2))

        val searchService = SearchService(mockBooksService, mockAlbumsService)
        val result = searchService.search(criteria)

        StepVerifier.create(result)
            .expectNext(expectedSearchResult)
            .verifyComplete()
    }

    @Test
    fun `test search method with empty result`() {
        val criteria = SearchCriteria("example query")

        whenever(mockBooksService.search(any())).thenReturn(Flux.empty())
        whenever(mockAlbumsService.search(any())).thenReturn(Flux.empty())

        val searchService = SearchService(mockBooksService, mockAlbumsService)
        val result = searchService.search(criteria)

        StepVerifier.create(result)
            .expectNextMatches { it.books.isEmpty() && it.albums.isEmpty() }
            .verifyComplete()
    }

    @Test
    fun `test search method with error`() {
        val criteria = SearchCriteria("example query")

        whenever(mockBooksService.search(any())).thenReturn(Flux.error(Exception("Books service error")))
        whenever(mockAlbumsService.search(any())).thenReturn(Flux.empty())

        val searchService = SearchService(mockBooksService, mockAlbumsService)
        val result = searchService.search(criteria)

        StepVerifier.create(result)
            .expectError(Exception::class.java)
            .verify()
    }
}