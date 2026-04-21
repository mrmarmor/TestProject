package com.appstairs.testproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.appstairs.testproject.domain.models.Movie
import com.appstairs.testproject.ui.components.MovieDetailDialog
import com.appstairs.testproject.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(viewModel: MovieViewModel = hiltViewModel()) {
    val lazyMovies = viewModel.movies.collectAsLazyPagingItems()
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular Movies") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val refreshState = lazyMovies.loadState.refresh) {
                is LoadState.Loading -> LoadingContent()
                is LoadState.Error -> ErrorContent(
                    message = refreshState.error.message ?: "An unknown error occurred",
                    onRetry = { lazyMovies.retry() }
                )
                else -> MovieGrid(
                    lazyMovies = lazyMovies,
                    onMovieClick = { selectedMovie = it }
                )
            }
        }

        selectedMovie?.let { movie ->
            MovieDetailDialog(
                movie = movie,
                onDismiss = { selectedMovie = null }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading movies...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun MovieGrid(lazyMovies: LazyPagingItems<Movie>, onMovieClick: (Movie) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        //columns = GridCells.Adaptive(minItemSize),//device decides the number of columns according to screen size(this will make app much more responsive to each kind of device)
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // title on whole width:
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "הסרטים הפופולריים ביותר",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(lazyMovies.itemCount) { index ->
            val movie = lazyMovies[index]
            if (movie != null) {
                MovieGridItem(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }

        // Bottom loading indicator when fetching next page
        if (lazyMovies.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Bottom error with retry when next page fails
        if (lazyMovies.loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { lazyMovies.retry() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieGridItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            AsyncImage(
                model = movie.getPosterUrl(),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.67f)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// =============================================================================
// גרסת MVI של ה-Screen
// =============================================================================
//
// ב-MVVM: ה-View אוסף את movies flow ישירות ומנהל selectedMovie עם remember מקומי.
// ב-MVI:  ה-View אוסף State יחיד מה-ViewModel ושולח Intent על כל אינטראקציה.
//
// הוספות נדרשות ל-imports (לא פעיל - בהערה):
// import androidx.lifecycle.compose.collectAsStateWithLifecycle
// import com.appstairs.testproject.ui.viewmodel.MovieMviViewModel
// import com.appstairs.testproject.ui.viewmodel.MovieIntent
//
// =============================================================================

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun MovieListScreenMvi(viewModel: MovieMviViewModel = hiltViewModel()) {
//
//     // אוספים State יחיד במקום מספר flows נפרדים
//     // collectAsStateWithLifecycle עדיף על collectAsState כי הוא מפסיק לאסוף כאשר
//     // ה-lifecycle לא פעיל (רקע) - חוסך משאבים
//     val state by viewModel.state.collectAsStateWithLifecycle()
//
//     Scaffold(
//         topBar = {
//             TopAppBar(
//                 title = { Text("Popular Movies") },
//                 colors = TopAppBarDefaults.topAppBarColors(
//                     containerColor = MaterialTheme.colorScheme.primaryContainer,
//                     titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                 )
//             )
//         }
//     ) { paddingValues ->
//         Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
//
//             // ה-View מחליט מה להציג לפי State - ללא לוגיקה עצמאית
//             when {
//                 state.isLoading && state.movies.isEmpty() -> LoadingContent()
//
//                 state.error != null && state.movies.isEmpty() -> ErrorContent(
//                     message = state.error!!,
//                     // שליחת Intent במקום קריאה ישירה לפונקציה
//                     onRetry = { viewModel.processIntent(MovieIntent.RetryLoad) }
//                 )
//
//                 else -> MovieListMviContent(
//                     state = state,
//                     onMovieClick = { movie ->
//                         viewModel.processIntent(MovieIntent.SelectMovie(movie))
//                     },
//                     onRefresh = {
//                         viewModel.processIntent(MovieIntent.RefreshMovies)
//                     }
//                 )
//             }
//         }
//
//         // דיאלוג מפרטים - מוצג כאשר selectedMovie != null ב-State
//         state.selectedMovie?.let { movie ->
//             MovieDetailDialog(
//                 movie = movie,
//                 // סגירת הדיאלוג = שליחת Intent, לא שינוי state מקומי
//                 onDismiss = { viewModel.processIntent(MovieIntent.DismissMovieDetail) }
//             )
//         }
//     }
// }

// תוכן הרשימה - מקבל State ו-callbacks לשליחת Intents
// @Composable
// private fun MovieListMviContent(
//     state: MovieUiState,
//     onMovieClick: (Movie) -> Unit,
//     onRefresh: () -> Unit
// ) {
//     LazyVerticalGrid(
//         columns = GridCells.Fixed(2),
//         contentPadding = PaddingValues(8.dp),
//         horizontalArrangement = Arrangement.spacedBy(8.dp),
//         verticalArrangement = Arrangement.spacedBy(8.dp)
//     ) {
//         item(span = { GridItemSpan(maxLineSpan) }) {
//             Text(
//                 text = "הסרטים הפופולריים ביותר",
//                 style = MaterialTheme.typography.headlineMedium,
//                 modifier = Modifier.padding(bottom = 16.dp)
//             )
//         }
//
//         // ב-MVI הרשימה מגיעה מה-State - לא מ-LazyPagingItems
//         items(state.movies.size) { index ->
//             MovieGridItem(
//                 movie = state.movies[index],
//                 onClick = { onMovieClick(state.movies[index]) }
//             )
//         }
//
//         // אינדיקטור טעינה בתחתית (pagination ידנית או pull-to-refresh)
//         if (state.isLoading && state.movies.isNotEmpty()) {
//             item(span = { GridItemSpan(maxLineSpan) }) {
//                 Box(
//                     modifier = Modifier.fillMaxWidth().padding(16.dp),
//                     contentAlignment = Alignment.Center
//                 ) {
//                     CircularProgressIndicator()
//                 }
//             }
//         }
//
//         // שגיאה עם Retry בתחתית (כאשר יש כבר נתונים + שגיאה ברענון)
//         if (state.error != null && state.movies.isNotEmpty()) {
//             item(span = { GridItemSpan(maxLineSpan) }) {
//                 Box(
//                     modifier = Modifier.fillMaxWidth().padding(16.dp),
//                     contentAlignment = Alignment.Center
//                 ) {
//                     Button(onClick = onRefresh) { Text("Retry") }
//                 }
//             }
//         }
//     }
// }