package com.kramphub.infra.service.itunes.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@Component
@ConfigurationProperties(prefix = "itunes")
class ITunesApiProperties {
    var searchLimit: Int = 5
}