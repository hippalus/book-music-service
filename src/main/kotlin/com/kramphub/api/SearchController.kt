package com.kramphub.api

import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.service.SearchService
import io.micrometer.observation.annotation.Observed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/search")
@Tag(name = "Search Music and Book Controller", description = "Search operations for books and musics")
class SearchController(
    private val searchService: SearchService
) {

    @GetMapping
    @Observed(name = "search", contextualName = "searching-music-book")
    @Operation(
        summary = "Search Music and Book",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Search Response",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SearchResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
        ]
    )
    fun search(@RequestParam("query") query: String): Mono<ResponseEntity<SearchResponse>> =
        searchService.search(SearchCriteria(query))
            .map { searchResult -> ResponseEntity.ok(SearchResponse.from(searchResult)) }

}