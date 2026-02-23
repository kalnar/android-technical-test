package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.ui.common.Ui
import fr.leboncoin.androidrecruitmenttestapp.ui.mapper.AlbumUiMapper
import fr.leboncoin.androidrecruitmenttestapp.ui.model.AlbumUi
import fr.leboncoin.core.coroutine.DispatcherProvider
import fr.leboncoin.domain.common.Resource
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepositoryContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumUiMapper: AlbumUiMapper,
    private val repository: AlbumRepositoryContract,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _ui: MutableStateFlow<Ui<List<AlbumUi>>> = MutableStateFlow(Ui.Loading)
    val ui: StateFlow<Ui<List<AlbumUi>>> = _ui

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
                    _ui.emit(
                        Ui.Success(albumUiList)
                    )
                }
            }
        }
    }
}
