package fr.leboncoin.androidrecruitmenttestapp

import android.app.Application
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.hilt.android.HiltAndroidApp
import coil3.SingletonImageLoader

@HiltAndroidApp
class PhotoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        SingletonImageLoader.setSafe {
            ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(this, 1.0)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.resolve("image_cache"))
                        .maxSizeBytes(50L * 1024 * 1024) // 50 MB
                        .build()
                }
                .components {
                    add(OkHttpNetworkFetcherFactory())
                }
                .build()
        }
    }
}
