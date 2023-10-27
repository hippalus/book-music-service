package com.kramphub.infra.service.googlebooks

import com.google.api.client.http.HttpHeaders
import com.google.api.client.http.HttpResponseException
import com.google.api.services.books.v1.Books
import com.google.api.services.books.v1.model.Volume
import com.google.api.services.books.v1.model.Volumes
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.infra.exception.RemoteServiceException
import com.kramphub.infra.exception.ServiceCallException
import com.kramphub.infra.service.googlebooks.client.GoogleApiProperties
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import reactor.test.StepVerifier

class BooksServiceGoogleImplTest {

    private lateinit var googleBooks: Books

    private lateinit var booksService: BooksServiceGoogleImpl

    @BeforeEach
    fun setup() {
        // Mock the Google Books API client
        googleBooks = mock()
        val googleApiProperties = GoogleApiProperties()
        // Create the service with the mock client
        booksService = BooksServiceGoogleImpl(googleBooks, googleApiProperties)
    }

    @Test
    fun `search should return books from Google Books API`() {
        // Create a mock search result
        val searchCriteria = SearchCriteria("query")

        val volumeInfo1 = Volume.VolumeInfo().setTitle("Book 1").setAuthors(listOf("Author 1"))
        val volumeInfo2 = Volume.VolumeInfo().setTitle("Book 2").setAuthors(listOf("Author 2"))

        val items = listOf(Volume().setVolumeInfo(volumeInfo1), Volume().setVolumeInfo(volumeInfo2))

        val volumes: Books.Volumes = mock()
        val volumesList: Books.Volumes.List = mock()

        whenever(googleBooks.volumes()).thenReturn(volumes)
        whenever(volumes.list(searchCriteria.query)).thenReturn(volumesList)
        whenever(volumesList.setMaxResults(5)).thenReturn(volumesList)
        whenever(volumesList.execute()).thenReturn(Volumes().setItems(items))

        // Invoke the search method
        val result = booksService.search(searchCriteria)

        // Verify the result
        StepVerifier.create(result)
            .expectNext(Book("Book 1", listOf("Author 1")))
            .expectNext(Book("Book 2", listOf("Author 2")))
            .verifyComplete()
    }

    @Test
    fun testSearch_With4xxClientError() {
        val criteria = SearchCriteria("Java")
        val errorMessage = "Bad Request"
        val statusCode = HttpStatus.BAD_REQUEST.value()

        val responseException = HttpResponseException.Builder(statusCode, errorMessage, HttpHeaders())
            .build()

        val volumes: Books.Volumes = mock()
        val volumesList: Books.Volumes.List = mock()

        whenever(googleBooks.volumes()).thenReturn(volumes)
        whenever(volumes.list(criteria.query)).thenReturn(volumesList)
        whenever(volumesList.setMaxResults(5)).thenReturn(volumesList)
        whenever(volumesList.execute()).thenThrow(responseException)


        Assertions.assertThatThrownBy { booksService.search(criteria) }
            .isInstanceOf(ServiceCallException::class.java)
    }

    @Test
    fun testSearch_With5xxServerError() {
        val criteria = SearchCriteria("Java")
        val errorMessage = "Internal Server Error"
        val statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()

        val responseException = HttpResponseException.Builder(statusCode, errorMessage, HttpHeaders())
            .build()

        val volumes: Books.Volumes = mock()
        val volumesList: Books.Volumes.List = mock()

        whenever(googleBooks.volumes()).thenReturn(volumes)
        whenever(volumes.list(criteria.query)).thenReturn(volumesList)
        whenever(volumesList.setMaxResults(5)).thenReturn(volumesList)
        whenever(volumesList.execute()).thenThrow(responseException)

        Assertions.assertThatThrownBy { booksService.search(criteria) }
            .isInstanceOf(RemoteServiceException::class.java)
    }
}