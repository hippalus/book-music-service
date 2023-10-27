package com.kramphub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BookAlbumServiceApplication

fun main(args: Array<String>) {
    runApplication<BookAlbumServiceApplication>(*args)
}
