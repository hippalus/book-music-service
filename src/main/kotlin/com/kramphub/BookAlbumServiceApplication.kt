package com.kramphub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookAlbumServiceApplication

fun main(args: Array<String>) {
    runApplication<BookAlbumServiceApplication>(*args)
}
