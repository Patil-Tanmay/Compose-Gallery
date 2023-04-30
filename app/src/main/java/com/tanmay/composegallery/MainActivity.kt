package com.tanmay.composegallery

import android.content.*
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tanmay.composegallery.utils.PermissionCheck
import com.tanmay.composegallery.data.model.PhotoItem
import com.tanmay.composegallery.ui.theme.ComposeGalleryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
                val backPressDispatcher =
                    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val showPhotoState by viewModel.showPhotos.collectAsState()
                    val expandDetailsCard = remember { mutableStateOf(false) }

                    // detect back press
                    LaunchedEffect(key1 = backPressDispatcher) {
                        val callback = object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                if (expandDetailsCard.value) {
                                    expandDetailsCard.value = false
                                } else {
                                    // exit the app
                                    finish()
                                }
                            }
                        }
                        backPressDispatcher?.addCallback(callback)
                    }



//                    if (permissionCheck.checkStoragePermission()) {
//                        LaunchedEffect(Unit) {
//                            viewModel.getPhotosFromSystem()
//                        }
//                        viewModel.updatePhotoState(ShowPhotoStates.Gallery)
//                    }

                    when (showPhotoState) {
                        ShowPhotoStates.Loading -> {
                            LoadingIndicator()
                            if (permissionCheck.checkStoragePermission()) {
                                LaunchedEffect(Unit) {
                                    viewModel.getPhotosFromSystem()
                                }
//                                viewModel.updatePhotoState(ShowPhotoStates.Gallery)
                            }
                        }

                        ShowPhotoStates.Gallery -> {
                            PagingListScreen(viewModel) {
                                expandDetailsCard.value = true
                            }
                            PhotoDetailsScreen(
                                viewModel, onBackPressed = {
                                    expandDetailsCard.value = false
                                }, expandableState = expandDetailsCard
                            )
                        }

                        ShowPhotoStates.PermissionDenied -> {
                            permissionCheck.showPermissionDialog()
                            ErrorScreen()
                        }

                        ShowPhotoStates.SplashScreen -> {
                            SplashScreen(){
                                viewModel.updatePhotoState(ShowPhotoStates.Loading)
                            }
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { check ->
            when (true) {
                check.all { it.value } -> {
                    lifecycleScope.launch {
                        viewModel.getPhotosFromSystem()
                    }
                    viewModel.updatePhotoState(ShowPhotoStates.Gallery)
                }

                else -> {
                    permissionCheck.showPermissionDialog()
                    viewModel.updatePhotoState(ShowPhotoStates.PermissionDenied)
                }
            }
        }
}

@Composable
fun SplashScreen(onFinished : () -> Unit) = Box(
    Modifier
        .fillMaxWidth()
        .fillMaxHeight()
) {

    val scale = remember {
        androidx.compose.animation.core.Animatable(0.0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(800, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
        delay(1000)
        onFinished()
    }

    Image(
        painter = painterResource(id = R.drawable.ic_splash),
        contentDescription = "",
        alignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .scale(scale.value)
    )

    Text(
        text = "Gallery App",
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
    )
}

@Composable
fun ErrorScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gallery") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = it.calculateBottomPadding() + 15.dp),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Please Give Media Permission through settings and Restart the App !",
                    style = MaterialTheme.typography.h6
                )
            }
        })
}

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
                            .transformable(state = state)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                                // if we need rotation we can enable this from below commented code
//                                rotationZ = rotation
                            ),
                        contentDescription = null
                    )
                }

            }
        )
    }
}


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


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeGalleryTheme {

    }
}