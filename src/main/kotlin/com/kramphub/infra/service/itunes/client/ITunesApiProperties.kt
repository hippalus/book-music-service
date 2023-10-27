package com.kramphub.infra.service.itunes.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "itunes")
class ITunesApiProperties {
    var searchLimit: Int = 5
}