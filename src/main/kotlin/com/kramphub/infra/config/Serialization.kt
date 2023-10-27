package com.kramphub.infra.config

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.kramphub.infra.config.JacksonUtils.enhancedObjectMapper


object Serialization {

    val objectMapper: ObjectMapper = enhancedObjectMapper()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

inline fun <reified T : Any> T.toJson(): String = Serialization.objectMapper.writeValueAsString(this)

inline fun <reified T : Any> String.toObject(): T = Serialization.objectMapper.readValue(this)


object JacksonUtils {

    fun enhancedObjectMapper(): ObjectMapper {
        val objectMapper: ObjectMapper = JsonMapper.builder()
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build()
        registerWellKnownModulesIfAvailable(objectMapper)
        return objectMapper
    }

    private fun registerWellKnownModulesIfAvailable(objectMapper: ObjectMapper) {
        objectMapper.registerModule(Jdk8ModuleProvider.MODULE)
        objectMapper.registerModule(JavaTimeModuleProvider.MODULE)
        objectMapper.registerModule(KotlinModuleProvider.MODULE)
    }

    private object Jdk8ModuleProvider {
        val MODULE: Module = Jdk8Module()
    }

    private object JavaTimeModuleProvider {
        val MODULE: Module = JavaTimeModule()
    }

    private object KotlinModuleProvider {
        @Suppress("deprecation")
        val MODULE: Module = KotlinModule()
    }
}