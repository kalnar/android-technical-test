package fr.leboncoin.androidrecruitmenttestapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.leboncoin.androidrecruitmenttestapp.utils.ImagePrefetchHandler
import fr.leboncoin.androidrecruitmenttestapp.utils.ImagePrefetcher
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageModule {

    @Binds
    @Singleton
    abstract fun bindImagePrefetcher(impl: ImagePrefetchHandler): ImagePrefetcher

}
