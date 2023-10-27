package com.kramphub.infra.service.client

import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono


@HttpExchange
interface ITunesClient {

    @Retry(name = "itunes")
    @Bulkhead(name = "itunes")
    @CircuitBreaker(name = "itunes")
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
    ): Mono<ResponseEntity<String>>


}