package com.kramphub.api

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchResult
import com.kramphub.domain.service.SearchService
import com.kramphub.infra.config.toJson
import com.kramphub.infra.exception.RemoteServiceException
import com.kramphub.infra.exception.ServiceCallException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import java.util.*

@WebFluxTest(SearchController::class)
class SearchControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var searchService: SearchService

    @Test
    fun `search returns BooksAndAlbums When Valid Query Is Provided`() {
        val query = "example query"
        val expectedBookTitle = "Book Title"
        val expectedAuthor = "Author"
        val expectedAlbumTitle = "Album Title"
        val expectedArtist = "Artist"

        whenever(searchService.search(any())).thenReturn(
            Mono.just(
                SearchResult(
                    books = listOf(Book(expectedBookTitle, listOf(expectedAuthor))),
                    albums = listOf(Album(expectedAlbumTitle, expectedArtist))
                )
            )
        )

        webTestClient
            .get()
            .uri("/search?query=$query")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<SearchResponse>()
            .consumeWith { response ->
                val searchResponse = response.responseBody
                assertNotNull(searchResponse)
                Assertions.assertThat(searchResponse)
                assertEquals(1, searchResponse?.books?.size)
                assertEquals(expectedBookTitle, searchResponse?.books?.firstOrNull()?.title)
                assertEquals(1, searchResponse?.books?.firstOrNull()?.authors?.size)
                assertEquals(expectedAuthor, searchResponse?.books?.firstOrNull()?.authors?.firstOrNull())

                assertEquals(1, searchResponse?.albums?.size)
                assertEquals(expectedAlbumTitle, searchResponse?.albums?.firstOrNull()?.title)
                assertEquals(expectedArtist, searchResponse?.albums?.firstOrNull()?.artist)
            }
    }

    @Test
    fun `test search endpoint with empty result`() {
        val query = "example query"

        whenever(searchService.search(any())).thenReturn(Mono.just(SearchResult(emptyList(), emptyList())))

        webTestClient
            .get()
            .uri("/search?query=$query")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<SearchResponse>()
            .consumeWith { response ->
                val searchResponse = response.responseBody
                assertNotNull(searchResponse)
                assertTrue(searchResponse?.books.isNullOrEmpty())
                assertTrue(searchResponse?.albums.isNullOrEmpty())
            }
    }

    @Test
    fun `test search endpoint with invalid query parameter`() {
        // Missing query parameter
        webTestClient
            .get()
            .uri("/search")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()

    }


    @Test
    fun `test search endpoint with error`() {
        val query = "example query"

        whenever(searchService.search(any())).thenReturn(Mono.error(Exception("Search error")))

        webTestClient
            .get()
            .uri("/search?query=$query")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody<ProblemDetail>()
    }

    @Test
    fun `test search endpoint with Bad Request`() {
        val query = ""

        whenever(searchService.search(any())).thenReturn(Mono.error(IllegalArgumentException("query can not be empty")))

        webTestClient
            .get()
            .uri("/search?query=$query")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody<ProblemDetail>()
    }

    @Test
    fun `test search endpoint with service call error`() {
        val query = "example query"

        whenever(searchService.search(any())).thenReturn(
            Mono.error(
                ServiceCallException(
                    ITunesErrorResponse(
                        code = HttpStatus.BAD_REQUEST.value().toString(),
                        status = HttpStatus.BAD_REQUEST.value().toString(),
                        detail = "Bad Request"
                    ).toJson(),
                    HttpStatus.BAD_REQUEST
                )
            )
        )

        webTestClient
            .get()
            .uri("/search?query=$query")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody<ProblemDetail>()
    }


    @Test
    fun `test search endpoint with remote call  error`() {
        val query = "example query"

        whenever(searchService.search(any())).thenReturn(
            Mono.error(
                RemoteServiceException(
                    ITunesErrorResponse().toJson(),
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            )
        )

        webTestClient
            .get()
            .uri("/search?query=$query")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody<ProblemDetail>()
    }


    data class ITunesErrorResponse(
        val id: String = UUID.randomUUID().toString(),
        val code: String = HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
        val detail: String = "Internal Error",
        val source: Any? = null,
        val status: String = HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
        val title: String = "Error Title"
    )

}