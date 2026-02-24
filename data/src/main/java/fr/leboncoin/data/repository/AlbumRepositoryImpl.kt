package fr.leboncoin.data.repository

import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.mapper.AlbumEntityMapper
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.repository.mapper.AlbumDtoMapper
import fr.leboncoin.domain.common.Resource
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepository
import kotlinx.serialization.InternalSerializationApi
import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumRepositoryIml @Inject constructor(
    private val albumApiService: AlbumApiService,
    private val albumDtoMapper: AlbumDtoMapper,
    private val albumDao: AlbumDao,
    private val albumEntityMapper: AlbumEntityMapper,
) : AlbumRepository {

    @OptIn(InternalSerializationApi::class)
    override suspend fun getAllAlbums(): Resource<List<Album>> {
        val lastFetchedAt = albumDao.getLastFetchedAt() ?: 0L
        val isCacheValid = System.currentTimeMillis() - lastFetchedAt < CACHE_WINDOW_MS

        if (isCacheValid) {
            val cached = albumDao.getAll()
            if (cached.isNotEmpty()) {
                return Resource.Success(
                    cached.map { entity ->
                        albumDtoMapper.toDomain(albumEntityMapper.toDto(entity))
                    }
                )
            }
        }

        return try {
            val albumDtoList = albumApiService.getAlbums()
            val now = System.currentTimeMillis()
            albumDao.deleteAll()
            albumDao.insertAll(albumDtoList.map { dto -> albumEntityMapper.toEntity(dto, now) })
            Resource.Success(albumDtoList.map { dto -> albumDtoMapper.toDomain(dto) })
        } catch (e: HttpException) {
            Resource.Error(e, "Server error: ${e.code()}")
        } catch (e: IOException) {
            Resource.Error(e, "Network error, check your connection")
        } catch (e: Exception) {
            Resource.Error(e, "An unexpected error occurred")
        }
    }

    companion object {
        private val CACHE_WINDOW_MS = TimeUnit.DAYS.toMillis(1)
    }
}
