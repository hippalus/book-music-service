package com.kramphub.infra.service.googlebooks

import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.HttpStatusCodes
import com.google.api.services.books.v1.Books
import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.service.BooksService
import com.kramphub.infra.exception.RemoteServiceException
import com.kramphub.infra.exception.ServiceCallException
import com.kramphub.infra.service.googlebooks.client.GoogleApiProperties
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.micrometer.observation.annotation.Observed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

@Service
class BooksServiceGoogleImpl(
    private val googleBooks: Books,
    private val googleApiProperties: GoogleApiProperties
) : BooksService {
    
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
    
    @Retry(name = "google-books")
    @Bulkhead(name = "google-books")
    @CircuitBreaker(name = "google-books", fallbackMethod = "searchFallBack")
    @Observed(name = "search-google-books", contextualName = "searching-google-books")
    override fun search(criteria: SearchCriteria): Flux<Book> {
        return Flux.fromIterable(googleBooks(criteria))
            .subscribeOn(Schedulers.boundedElastic())
    }
    
    fun searchFallBack(criteria: SearchCriteria, ex: Exception): Flux<Book> {
        log.warn("#searchFallBack invoking for GoogleApiClient", ex)
        return Flux.empty()
    }
    
    private fun googleBooks(criteria: SearchCriteria): List<Book> {
        return try {
            val volumes: Books.Volumes = googleBooks.volumes()
            val volumesList = volumes.list(criteria.query)
                .setMaxResults(googleApiProperties.searchLimit.toLong())
            
            val execute = volumesList.execute()
            
            execute.items
                ?.map { it.volumeInfo }
                ?.map { Book(it.title, it.authors) }
                ?: listOf()
        } catch (ex: Exception) {
            when (ex) {
                is HttpResponseException -> {
                    throw convertGoogleApiError(ex)
                }
                
                else -> {
                    throw ex
                }
            }
        }
        
    }
    
    private fun convertGoogleApiError(ex: HttpResponseException): Throwable {
        if (ex.is4xxClientError) {
            return ServiceCallException(ex.content, HttpStatusCode.valueOf(ex.statusCode))
        }
        if (ex.is5xxServerError) {
            return RemoteServiceException(ex.content, HttpStatusCode.valueOf(ex.statusCode))
        }
        return ex
    }
    
}

val HttpResponseException.is4xxClientError: Boolean
    get() = statusCode >= HttpStatusCodes.STATUS_CODE_BAD_REQUEST && statusCode < HttpStatusCodes.STATUS_CODE_SERVER_ERROR

val HttpResponseException.is5xxServerError: Boolean
    get() = statusCode >= HttpStatusCodes.STATUS_CODE_SERVER_ERROR