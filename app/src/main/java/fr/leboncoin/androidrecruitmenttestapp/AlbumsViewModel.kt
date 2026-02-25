package fr.leboncoin.androidrecruitmenttestapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Size
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.leboncoin.androidrecruitmenttestapp.ui.common.Ui
import fr.leboncoin.androidrecruitmenttestapp.ui.mapper.AlbumUiMapper
import fr.leboncoin.androidrecruitmenttestapp.ui.model.AlbumUi
import fr.leboncoin.androidrecruitmenttestapp.utils.ImagePrefetchHandler
import fr.leboncoin.core.coroutine.DispatcherProvider
import fr.leboncoin.domain.common.Resource
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumUiMapper: AlbumUiMapper,
    private val repository: AlbumRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val imagePrefetchHandler: ImagePrefetchHandler,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _ui: MutableStateFlow<Ui<List<AlbumUi>>> = MutableStateFlow(Ui.Loading)
    val ui: StateFlow<Ui<List<AlbumUi>>> = _ui

    fun toggleFavorite(albumId: Int) {
        viewModelScope.launch(dispatcherProvider.io) {
            val currentUi = _ui.value as? Ui.Success ?: return@launch
            val album = currentUi.data.find { it.id == albumId } ?: return@launch
            val newFavoriteState = !album.isFavorite
            repository.toggleFavorite(albumId, newFavoriteState)
            _ui.value = Ui.Success(
                currentUi.data.map { if (it.id == albumId) it.copy(isFavorite = newFavoriteState) else it }
            )
        }
    }

    fun loadAlbums() {
        _ui.value = Ui.Loading
        viewModelScope.launch(dispatcherProvider.io) {
            when (val resource = repository.getAllAlbums()) {
                is Resource.Error -> {
                    _ui.value = Ui.Error(resource.message.orEmpty())
                }

                is Resource.Success<List<Album>> -> {
                    val albumList = resource.data
                    val albumUiList = albumList.map { album ->
                        albumUiMapper.toUi(album)
                    }

                    val urlList = albumUiList
                        .map { it.thumbnailUrl }
                        .plus(albumUiList.map { it.url })

                    imagePrefetchHandler.prefetchImages(
                        viewModelScope,
                        urlList,
                    )

                    _ui.emit(
                        Ui.Success(albumUiList)
                    )
                }
            }
        }
    }
}
