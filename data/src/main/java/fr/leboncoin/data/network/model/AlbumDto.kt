package fr.leboncoin.data.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
@InternalSerializationApi
@Serializable
data class AlbumDto(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)