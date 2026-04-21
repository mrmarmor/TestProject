package com.appstairs.testproject.data.api

import com.appstairs.testproject.data.api.dto.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MoviesResponse
}
