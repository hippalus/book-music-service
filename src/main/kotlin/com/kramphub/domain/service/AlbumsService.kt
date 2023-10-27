package com.kramphub.domain.service

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.SearchCriteria
import reactor.core.publisher.Flux

interface AlbumsService {

    fun search(criteria: SearchCriteria): Flux<Album>
}