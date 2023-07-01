package com.tanmay.composegallery.screens

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tanmay.composegallery.GalleryViewModel
import com.tanmay.composegallery.data.model.Album

@Composable
fun AlbumScreen(
    albums: LazyPagingItems<Album>,
    viewModel: GalleryViewModel = hiltViewModel(),
    onAlbumClick: (index: Long) -> Unit
) {
//    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pagingScrollState = rememberLazyGridState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gallery") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        content = {
//                    val photos = viewModel.getPhotos().collectAsLazyPagingItems()
            LazyVerticalGrid(
                modifier = Modifier.padding(it),
                state = pagingScrollState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(albums.itemCount) { index ->
                    albums[index]?.let { album ->
                        AlbumCard(
                            album = album,
                            modifier = Modifier.padding(4.dp),
                            //                                    viewModel = viewModel,
                            onAlbumClick = onAlbumClick
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun AlbumCard(
    album: Album,
    modifier: Modifier = Modifier,
    onAlbumClick: (bucketId: Long) -> Unit
) {
    Card(
        modifier = modifier.height(30.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        onClick = {
            onAlbumClick(album.bucketId)
        }
    ) {
        Row {
            // Image
            GlideImage(
                model = Uri.parse(album.thumbnailPath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Texts
            Column {
                Text(
                    text = album.displayName,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = album.count.toString(),
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTheScreen() {
    AlbumCard(album = Album(15, "", 5, "")) {

    }
}
