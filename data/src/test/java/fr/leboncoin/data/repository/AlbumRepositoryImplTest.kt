package fr.leboncoin.data.repository

import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.entity.AlbumEntity
import fr.leboncoin.data.local.mapper.AlbumEntityMapper
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.domain.common.Resource
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.InternalSerializationApi
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.lang.AutoCloseable


@OptIn(InternalSerializationApi::class)
class AlbumRepositoryImplTest {

    @Mock
    lateinit var albumApiService: AlbumApiService

    @Mock
    lateinit var albumDao: AlbumDao

    private val albumEntityMapper = AlbumEntityMapper()

    private lateinit var repository: AlbumRepositoryIml

    private var mocks: AutoCloseable? = null

    @Before
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        repository = AlbumRepositoryIml(albumApiService, albumDao, albumEntityMapper)
    }

    @After
    @Throws(Exception::class)
    fun teardown() {
        mocks!!.close() // cleans up inline mock maker resources
    }

    private fun makeDto(id: Int) = AlbumDto(
        id = id,
        albumId = 1,
        title = "Title $id",
        url = "https://example.com/$id",
        thumbnailUrl = "https://example.com/thumb/$id",
    )

    private fun makeEntity(id: Int, fetchedAt: Long = System.currentTimeMillis(), isFavorite: Boolean = false) = AlbumEntity(
        id = id,
        albumId = 1,
        title = "Title $id",
        url = "https://example.com/$id",
        thumbnailUrl = "https://example.com/thumb/$id",
        fetchedAt = fetchedAt,
        isFavorite = isFavorite,
    )

    // --- getAllAlbums ---

    @Test
    fun `getAllAlbums - returns cached data when cache is valid and not empty`() = runTest {
        val cachedEntities = listOf(makeEntity(1), makeEntity(2))
        whenever(albumDao.getLastFetchedAt()).thenReturn(System.currentTimeMillis())
        whenever(albumDao.getAll()).thenReturn(cachedEntities)

        val result = repository.getAllAlbums()

        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data.size)
        assertEquals("Title 1", result.data[0].title)
        assertEquals("Title 2", result.data[1].title)
        verify(albumApiService, never()).getAlbums()
    }

    @Test
    fun `getAllAlbums - fetches from API when cache is expired`() = runTest {
        val dtos = listOf(makeDto(1))
        val entities = listOf(makeEntity(1), makeEntity(2))
        whenever(albumDao.getLastFetchedAt()).thenReturn(0L)
        whenever(albumDao.getAll()).thenReturn(entities)
        whenever(albumApiService.getAlbums()).thenReturn(dtos)

        val result = repository.getAllAlbums()

        assertTrue(result is Resource.Success)
        verify(albumApiService).getAlbums()
        verify(albumDao).deleteAll()
        verify(albumDao).insertAll(argThat { albumEntityList ->
            albumEntityList[0].title == "Title 1"
        })
    }

    @Test
    fun `getAllAlbums - preserves favorite state when refreshing from API`() = runTest {
        val favoriteEntity = makeEntity(1, isFavorite = true)
        val dtos = listOf(makeDto(1), makeDto(2))
        val entitiesAfterInsert = listOf(makeEntity(1, isFavorite = true), makeEntity(2, isFavorite = false))
        whenever(albumDao.getLastFetchedAt()).thenReturn(0L)
        whenever(albumDao.getAll()).thenReturn(listOf(favoriteEntity), entitiesAfterInsert)
        whenever(albumApiService.getAlbums()).thenReturn(dtos)

        val result = repository.getAllAlbums() as Resource.Success

        assertTrue(result.data[0].isFavorite)
        assertTrue(!result.data[1].isFavorite)
    }

    @Test
    fun `getAllAlbums - returns error on HttpException`() = runTest {
        whenever(albumDao.getLastFetchedAt()).thenReturn(0L)
        whenever(albumDao.getAll()).thenReturn(emptyList())
        whenever(albumApiService.getAlbums()).thenThrow(HttpException(Response.error<Any>(500, "".toResponseBody(null))))

        val result = repository.getAllAlbums()

        assertTrue(result is Resource.Error)
        assertEquals((result as Resource.Error).message,"Server error: 500")
    }

    @Test
    fun `getAllAlbums - returns error on IOException`() = runTest {
        whenever(albumDao.getLastFetchedAt()).thenReturn(0L)
        whenever(albumDao.getAll()).thenReturn(emptyList())
        whenever(albumApiService.getAlbums()).thenThrow(IOException("timeout"))

        val result = repository.getAllAlbums()

        assertTrue(result is Resource.Error)
        assertEquals("Network error, check your connection", (result as Resource.Error).message)
    }

    @Test
    fun `getAllAlbums - returns error on unexpected exception`() = runTest {
        whenever(albumDao.getLastFetchedAt()).thenReturn(0L)
        whenever(albumDao.getAll()).thenReturn(emptyList())
        whenever(albumApiService.getAlbums()).thenThrow(RuntimeException("Error"))

        val result = repository.getAllAlbums()

        assertTrue(result is Resource.Error)
        assertEquals("An unexpected error occurred", (result as Resource.Error).message)
    }

    // --- toggleFavorite ---

    @Test
    fun `toggleFavorite - calls dao updateFavorite with correct id and state`() = runTest {
        repository.toggleFavorite(42, true)

        verify(albumDao).updateFavorite(42, true)
    }

    @Test
    fun `toggleFavorite - can unmark a favorite`() = runTest {
        repository.toggleFavorite(7, false)

        verify(albumDao).updateFavorite(7, false)
    }
}
