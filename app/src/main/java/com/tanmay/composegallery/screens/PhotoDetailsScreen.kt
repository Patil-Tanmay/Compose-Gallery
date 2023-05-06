package com.tanmay.composegallery.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tanmay.composegallery.GalleryViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PhotoDetailsScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    expandableState: MutableState<Boolean>
) {

    AnimatedVisibility(visibleState = MutableTransitionState(expandableState.value)) {

        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            offset += offsetChange
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(viewModel.photoItem?.displayName ?: "Photo") },
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                )
            },
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(Color.Black)
                        .padding(it)
                ) {
                    GlideImage(
                        model = Uri.parse(viewModel.photoItem?.uri),
                        modifier = Modifier
                            .fillMaxSize()
                            .transformable(state = state),
//                            .graphicsLayer(
//                                scaleX = scale,
//                                scaleY = scale,
//                                translationX = offset.x,
//                                translationY = offset.y
                        // if we need rotation we can enable this from below commented code
//                                rotationZ = rotation
//                            ),
                        contentDescription = null
                    )
                }

            }
        )
    }
}