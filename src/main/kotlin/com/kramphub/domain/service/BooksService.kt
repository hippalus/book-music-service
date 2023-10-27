package com.kramphub.domain.service

import com.kramphub.domain.model.Book
import com.kramphub.domain.model.SearchCriteria
import reactor.core.publisher.Flux

interface BooksService {

    fun search(criteria: SearchCriteria): Flux<Book>
}