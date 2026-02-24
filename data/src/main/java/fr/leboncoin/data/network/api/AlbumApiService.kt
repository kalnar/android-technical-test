package fr.leboncoin.data.network.api

import fr.leboncoin.data.network.model.AlbumDto
import kotlinx.serialization.InternalSerializationApi
import retrofit2.http.GET

interface AlbumApiService {
    
    @OptIn(InternalSerializationApi::class)
    @GET("img/shared/technical-test.json")
    suspend fun getAlbums(): List<AlbumDto>
    
    companion object {
        const val BASE_URL = "https://static.leboncoin.fr/"
    }
}