package com.kramphub.domain.model

data class SearchCriteria(val query: String) {

    init {
        require(!(query.isBlank() || query.isEmpty())) { "query can not be an empty!" }
    }
}