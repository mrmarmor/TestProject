package com.appstairs.testproject.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.appstairs.testproject.data.api.MovieApiService
import com.appstairs.testproject.domain.models.Movie

class MoviePagingSource(
    private val apiService: MovieApiService,
    private val apiKey: String
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getPopularMovies(apiKey = apiKey, page = page)
            val movies = response.results.map { dto ->
                Movie(
                    id = dto.id,
                    title = dto.title,
                    overview = dto.overview,
                    posterPath = dto.posterPath,
                    backdropPath = dto.backdropPath,
                    releaseDate = dto.releaseDate,
                    voteAverage = dto.voteAverage
                )
            }
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}