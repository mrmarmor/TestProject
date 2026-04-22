package com.appstairs.testproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.appstairs.testproject.data.repository.MovieRepository
import com.appstairs.testproject.domain.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {
    val movies: Flow<PagingData<Movie>> = repository.getMoviesPager()
        .cachedIn(viewModelScope) // cache all movies until viewModel destroyed,
                                  // so rotating will not erase data

    // this is a comment that i want to add to the git, so diff function will understand that it is different
    // and github action will summarize it as something done between commits.....
}

// =============================================================================
// גרסת MVI - Model-View-Intent
// =============================================================================
//
// MVI היא ארכיטקטורת זרימת נתונים חד-כיוונית (Unidirectional Data Flow):
//
//   +---------+   Intent   +------------+   State   +---------+
//   |  View   | ---------> | ViewModel  | --------> |  View   |
//   +---------+            +------------+           +---------+
//
// ההבדל העיקרי מ-MVVM:
//   MVVM: ה-View מאזין ישירות לנתונים ומנהל state מקומי (remember / mutableStateOf)
//   MVI:  ה-View רק שולח Intent → ViewModel מעבד ומעדכן → State יחיד מרכזי → View מרנדר
//
// יתרונות MVI:
//   - State ניתן לחיזוי מלא (predictable) - רק ViewModel משנה אותו
//   - קל לבדיקות יחידה: שולחים Intent, בודקים את ה-State שיצא
//   - היסטוריית State ניתנת לתיעוד (time-travel debugging)
//
// =============================================================================

// --- הוספות נדרשות ל-imports (לא פעיל - בהערה) ---
// import kotlinx.coroutines.flow.MutableStateFlow
// import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.asStateFlow
// import kotlinx.coroutines.flow.update
// import kotlinx.coroutines.launch

// --- Intent: כל הפעולות שה-View יכול לשלוח ל-ViewModel ---
// sealed class MovieIntent {
//     object LoadMovies             : MovieIntent() // טעינה ראשונית מה-cache ומהרשת
//     object RefreshMovies          : MovieIntent() // רענון מהרשת (pull-to-refresh)
//     object RetryLoad              : MovieIntent() // ניסיון חוזר לאחר שגיאה
//     data class SelectMovie(
//         val movie: Movie
//     )                             : MovieIntent() // משתמש לחץ על סרט
//     object DismissMovieDetail     : MovieIntent() // משתמש סגר את הדיאלוג
// }

// --- State: מצב ה-UI כולו במקום אחד ---
// data class MovieUiState(
//     val isLoading: Boolean   = false,        // האם מתבצעת טעינה כרגע
//     val error: String?       = null,          // הודעת שגיאה (null = אין שגיאה)
//     val movies: List<Movie>  = emptyList(),   // רשימת הסרטים המוצגת
//     val selectedMovie: Movie? = null          // הסרט שנבחר להצגה בדיאלוג
// )

// --- MVI ViewModel ---
// @HiltViewModel
// class MovieMviViewModel @Inject constructor(
//     private val repository: MovieRepository
// ) : ViewModel() {
//
//     // State מוחזק בתוך MutableStateFlow - ה-View מאזין לו דרך StateFlow (read-only)
//     private val _state = MutableStateFlow(MovieUiState())
//     val state: StateFlow<MovieUiState> = _state.asStateFlow()
//
//     init {
//         processIntent(MovieIntent.LoadMovies)
//     }
//
//     // נקודת כניסה יחידה מה-View - כל פעולה עוברת דרך כאן
//     fun processIntent(intent: MovieIntent) {
//         when (intent) {
//             is MovieIntent.LoadMovies         -> loadMovies()
//             is MovieIntent.RefreshMovies      -> refreshMovies()
//             is MovieIntent.RetryLoad          -> refreshMovies()
//             is MovieIntent.SelectMovie        -> _state.update { it.copy(selectedMovie = intent.movie) }
//             is MovieIntent.DismissMovieDetail -> _state.update { it.copy(selectedMovie = null) }
//         }
//     }
//
//     private fun loadMovies() {
//         viewModelScope.launch {
//             // מאזין ל-Room DB - מתעדכן אוטומטית בכל פעם ש-refreshMovies כותב ל-DB
//             repository.getMovies().collect { movies ->
//                 _state.update { it.copy(movies = movies) }
//             }
//         }
//         refreshMovies() // בטעינה ראשונה מביא גם מהרשת כדי לעדכן את ה-cache
//     }
//
//     private fun refreshMovies() {
//         viewModelScope.launch {
//             _state.update { it.copy(isLoading = true, error = null) }
//             repository.refreshMovies()
//                 .onSuccess {
//                     // רשימת הסרטים תתעדכן אוטומטית דרך ה-Flow של Room שב-loadMovies
//                     _state.update { it.copy(isLoading = false) }
//                 }
//                 .onFailure { e ->
//                     _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
//                 }
//         }
//     }
// }