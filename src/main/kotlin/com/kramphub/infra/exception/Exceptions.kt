package com.kramphub.infra.exception

import org.springframework.http.HttpStatusCode

class RemoteServiceException(message: String?, val statusCode: HttpStatusCode) : RuntimeException(message)

class ServiceCallException(message: String?, val statusCode: HttpStatusCode) : RuntimeException(message)
