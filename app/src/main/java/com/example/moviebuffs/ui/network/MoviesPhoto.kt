package com.example.moviebuffs.ui.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MoviesPhoto(

    val title : String,
    val poster: String,
    val description: String,
    @SerialName(value = "release_date")
    val releaseDate: String,
    @SerialName(value = "content_rating")
    val contentRating: String,
    @SerialName(value = "review_score")
    val reviewScore: String,
    @SerialName(value = "big_image")
    val bigImage: String,
    val length: String


)
