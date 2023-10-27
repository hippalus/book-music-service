package com.kramphub.infra.service.itunes

import com.kramphub.domain.model.SearchCriteria
import com.kramphub.infra.service.itunes.client.ITunesClient
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.*
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class ITunesClientResiliencyTest {
    
    @Autowired
    private lateinit var albumsServiceITunesImpl: AlbumsServiceITunesImpl
    
    @MockBean
    private lateinit var iTunesClient: ITunesClient
    
    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry
    
    
    @BeforeEach
    fun beforeEach() {
        circuitBreakerRegistry.circuitBreaker("itunes").transitionToClosedState()
    }
    
    @AfterEach
    fun afterEach() {
        circuitBreakerRegistry.circuitBreaker("itunes").transitionToClosedState()
    }
    
    @Test
    @Disabled
    fun `will not call ITunes and then fallback when the circuit breaker is open`() {
        
        // given: circuit breaker open
        circuitBreakerRegistry.circuitBreaker("itunes").transitionToOpenState()
        
        
        val fallBackAlbums = albumsServiceITunesImpl.search(SearchCriteria("query"))
        
        Assertions.assertTrue(fallBackAlbums.collectList().block()?.isEmpty() ?: false)
        
        // and: ITunes is not called
        verify(iTunesClient, times(0)).search(any(), any(), any(), any(), any(), any(), any(), any(), any())
    }
    
}