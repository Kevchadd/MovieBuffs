package com.example.moviebuffs.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviebuffs.R
import com.example.moviebuffs.ui.network.MoviesPhoto
import com.example.moviebuffs.ui.theme.MovieBuffsTheme
import com.example.moviebuffs.ui.utils.MovieContentType


@Composable
fun HomeScreen(
    moviesUiState: MoviesUiState, retryAction: () -> Unit, onClick: (MoviesPhoto) -> Unit, modifier: Modifier = Modifier
) {
    when (moviesUiState) {
        is MoviesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is MoviesUiState.Success -> MoviesApp(moviesUiState, onClick, modifier)
        is MoviesUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MoviesApp(
    moviesUiState: MoviesUiState,
    onClick: (MoviesPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MoviesViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState().value
    val windowSize = calculateWindowSizeClass(LocalContext.current as Activity)

    val contentType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> MovieContentType.LIST_ONLY
        WindowWidthSizeClass.Medium -> MovieContentType.LIST_ONLY
        WindowWidthSizeClass.Expanded -> MovieContentType.LIST_AND_DETAIL
        else -> MovieContentType.LIST_ONLY
    }

    if (moviesUiState is MoviesUiState.Success) {
        val photos = moviesUiState.photos

        if (contentType == MovieContentType.LIST_AND_DETAIL) {
            MoviesPhotoListAndDetail(
                photo = photos,
                selectedMovie = uiState.currentMovie ?: photos[0],
                onClick = { viewModel.updateCurrentMovie(it) },
                modifier = modifier.fillMaxSize()
            )
        } else {

            if (uiState.isShowingListPage) {
                MoviesList(
                    photos = photos,
                    onClick = { photo ->
                        viewModel.updateCurrentMovie(photo)
                        viewModel.navigateToDetailsPage()
                    },
                    modifier = modifier.fillMaxSize()
                )
            } else {
                MoviesPhotoDetailScreen(
                    selectedMovie = uiState.currentMovie!!,
                    onBackPressed = { viewModel.navigateToListPage() }
                )
            }
        }
    }
}


@Composable
fun MoviesList(photos: List<MoviesPhoto>, onClick:(MoviesPhoto) -> Unit,modifier: Modifier = Modifier,

){
    LazyColumn( modifier = modifier

       ) {

        items(items = photos, key = { photo -> photo.poster }) { photo ->
            MoviesPhotoCard(
                photo = photo ,
                onItemClick = onClick,
                modifier = modifier
                    .fillMaxWidth(),
            )
        }
    }

}

@Composable
fun MoviesPhotoDetailScreen(
    selectedMovie: MoviesPhoto,
    onBackPressed: ()-> Unit,
    modifier: Modifier = Modifier

){
    BackHandler {
        onBackPressed()
    }
    Column (modifier = modifier.padding(8.dp)
        .verticalScroll(state = rememberScrollState())

    ){
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(selectedMovie.bigImage)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.movie_photo),
            modifier = Modifier.fillMaxWidth()
                .height(350.dp)
        )
        Spacer(modifier = modifier.height(16.dp))
        Text(
            text = selectedMovie.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(8.dp))
        Row(modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(
                text ="${selectedMovie.contentRating} | ${selectedMovie.length}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

        }
            Spacer(modifier = modifier.height(4.dp))
        Row(modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(
                text = selectedMovie.releaseDate ,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = modifier.height(4.dp))
        Row (modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(
                text = selectedMovie.reviewScore,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = modifier.height(16.dp))
        Text(
            text = selectedMovie.description,
            style = MaterialTheme.typography.titleMedium
        )


    }

}









@Composable
fun MoviesPhotoCard(photo: MoviesPhoto, onItemClick:(MoviesPhoto) ->Unit, modifier: Modifier = Modifier) {
    Card(
        elevation = cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 8.dp),
        onClick = { onItemClick(photo) }
    ) {
        Row(modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(photo.poster)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.movie_photo),
                modifier = Modifier.width(120.dp)
                    .padding(end = 4.dp)
            )
            Column (modifier = modifier.padding( end = 2.dp)){
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = photo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier.padding(4.dp)


                )
                Spacer(modifier = Modifier.height(8.dp))

                Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    Text(
                        text = photo.reviewScore,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }


            }

        }
    }
}



@Composable
fun MoviesPhotoListAndDetail(
    photo: List<MoviesPhoto>,
    onClick: (MoviesPhoto) -> Unit,
    selectedMovie: MoviesPhoto,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier){
        MoviesList(
            photos = photo,
            onClick = onClick,
            modifier = Modifier.weight(2f)
                .padding(start = 16.dp, end = 16.dp)
        )
        MoviesPhotoDetailScreen(
            selectedMovie = selectedMovie,
            onBackPressed = {},
            modifier = Modifier.weight(3f)
        )
    }
}



@Composable
fun ResultScreen(photos: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = photos)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MovieBuffsTheme {
        ResultScreen("Placeholder result text")
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesPhotoCardPreview() {
    val mockData = MoviesPhoto(
        poster = "android.resource://app/src/main/res/drawable-nodpi.mario",
        title = "Mario",
        description = "While working underground to fix a water main, Brooklyn plumbers-and brothers-Mario and Luigi are transported down a mysterious pipe and wander into a magical new world. But when the brothers are separated, Mario embarks on an epic quest to find Luigi.",
        releaseDate = "Apr 5, 2023",
        contentRating = "PG",
        reviewScore = "7.1",
        bigImage = "big image",
        length = "2h 15m"
    )

    MovieBuffsTheme {
        MoviesPhotoCard(photo = mockData, onItemClick = {})
    }
}


@Preview(showBackground = true)
@Composable
fun MoviesPhotoDetailScreenPreview() {
    val mockData = MoviesPhoto(
        poster = "android.resource://MovieBuffs//drawable-nodpi/mario",
        title = "Mario",
        description = "While working underground to fix a water main, Brooklyn plumbers-and brothers-Mario and Luigi are transported down a mysterious pipe and wander into a magical new world. But when the brothers are separated, Mario embarks on an epic quest to find Luigi.",
        releaseDate = "Apr 5, 2023",
        contentRating = "PG",
        reviewScore = "7.1",
        bigImage = "big image",
        length = "2h 15m"
    )

    MovieBuffsTheme {
        MoviesPhotoDetailScreen(selectedMovie = mockData, onBackPressed = {})
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesListAndDetailPreview() {
    val mockData = listOf(
        MoviesPhoto(
            poster = "android.resource://app/drawable/poster1",
            title = "Movie 1",
            description = "Description for movie 1.",
            releaseDate = "Jan 1, 2024",
            contentRating = "PG",
            reviewScore = "7.5",
            bigImage = "big_image_1",
            length = "1h 30m"
        ),
        MoviesPhoto(
            poster = "android.resource://app/drawable/poster2",
            title = "Movie 2",
            description = "Description for movie 2.",
            releaseDate = "Feb 1, 2024",
            contentRating = "PG-13",
            reviewScore = "8.2",
            bigImage = "big_image_2",
            length = "1h 45m"
        )
    )

    MovieBuffsTheme {
        MoviesPhotoListAndDetail(
            photo = mockData,
            selectedMovie = mockData[0], // Default to the first movie for the detail view
            onClick = {}
        )
    }
}
