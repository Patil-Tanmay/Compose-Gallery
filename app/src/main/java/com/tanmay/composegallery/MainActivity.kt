package com.tanmay.composegallery

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.tanmay.composegallery.screens.AlbumScreen
import com.tanmay.composegallery.screens.ErrorScreen
import com.tanmay.composegallery.utils.PermissionCheck
import com.tanmay.composegallery.screens.PagingListScreen
import com.tanmay.composegallery.screens.PhotoDetailsScreen
import com.tanmay.composegallery.screens.SplashScreen
import com.tanmay.composegallery.ui.theme.ComposeGalleryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionCheck: PermissionCheck by lazy {
        PermissionCheck(this, requestPermissionLauncher)
    }

    private val viewModel by viewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val showPhotoState by viewModel.showPhotos.collectAsState()
                    val expandDetailsCard = remember { mutableStateOf(ExpandableState(0, false)) }


                    val backPressDispatcher =
                        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                    // detect back press
                    LaunchedEffect(key1 = backPressDispatcher) {
                        val callback = object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                when (showPhotoState) {
                                    ShowPhotoStates.Loading -> {
                                        finish()
                                    }

                                    ShowPhotoStates.Albums -> finish()
                                    ShowPhotoStates.AlbumPhotos -> {
                                        if (expandDetailsCard.value.isExpanded) {
                                            expandDetailsCard.value =
                                                expandDetailsCard.value.copy(isExpanded = false)
                                        } else {
                                            viewModel.updatePhotoState(ShowPhotoStates.Albums)
                                        }
                                    }

                                    ShowPhotoStates.PermissionDenied -> {
                                        finish()
                                    }

                                    ShowPhotoStates.EmptyScreen -> {
                                        finish()
                                    }

                                    ShowPhotoStates.SplashScreen -> {
                                        finish()
                                    }
                                }
                            }
                        }
                        backPressDispatcher?.addCallback(callback)
                    }

                    when (showPhotoState) {
                        ShowPhotoStates.Loading -> {
                            LoadingIndicator()
                            if (permissionCheck.checkStoragePermission()) {
//                                LaunchedEffect(Unit) {
//                                    viewModel.getPhotosFromSystem()
                                viewModel.getAllAlbums()
//                                }
                            }
                        }

                        ShowPhotoStates.AlbumPhotos -> {
                            val photos = viewModel.getPhotos().collectAsLazyPagingItems()
                            PagingListScreen(photos, viewModel,
                                onPhotoClick = { index ->
                                    expandDetailsCard.value =
                                        expandDetailsCard.value.copy(
                                            index = index,
                                            isExpanded = true
                                        )
                                }
                            )
                            PhotoDetailsScreen(
                                photos = photos,
//                                viewModel = viewModel,
                                onBackPressed = {
                                    expandDetailsCard.value =
                                        expandDetailsCard.value.copy(isExpanded = false)
                                },
                                expandableState = expandDetailsCard
                            )
                        }

                        ShowPhotoStates.PermissionDenied -> {
                            permissionCheck.showPermissionDialog()
                            ErrorScreen("Please Give Media Permission through settings and Restart the App !")
                        }

                        ShowPhotoStates.SplashScreen -> {
                            SplashScreen {
                                viewModel.updatePhotoState(ShowPhotoStates.Loading)
                            }
                        }

                        ShowPhotoStates.EmptyScreen -> {
                            ErrorScreen("No Images Found!")
                        }

                        ShowPhotoStates.Albums -> {
                            val albums = viewModel.getPagedAlbums().collectAsLazyPagingItems()

                            AlbumScreen(albums = albums, onAlbumClick = {
                                // navigate to album Photos
                                viewModel.getPhotosFromAlbum(it)
                            })
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAGG", "onResume: called")
        if (viewModel.showPhotos.value == ShowPhotoStates.PermissionDenied) {
            if (permissionCheck.checkStoragePermission()) {
//            LaunchedEffect(Unit) {
//                viewModel.getPhotosFromSystem()
                viewModel.getAllAlbums()
//            }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { check ->
            when (true) {
                check.all { it.value } -> {
                    lifecycleScope.launch {
//                        viewModel.getPhotosFromSystem()
                        viewModel.getAllAlbums()
                    }
                    viewModel.updatePhotoState(ShowPhotoStates.AlbumPhotos)
                }

                else -> {
//                    permissionCheck.showPermissionDialog()
                    viewModel.updatePhotoState(ShowPhotoStates.PermissionDenied)
                }
            }
        }
}

data class ExpandableState(
    val index: Int = 0,
    val isExpanded: Boolean = false
)

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeGalleryTheme {

    }
}