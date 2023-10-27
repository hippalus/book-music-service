package com.kramphub.infra.service.googlebooks

import com.google.api.services.books.v1.Books
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.service.BooksService
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

@Service
class BooksServiceGoogleImpl(
    private val googleBooks: Books
) : BooksService {

    @Retry(name = "google-books")
    @Bulkhead(name = "google-books")
    @CircuitBreaker(name = "google-books")
    override fun search(criteria: SearchCriteria): Flux<Book> {
        return Flux.fromIterable(googleBooks(criteria))
            .subscribeOn(Schedulers.boundedElastic())
    }

    private fun googleBooks(criteria: SearchCriteria): List<Book> {
        val volumes: Books.Volumes = googleBooks.volumes()
        val volumesList = volumes.list(criteria.query)
            .setMaxResults(5)

        val execute = volumesList.execute()

        return (execute.items
            ?.map { it.volumeInfo }
            ?.map { Book(it.title, it.authors) }
            ?: listOf())
    }
}