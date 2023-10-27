package com.kramphub.infra.config

import com.kramphub.infra.exception.RemoteServiceException
import com.kramphub.infra.exception.ServiceCallException
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
@ConfigurationProperties(prefix = "book-album-api")
data class ServiceConfigurations(
    var services: Map<ServiceId, ServiceConfiguration> = mapOf()
) {

    @Autowired
    private lateinit var builder: WebClient.Builder

    fun createWebClient(serviceId: ServiceId): WebClient {
        services.getValue(serviceId).let { serviceConfig ->

            return builder.baseUrl(serviceConfig.url)
                .filter(errorFilter())
                .clientConnector(reactorClientHttpConnector(serviceConfig))
                .build()
        }
    }

    private fun reactorClientHttpConnector(serviceConfiguration: ServiceConfiguration): ReactorClientHttpConnector {
        val httpClient: HttpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, serviceConfiguration.connectTimeout.toMillis().toInt())
            .responseTimeout(serviceConfiguration.responseTimeout)
            .doOnConnected { conn ->
                conn.addHandlerLast(
                    ReadTimeoutHandler(
                        serviceConfiguration.readTimeout.toMillis(),
                        TimeUnit.MILLISECONDS
                    )
                )
                    .addHandlerLast(
                        WriteTimeoutHandler(
                            serviceConfiguration.writeTimeout.toMillis(),
                            TimeUnit.MILLISECONDS
                        )
                    )
            }

        return ReactorClientHttpConnector(httpClient)
    }

    private fun errorFilter() = ExchangeFilterFunction.ofResponseProcessor(
        fun(clientResponse: ClientResponse): Mono<ClientResponse> {

            if (clientResponse.statusCode().is5xxServerError) {
                return clientResponse.bodyToMono(String::class.java)
                    .flatMap { Mono.error(RemoteServiceException(it, clientResponse.statusCode())) }
            }

            if (clientResponse.statusCode().is4xxClientError) {
                return clientResponse.bodyToMono(String::class.java)
                    .flatMap { Mono.error(ServiceCallException(it, clientResponse.statusCode())) }
            }

            return Mono.just(clientResponse)
        }
    )

    enum class ServiceId {
        ITUNES
    }

    data class ServiceConfiguration(
        val url: String,
        val readTimeout: Duration = Duration.ofSeconds(60),
        val writeTimeout: Duration = Duration.ofSeconds(60),
        val responseTimeout: Duration = Duration.ofSeconds(60),
        val connectTimeout: Duration = Duration.ofSeconds(60),
        val oauth2Enabled: Boolean = false
    )
}

