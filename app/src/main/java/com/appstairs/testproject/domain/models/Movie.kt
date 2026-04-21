package com.appstairs.testproject.domain.models

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double
) {
    fun getPosterUrl(): String {
        return "https://image.tmdb.org/t/p/w500$posterPath"
    }

    fun getFullPosterUrl(): String {
        return "https://image.tmdb.org/t/p/original$posterPath"
    }
}
