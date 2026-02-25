package fr.leboncoin.data.local.mapper

import fr.leboncoin.data.local.entity.AlbumEntity
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.domain.model.Album
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

class AlbumEntityMapper @Inject constructor() {

    @OptIn(InternalSerializationApi::class)
    fun toEntity(dto: AlbumDto, fetchedAt: Long, isFavorite: Boolean = false): AlbumEntity = AlbumEntity(
        id = dto.id,
        albumId = dto.albumId,
        title = dto.title,
        url = dto.url,
        thumbnailUrl = dto.thumbnailUrl,
        fetchedAt = fetchedAt,
        isFavorite = isFavorite,
    )

    fun toDomain(entity: AlbumEntity): Album = Album(
        id = entity.id,
        albumId = entity.albumId,
        title = entity.title,
        url = entity.url,
        thumbnailUrl = entity.thumbnailUrl,
        isFavorite = entity.isFavorite,
    )

    @OptIn(InternalSerializationApi::class)
    fun toDto(entity: AlbumEntity): AlbumDto = AlbumDto(
        id = entity.id,
        albumId = entity.albumId,
        title = entity.title,
        url = entity.url,
        thumbnailUrl = entity.thumbnailUrl,
    )
}
