package fr.leboncoin.data.repository

import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.repository.mapper.AlbumDtoMapper
import fr.leboncoin.domain.common.Resource
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepositoryContract
import okio.IOException
import retrofit2.HttpException

class AlbumRepository(
    private val albumApiService: AlbumApiService,
    private val albumDtoMapper: AlbumDtoMapper,
) : AlbumRepositoryContract {

    override suspend fun getAllAlbums(): Resource<List<Album>> {
        return try {
            val albumDtoList = albumApiService.getAlbums()
            val albumList = albumDtoList.map { albumDto ->
                albumDtoMapper.toDomain(albumDto)
            }
            Resource.Success(albumList)
        } catch (e: HttpException) {
            Resource.Error(e, "Server error: ${e.code()}")
        } catch (e: IOException) {
            Resource.Error(e, "Network error, check your connection")
        } catch (e: Exception) {
            Resource.Error(e, "An unexpected error occurred")
        }
    }
}