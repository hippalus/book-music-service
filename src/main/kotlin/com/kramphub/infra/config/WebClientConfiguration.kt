package com.kramphub.infra.config

import com.kramphub.infra.service.client.ITunesClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class WebClientConfiguration(
    val serviceConfigurations: ServiceConfigurations
) {

    @Bean
    fun iTunesClient(): ITunesClient =
        serviceConfigurations.createWebClient(ServiceConfigurations.ServiceId.ITUNES).let { webClient ->
            val httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build()

            httpServiceProxyFactory.createClient(ITunesClient::class.java)
        }
}