package com.kramphub.infra.service.googlebooks.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "google-books")
class GoogleApiProperties {
    var searchLimit: Int = 5
}