package com.kramphub.infra.service.googlebooks

import com.google.api.services.books.v1.Books
import com.kramphub.domain.model.SearchCriteria
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class GoogleClientResiliencyTest {
    
    @Autowired
    private lateinit var booksServiceGoogleImpl: BooksServiceGoogleImpl
    
    @MockBean
    private lateinit var googleBooks: Books
    
    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry
    
    
    @BeforeEach
    fun beforeEach() {
        circuitBreakerRegistry.circuitBreaker("google-books").transitionToClosedState()
    }
    
    @AfterEach
    fun afterEach() {
        circuitBreakerRegistry.circuitBreaker("google-books").transitionToClosedState()
    }
    
    @Test
    fun `will not call Google and then fallback when the circuit breaker is open`() {
        
        // given: circuit breaker open
        circuitBreakerRegistry.circuitBreaker("google-books").transitionToOpenState()
        
        
        val fallBackBooks = booksServiceGoogleImpl.search(SearchCriteria("query"))
        
        Assertions.assertTrue(fallBackBooks.collectList().block()?.isEmpty() ?: false)
        
        // and: Google is not called
        verify(googleBooks, times(0)).volumes()
    }
    
    
}