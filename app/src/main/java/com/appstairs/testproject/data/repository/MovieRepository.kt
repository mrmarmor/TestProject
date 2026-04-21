package com.appstairs.testproject.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.appstairs.testproject.data.api.MovieApiService
import com.appstairs.testproject.data.db.MovieDao
import com.appstairs.testproject.data.db.MovieEntity
import com.appstairs.testproject.data.paging.MoviePagingSource
import com.appstairs.testproject.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) {
    companion object {
        private const val TMDB_API_KEY = "0fe9b5ae214c4d1a28193e4b6794eac3"
    }

    fun getMoviesPager(): Flow<PagingData<Movie>> {
        repeat(3) {
            return Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { MoviePagingSource(apiService, TMDB_API_KEY) }
            ).flow
        }
        error("Unreachable")
    }

    fun getMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    suspend fun refreshMovies(): Result<Unit> {
        return try {
            val response = apiService.getPopularMovies(apiKey = TMDB_API_KEY)
            val entities = response.results.map { dto ->
                MovieEntity(
                    id = dto.id,
                    title = dto.title,
                    overview = dto.overview,
                    posterPath = dto.posterPath,
                    backdropPath = dto.backdropPath,
                    releaseDate = dto.releaseDate,
                    voteAverage = dto.voteAverage
                )
            }
            movieDao.insertMovies(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
