package com.kramphub.infra.service

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.service.AlbumsService
import com.kramphub.infra.config.toObject
import com.kramphub.infra.service.client.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class AlbumsServiceITunesImpl(
    private val iTunesClient: ITunesClient
) : AlbumsService {

    override fun search(criteria: SearchCriteria): Flux<Album> = iTunesClient.search(
        term = criteria.query,
        country = Country.NETHERLANDS.iso,
        media = Media.ALL.type,
        entity = Entity.ALBUM.value,
        attribute = Attribute.ALBUM_TERM.value,
        lang = Lang.ENGLISH.code,
        limit = 5 //TODO get from env
    )
        .mapNotNull { it.body?.toObject<ITunesSearchResponse>() }
        .flatMapIterable { checkNotNull(it).results }
        .map { toModel(it) }


    private fun toModel(result: Result) = Album(result.collectionName, result.artistName)

}
