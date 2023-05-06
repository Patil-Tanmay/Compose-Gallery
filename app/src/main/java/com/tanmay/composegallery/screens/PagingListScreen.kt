package com.tanmay.composegallery.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tanmay.composegallery.GalleryViewModel
import com.tanmay.composegallery.data.model.PhotoItem

@Composable
fun PagingListScreen(viewModel: GalleryViewModel = hiltViewModel(), onPhotoClick: () -> Unit) {

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pagingScrollState = rememberLazyGridState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gallery") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        content = {
            when (true) {
                isRefreshing -> {
                    LoadingIndicator()
                }

                else -> {
                    val photos = viewModel.getPhotos().collectAsLazyPagingItems()
                    LazyVerticalGrid(
                        modifier = Modifier.padding(it),
                        state = pagingScrollState,
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(photos.itemCount) { index ->
                            photos[index]?.let { photo ->
                                ImageCard(
                                    image = photo,
                                    modifier = Modifier.padding(4.dp),
                                    viewModel = viewModel,
                                    onPhotoClick
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun ImageCard(
    image: PhotoItem,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = hiltViewModel(),
    onPhotoClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        onClick = {
            viewModel.updatePhotoUri(image)
            onPhotoClick()
        }
    ) {
        GlideImage(
            model = Uri.parse(image.uri),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}