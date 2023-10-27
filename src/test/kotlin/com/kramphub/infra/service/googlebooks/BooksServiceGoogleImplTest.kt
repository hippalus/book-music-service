package com.kramphub.infra.service.googlebooks

import com.google.api.services.books.v1.Books
import com.google.api.services.books.v1.model.Volume
import com.google.api.services.books.v1.model.Volumes
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import reactor.test.StepVerifier

class BooksServiceGoogleImplTest {

    private lateinit var googleBooks: Books

    private lateinit var booksService: BooksServiceGoogleImpl

    @BeforeEach
    fun setup() {
        // Mock the Google Books API client
        googleBooks = mock(Books::class.java)

        // Create the service with the mock client
        booksService = BooksServiceGoogleImpl(googleBooks)
    }

    @Test
    fun `search should return books from Google Books API`() {
        // Create a mock search result
        val searchCriteria = SearchCriteria("query")

        val volumeInfo1 = Volume.VolumeInfo().setTitle("Book 1").setAuthors(listOf("Author 1"))
        val volumeInfo2 = Volume.VolumeInfo().setTitle("Book 2").setAuthors(listOf("Author 2"))

        val items = listOf(Volume().setVolumeInfo(volumeInfo1), Volume().setVolumeInfo(volumeInfo2))

        val volumes = mock(Books.Volumes::class.java)
        val volumesList = mock(Books.Volumes.List::class.java)

        `when`(googleBooks.volumes()).thenReturn(volumes)
        `when`(volumes.list(searchCriteria.query)).thenReturn(volumesList)
        `when`(volumesList.setMaxResults(5)).thenReturn(volumesList)
        `when`(volumesList.execute()).thenReturn(Volumes().setItems(items))

        // Invoke the search method
        val result = booksService.search(searchCriteria)

        // Verify the result
        StepVerifier.create(result)
            .expectNext(Book("Book 1", listOf("Author 1")))
            .expectNext(Book("Book 2", listOf("Author 2")))
            .verifyComplete()
    }

}