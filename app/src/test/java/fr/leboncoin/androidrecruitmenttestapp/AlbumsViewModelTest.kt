package fr.leboncoin.androidrecruitmenttestapp

import fr.leboncoin.androidrecruitmenttestapp.ui.common.Ui
import fr.leboncoin.androidrecruitmenttestapp.ui.mapper.AlbumUiMapper
import fr.leboncoin.androidrecruitmenttestapp.utils.ImagePrefetcher
import fr.leboncoin.core.coroutine.DispatcherProvider
import fr.leboncoin.core.coroutine.TestDispatcherProvider
import fr.leboncoin.domain.common.Resource
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumsViewModelTest {

    private val repository: AlbumRepository = mock()
    private val imagePrefetcher: ImagePrefetcher = mock()

    private lateinit var viewModel: AlbumsViewModel

    @Before
    fun setUp() {
        viewModel = AlbumsViewModel(
            AlbumUiMapper(),
            repository,
            TestDispatcherProvider,
            imagePrefetcher,
        )
    }

    private fun makeAlbum(id: Int, isFavorite: Boolean = false) = Album(
        id = id,
        albumId = 1,
        title = "Title $id",
        url = "https://example.com/$id",
        thumbnailUrl = "https://example.com/thumb/$id",
        isFavorite = isFavorite,
    )

    // --- loadAlbums ---

    @Test
    fun `loadAlbums - initial state is Loading`() {
        assertTrue(viewModel.ui.value is Ui.Loading)
    }

    @Test
    fun `loadAlbums - emits Success with mapped albums`() = runTest {
        val albums = listOf(makeAlbum(1), makeAlbum(2))
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(albums))

        viewModel.loadAlbums()

        val state = viewModel.ui.value as Ui.Success
        assertEquals(2, state.data.size)
        assertEquals(1, state.data[0].id)
        assertEquals(2, state.data[1].id)
        verify(imagePrefetcher).prefetchImages(any(), any())
    }

    @Test
    fun `loadAlbums - maps album fields correctly`() = runTest {
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(listOf(makeAlbum(42))))

        viewModel.loadAlbums()

        val albumUi = (viewModel.ui.value as Ui.Success).data[0]
        assertEquals(42, albumUi.id)
        assertEquals("Title 42", albumUi.title)
        assertEquals("Album #1", albumUi.chip1Description)
        assertEquals("Track #42", albumUi.chip2Description)
        verify(imagePrefetcher).prefetchImages(any(), any())
    }

    @Test
    fun `loadAlbums - passes thumbnail and full urls to image prefetcher`() = runTest {
        val album = makeAlbum(1)
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(listOf(album)))

        viewModel.loadAlbums()

        val expectedUrls = listOf(album.thumbnailUrl, album.url)
        verify(imagePrefetcher).prefetchImages(any(), org.mockito.kotlin.eq(expectedUrls))
    }

    @Test
    fun `loadAlbums - emits Error with message on failure`() = runTest {
        whenever(repository.getAllAlbums()).thenReturn(Resource.Error(Exception(), "Network error"))

        viewModel.loadAlbums()

        val state = viewModel.ui.value as Ui.Error
        assertEquals("Network error", state.message)
        verify(imagePrefetcher, never()).prefetchImages(any(), any())
    }

    @Test
    fun `loadAlbums - emits Error with empty message when message is null`() = runTest {
        whenever(repository.getAllAlbums()).thenReturn(Resource.Error(Exception(), null))

        viewModel.loadAlbums()

        assertEquals("", (viewModel.ui.value as Ui.Error).message)
        verify(imagePrefetcher, never()).prefetchImages(any(), any())
    }

    // --- toggleFavorite ---

    @Test
    fun `toggleFavorite - marks non-favorite album as favorite`() = runTest {
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(listOf(makeAlbum(1, isFavorite = false))))
        viewModel.loadAlbums()

        viewModel.toggleFavorite(1)

        assertTrue((viewModel.ui.value as Ui.Success).data[0].isFavorite)
        verify(repository).toggleFavorite(1, true)
    }

    @Test
    fun `toggleFavorite - unmarks favorite album`() = runTest {
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(listOf(makeAlbum(1, isFavorite = true))))
        viewModel.loadAlbums()

        viewModel.toggleFavorite(1)

        assertFalse((viewModel.ui.value as Ui.Success).data[0].isFavorite)
        verify(repository).toggleFavorite(1, false)
    }

    @Test
    fun `toggleFavorite - only toggles the targeted album`() = runTest {
        val albums = listOf(makeAlbum(1, isFavorite = false), makeAlbum(2, isFavorite = false))
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(albums))
        viewModel.loadAlbums()

        viewModel.toggleFavorite(1)

        val data = (viewModel.ui.value as Ui.Success).data
        assertTrue(data[0].isFavorite)
        assertFalse(data[1].isFavorite)
        verify(repository).toggleFavorite(1, true)
    }

    @Test
    fun `toggleFavorite - does nothing when state is not Success`() = runTest {
        // loadAlbums never called — state is Loading

        viewModel.toggleFavorite(1)

        assertTrue(viewModel.ui.value is Ui.Loading)
        verify(repository, never()).toggleFavorite(any(), any())
    }

    @Test
    fun `toggleFavorite - does nothing when album id is not found`() = runTest {
        whenever(repository.getAllAlbums()).thenReturn(Resource.Success(listOf(makeAlbum(1))))
        viewModel.loadAlbums()
        val stateBefore = viewModel.ui.value

        viewModel.toggleFavorite(999)

        assertEquals(stateBefore, viewModel.ui.value)
        verify(repository, never()).toggleFavorite(any(), any())
    }
}
