package com.kramphub.infra.service.itunes

import com.kramphub.domain.model.Album
import com.kramphub.domain.model.SearchCriteria
import com.kramphub.domain.service.AlbumsService
import com.kramphub.infra.service.itunes.client.*
import io.micrometer.observation.annotation.Observed
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AlbumsServiceITunesImpl(
    private val iTunesClient: ITunesClient,
    private val iTunesApiProperties: ITunesApiProperties
) : AlbumsService {

    @Observed(name = "search-itunes-albums", contextualName = "searching-itunes-albums")
    override fun search(criteria: SearchCriteria): Flux<Album> {
        val albums = getAlbums(criteria)

        return albums.mapNotNull { it.body }
            .flatMapIterable { checkNotNull(it).results }
            .map { toModel(it) }
    }

    private fun getAlbums(criteria: SearchCriteria): Mono<ResponseEntity<ITunesSearchResponse>> {
        return iTunesClient.search(
            term = criteria.term(),
            country = Country.NETHERLANDS.iso,
            media = Media.ALL.type,
            entity = Entity.ALBUM.value,
            attribute = Attribute.ALBUM_TERM.value,
            lang = Lang.ENGLISH.code,
            limit = iTunesApiProperties.searchLimit
        )
    }

    private fun SearchCriteria.term() = this.query.trim().replace("\\W", "+")

    private fun toModel(result: ITunesResult) = Album(result.collectionName, result.artistName)

}
