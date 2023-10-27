package com.kramphub.infra.service.googlebooks.client

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.books.v1.Books
import com.google.api.services.books.v1.BooksRequestInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val APPLICATION_NAME = "Google Books"
private const val API_KEY = ""

@Configuration
class SdkConfiguration {

    @Bean
    fun googleBooks(): Books {
        val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val httpRequestInitializer = HttpRequestInitializer { _: HttpRequest -> }

        return Books.Builder(httpTransport, GsonFactory.getDefaultInstance(), httpRequestInitializer)
            .setApplicationName(APPLICATION_NAME)
            .setGoogleClientRequestInitializer(BooksRequestInitializer(API_KEY))
            .build()
    }
}