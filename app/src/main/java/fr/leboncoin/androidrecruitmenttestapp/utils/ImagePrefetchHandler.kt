package fr.leboncoin.androidrecruitmenttestapp.utils

import android.content.Context
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.leboncoin.core.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImagePrefetchHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : ImagePrefetcher {

    override fun prefetchImages(
        scope: CoroutineScope,
        urlList: List<String>,
    ) {
        scope.launch(dispatcherProvider.io) {
            urlList
                .forEach { url ->
                    if (!isImageCached(url, context)) {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .size(Size.ORIGINAL)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build()

                        context.imageLoader.execute(request)
                    }
                }
        }
    }

    private fun isImageCached(url: String, context: Context): Boolean {
        val memoryCache = context.imageLoader.memoryCache
        val diskCache = context.imageLoader.diskCache

        val memoryCacheKey = MemoryCache.Key(url)
        if (memoryCache?.get(memoryCacheKey) != null) return true

        diskCache?.openSnapshot(url)?.use { return true }

        return false
    }
}
