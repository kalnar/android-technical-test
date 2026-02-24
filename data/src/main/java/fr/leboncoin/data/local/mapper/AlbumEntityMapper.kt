package fr.leboncoin.data.local.mapper

import fr.leboncoin.data.local.entity.AlbumEntity
import fr.leboncoin.data.network.model.AlbumDto
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

class AlbumEntityMapper @Inject constructor() {

    @OptIn(InternalSerializationApi::class)
    fun toEntity(dto: AlbumDto, fetchedAt: Long): AlbumEntity = AlbumEntity(
        id = dto.id,
        albumId = dto.albumId,
        title = dto.title,
        url = dto.url,
        thumbnailUrl = dto.thumbnailUrl,
        fetchedAt = fetchedAt,
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
