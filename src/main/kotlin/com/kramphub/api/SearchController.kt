package com.kramphub.api

import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.service.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService
) {

    @GetMapping
    fun search(@RequestParam("query") query: String): Mono<ResponseEntity<SearchResponse>> =
        searchService.search(SearchCriteria(query))
            .map { searchResult -> ResponseEntity.ok(SearchResponse.from(searchResult)) }

}