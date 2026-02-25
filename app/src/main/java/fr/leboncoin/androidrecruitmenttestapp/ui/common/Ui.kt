package fr.leboncoin.androidrecruitmenttestapp.ui.common

sealed class Ui<out T> {
    data object Loading : Ui<Nothing>()

    data class Success<T>(val data: T) : Ui<T>()

    data class Error(val message: String) : Ui<Nothing>()
}
