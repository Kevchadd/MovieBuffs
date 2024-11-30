package com.example.moviebuffs.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviebuffs.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesBuffsApp() {
    val viewModel: MoviesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MoviesTopAppBar(
                isShowingListPage = uiState.isShowingListPage,
                onBackButtonClick = {viewModel.navigateToListPage()},
                //scrollBehavior = scrollBehavior
            ) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val moviesViewModel: MoviesViewModel = viewModel()
            if(uiState.isShowingListPage) {
                HomeScreen(
                    onClick = {
                        viewModel.updateCurrentMovie(it)
                        viewModel.navigateToDetailsPage()
                    },
                    moviesUiState = moviesViewModel.moviesUiState,
                    retryAction = moviesViewModel::getMoviesPhotos
                )
            }else{
                MoviesPhotoDetailScreen(
                    selectedMovie = uiState.currentMovie!!,
                    onBackPressed = {
                        viewModel.navigateToListPage()
                    }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesTopAppBar(
    onBackButtonClick: () -> Unit,
    isShowingListPage: Boolean,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = if (!isShowingListPage) {
                    stringResource(R.string.app_name)
                } else {
                    stringResource(R.string.app_name)
                }
            )
        },
        navigationIcon = if (!isShowingListPage) {
            {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        } else {
            { Box {} }
        },
        //colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier,
    )
}