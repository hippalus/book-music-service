package com.kramphub.infra.service.itunes


import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.kramphub.domain.model.Album
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.infra.exception.RemoteServiceException
import com.kramphub.infra.exception.ServiceCallException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import wiremock.org.apache.hc.core5.http.HttpHeaders
import java.util.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlbumsServiceITunesImplTest {

    @Autowired
    private lateinit var albumsServiceITunesImpl: AlbumsServiceITunesImpl

    companion object {
        private val wireMockServer: WireMockServer = WireMockServer(9994)

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("book-album-api.services.itunes.url") { wireMockServer.baseUrl() }
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            wireMockServer.start()
            wireMockServer.stubFor(
                get(urlMatching("/search\\??(?:&?[^=&]*=[^=&]*)*"))
                    .willReturn(
                        aResponse()
                            .withBodyFile("albums.json")
                            .withHeader(HttpHeaders.CONTENT_TYPE, "text/javascript;UTF-8")
                            .withStatus(HttpStatus.OK.value())
                    )
            )
            wireMockServer.stubFor(
                get(urlMatching("/search\\??(?:&?[^=&]*=[^=&]*)*"))
                    .willReturn(
                        aResponse()
                            .withBodyFile("albums.json")
                            .withHeader(HttpHeaders.CONTENT_TYPE, "text/javascript;UTF-8")
                            .withStatus(HttpStatus.OK.value())
                    )
            )

        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            wireMockServer.stop()
        }
    }

    @Test
    fun `search should return albums from ITunesClient`() {
        wireMockServer.stubFor(
            get(urlMatching("/search\\??(?:&?[^=&]*=[^=&]*)*"))
                .willReturn(
                    aResponse()
                        .withBodyFile("albums.json")
                        .withHeader(HttpHeaders.CONTENT_TYPE, "text/javascript;UTF-8")
                        .withStatus(HttpStatus.OK.value())
                )
        )
        // Create test data
        val criteria = SearchCriteria(query = "Queen")

        // Perform the search
        val resultFlux: Flux<Album> = albumsServiceITunesImpl.search(criteria)

        // Verify the result
        StepVerifier.create(resultFlux)
            .expectNext(Album("Soul Queen", "Aretha Franklin"))
            .expectNext(Album("Queen", "Nicki Minaj"))
            .expectNext(
                Album(
                    "Purcell: King Arthur, Music for Queen Mary, Hail! Bright Cecilia, Timon of Athens, Dioclesian, The Indian Queen & The Tempest",
                    "Carol Hall, David Thomas, Dinah Harris, Elisabeth Priday, English Baroque Soloists, Equale Brass Ensemble, Felicity Lott, Gill Ross, Gillian Fisher, Jennifer Smith, John Eliot Gardiner, John Elwes, Lynne Dawson, Martyn Hill, Michael George, Monteverdi Choir, Monteverdi Orchestra, Paul Elliott, Roderick Earle, Rogers Covey-Crump, Rosemary Hardy, Stephen Varcoe & Thomas Allen"
                )
            )
            .expectNext(Album("Queen", "Ten Walls"))
            .expectNext(Album("Queen (Deluxe)", "Nicki Minaj"))
            .verifyComplete()
    }


    @Test
    fun `test search endpoint with service call error`() {
        wireMockServer.stubFor(
            get(urlMatching("/search\\??(?:&?[^=&]*=[^=&]*)*"))
                .willReturn(
                    aResponse()
                        .withBodyFile("client-error.json")
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        )

        // Create test data
        val criteria = SearchCriteria(query = "invalid")

        // Perform the search
        StepVerifier.create(albumsServiceITunesImpl.search(criteria))
            .expectError(ServiceCallException::class.java)
            .verify()
    }


    @Test
    fun `test search endpoint with remote call  error`() {
        wireMockServer.stubFor(
            get(urlMatching("/search\\??(?:&?[^=&]*=[^=&]*)*"))
                .willReturn(
                    aResponse()
                        .withBodyFile("internal-error.json")
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        )

        // Create test data
        val criteria = SearchCriteria(query = "internal")

        // Perform the search
        StepVerifier.create(albumsServiceITunesImpl.search(criteria))
            .expectError(RemoteServiceException::class.java)
            .verify()
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