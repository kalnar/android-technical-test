package fr.leboncoin.androidrecruitmenttestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.adevinta.spark.SparkTheme
import dagger.hilt.android.AndroidEntryPoint
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumDetailsScreen
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumsScreen
import fr.leboncoin.androidrecruitmenttestapp.ui.model.AlbumUi
import fr.leboncoin.androidrecruitmenttestapp.utils.AnalyticsHelper
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
object AlbumsRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AlbumsViewModel by viewModels()

    @Inject lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        analyticsHelper.initialize(this)

        setContent {
            SparkTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = AlbumsRoute) {
                    composable<AlbumsRoute> {
                        AlbumsScreen(
                            viewModel = viewModel,
                            onItemSelected = {
                                analyticsHelper.trackSelection(it.id.toString())
                                navController.navigate(it)
                            }
                        )
                    }
                    composable<AlbumUi> { backStackEntry ->
                        val album: AlbumUi = backStackEntry.toRoute()
                        analyticsHelper.trackScreenView("Details")
                        AlbumDetailsScreen(
                            album = album,
                            onToggleFavorite = { viewModel.toggleFavorite(album.id) },
                        )
                    }
                }
            }
        }
    }
}
