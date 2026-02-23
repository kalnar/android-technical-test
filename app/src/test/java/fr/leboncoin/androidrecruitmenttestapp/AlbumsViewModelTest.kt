package fr.leboncoin.androidrecruitmenttestapp

import fr.leboncoin.androidrecruitmenttestapp.ui.common.Ui
import fr.leboncoin.androidrecruitmenttestapp.ui.mapper.AlbumUiMapper
import fr.leboncoin.core.coroutine.DispatcherProvider
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.data.repository.AlbumRepository
import fr.leboncoin.data.repository.mapper.AlbumDtoMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertTrue
import org.junit.Test

class AlbumsViewModelTest {

    private val testDispatcherProvider = object : DispatcherProvider {
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun loadsAlbums_initialStateIsLoading() {
        val fakeService = object : AlbumApiService {
            override suspend fun getAlbums(): List<AlbumDto> = listOf(
                AlbumDto(id = 1, albumId = 1, title = "t", url = "u", thumbnailUrl = "tu")
            )
        }
        val repository = AlbumRepository(fakeService, AlbumDtoMapper())
        val vm = AlbumsViewModel(AlbumUiMapper(), repository, testDispatcherProvider)

        assertTrue("Expected initial state to be Loading", vm.ui.value is Ui.Loading)
    }
}
