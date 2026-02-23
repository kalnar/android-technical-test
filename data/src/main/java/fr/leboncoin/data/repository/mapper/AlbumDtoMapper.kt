package fr.leboncoin.data.repository.mapper

import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.domain.model.Album

class AlbumDtoMapper {

    fun toDomain(dto: AlbumDto): Album = Album(
        id = dto.id,
        albumId = dto.albumId,
        title = dto.title,
        url = dto.url,
        thumbnailUrl = dto.thumbnailUrl
    )
}
