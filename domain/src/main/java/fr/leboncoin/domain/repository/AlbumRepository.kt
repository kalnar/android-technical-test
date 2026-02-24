package fr.leboncoin.domain.repository

import fr.leboncoin.domain.common.Resource
import fr.leboncoin.domain.model.Album

interface AlbumRepository {
    suspend fun getAllAlbums(): Resource<List<Album>>
}
