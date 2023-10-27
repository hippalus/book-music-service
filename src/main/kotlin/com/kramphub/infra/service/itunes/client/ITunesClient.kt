package com.kramphub.infra.service.itunes.client

import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono


@HttpExchange
interface ITunesClient {
    
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
    
    @Retry(name = "itunes")
    @Bulkhead(name = "itunes")
    @CircuitBreaker(name = "itunes", fallbackMethod = "searchFallBack")
    @GetExchange("/search")
    fun search(
        @RequestParam("term") term: String,
        @RequestParam("country") country: String,
        @RequestParam("media") media: String,
        @RequestParam("entity") entity: String,
        @RequestParam("attribute") attribute: String,
        @RequestParam("limit") limit: Int,
        @RequestParam("lang") lang: String,
        @RequestParam("version") version: Int? = 2,
        @RequestParam("explicit") explicit: YesNoType? = YesNoType.Y
    ): Mono<ResponseEntity<ITunesSearchResponse>>
    
    fun searchFallBack(
        term: String,
        country: String,
        media: String,
        entity: String,
        attribute: String,
        limit: Int,
        lang: String,
        version: Int? = 2,
        explicit: YesNoType? = YesNoType.Y,
        ex: Exception
    ): Mono<ResponseEntity<ITunesSearchResponse>> {
        log.warn("#searchFallBack invoking for ITunesClient", ex)
        return Mono.just(ResponseEntity.ok(ITunesSearchResponse()))
    }
    
}