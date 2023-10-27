package com.kramphub.infra.exception

import com.google.api.client.http.HttpResponseException
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        log.warn("WebExchangeBindException occurred [${ex.message}]", ex)

        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString("&&") { "${it.field}: ${it.defaultMessage}" }

        return Mono.just(ResponseEntity(ProblemDetail.forStatusAndDetail(ex.statusCode, errorMessage), ex.statusCode))
    }

    @ExceptionHandler(WebClientResponseException::class)
    fun handle(ex: WebClientResponseException): Mono<ProblemDetail> {
        log.warn("WebClientResponseException occurred [${ex.message}]", ex)
        return Mono.just(ProblemDetail.forStatusAndDetail(ex.statusCode, ex.message ?: ""))
    }

    @ExceptionHandler(HttpResponseException::class)
    fun handle(ex: HttpResponseException): Mono<ProblemDetail> {
        log.warn("HttpResponseException occurred [${ex.message}]", ex)
        return Mono.just(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(ex.statusCode), ex.statusMessage ?: ""))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): Mono<ProblemDetail> {
        log.warn("IllegalArgumentException occurred [${ex.message}]", ex)
        return Mono.just(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: ""))
    }

    @ExceptionHandler(RemoteServiceException::class)
    fun handle(ex: RemoteServiceException): Mono<ProblemDetail> {
        log.warn("RemoteServiceException occurred [${ex.message}]", ex)
        return Mono.just(ProblemDetail.forStatusAndDetail(ex.statusCode, ex.message ?: "Remote call error"))
    }

    @ExceptionHandler(ServiceCallException::class)
    fun handle(ex: ServiceCallException): Mono<ProblemDetail> {
        log.warn("ServiceCallException occurred [${ex.message}]", ex)
        return Mono.just(ProblemDetail.forStatusAndDetail(ex.statusCode, ex.message ?: "Bad request"))
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): Mono<ProblemDetail> {
        log.warn("Exception occurred [${ex.message}]", ex)
        return Mono.just(
            ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "Internal server error"
            )
        )
    }
}