package com.example.moviebuffs.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebuffs.ui.network.MoviesApi
import com.example.moviebuffs.ui.network.MoviesPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface MoviesUiState {
    data class Success(val photos: List <MoviesPhoto>) : MoviesUiState
    object Error : MoviesUiState
    object Loading : MoviesUiState
}

data class UiState(
    val currentMovie: MoviesPhoto?,
    val isShowingListPage: Boolean = true
)




class MoviesViewModel : ViewModel() {

    var moviesUiState: MoviesUiState by mutableStateOf(MoviesUiState.Loading)
        private set

    private val _uiState = MutableStateFlow(
        UiState(
            currentMovie = null,
            isShowingListPage = true
        )
    )

    val uiState : StateFlow<UiState> = _uiState

    init {
        getMoviesPhotos()
    }

    fun updateCurrentMovie(selectedMovie: MoviesPhoto){
        _uiState.update {
            it.copy(currentMovie = selectedMovie)
        }
    }

    fun navigateToListPage(){
        _uiState.update {
            it.copy(isShowingListPage = true)
        }
    }
    fun navigateToDetailsPage(){
        _uiState.update {
            it.copy(isShowingListPage = false)
        }
    }


    fun getMoviesPhotos() {
       viewModelScope.launch{
           moviesUiState = try {
               MoviesUiState.Success(MoviesApi.retrofitService.getPhotos())
           }catch (e: IOException) {
               MoviesUiState.Error
           }
    }
}
}