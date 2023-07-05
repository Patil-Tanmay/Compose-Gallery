package com.tanmay.composegallery.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.compose.LazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tanmay.composegallery.ExpandableState
import com.tanmay.composegallery.data.model.PhotoItem
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun PhotoDetailsScreen(
    photos: LazyPagingItems<PhotoItem>,
    onBackPressed: () -> Unit,
    expandableState: MutableState<ExpandableState>
) {

    AnimatedVisibility(visibleState = MutableTransitionState(expandableState.value.isExpanded)) {

        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            offset += offsetChange
        }


        var offsetY by remember { mutableStateOf(0f) }
        val pagerState = rememberPagerState(
            initialPage = expandableState.value.index
        )

        HorizontalPager(
            pageCount = photos.itemCount,
            modifier = Modifier
                .pointerInteropFilter {
                    offsetY = it.y
                    false
                }
//                .padding(horizontal = 32.dp, vertical = 64.dp)
//                .clip(
//                    RoundedCornerShape(25.dp)
//                )
                .background(Color.Black),
            state = pagerState
        ) { page ->
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = pagerState.offsetForPage(page)
                        translationX = size.width * pageOffset

                        val endOffset = pagerState.endOffsetForPage(page)

                        shadowElevation = 20f
                        shape = CirclePath(
                            progress = 1f - endOffset.absoluteValue,
                            origin = Offset(
                                size.width,
                                offsetY,
                            )
                        )
                        clip = true

                        val absoluteOffset = pagerState.offsetForPage(page).absoluteValue
                        val currentScale = 1f + (absoluteOffset.absoluteValue * .4f)

                        scaleX = currentScale
                        scaleY = currentScale

                        val startOffset = pagerState.startOffsetForPage(page)
                        alpha = (2f - startOffset) / 2f

                    },
                topBar = {
                    TopAppBar(
                        title = { Text(photos[page]?.displayName ?: "Photo") },
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
                            model = Uri.parse(photos[page]?.uri),
                            modifier = Modifier
                                .fillMaxSize(),
//                                .transformable(state = state),
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

}

class CirclePath(private val progress: Float, private val origin: Offset = Offset(0f, 0f)) : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {

        val center = Offset(
            x = size.center.x - ((size.center.x - origin.x) * (1f - progress)),
            y = size.center.y - ((size.center.y - origin.y) * (1f - progress)),
        )
        val radius = (sqrt(
            size.height * size.height + size.width * size.width
        ) * .5f) * progress

        return Outline.Generic(Path().apply {
            addOval(
                Rect(
                    center = center,
                    radius = radius,
                )
            )
        })
    }
}

// ACTUAL OFFSET
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

// OFFSET ONLY FROM THE LEFT
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

// OFFSET ONLY FROM THE RIGHT
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}