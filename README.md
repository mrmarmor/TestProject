# Movie App

A Kotlin Android application that fetches and displays popular movies from The Movie Database (TMDB) API.

## Features

- Displays a scrollable grid of popular movies with poster thumbnails
- Tap on a movie to view full details in a dialog
- Offline support with Room database caching
- Network-first approach with offline fallback
- MVVM architecture with Jetpack Compose
- Dependency injection with Hilt
- Async image loading with Coil
- Coroutines and Flow for reactive data

## Tech Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - ViewModel + UiState pattern
- **Hilt** - Dependency injection
- **Coroutines + Flow** - Asynchronous programming
- **Retrofit + OkHttp** - Network calls with logging interceptor
- **Room Database** - Local data persistence
- **Coil** - Image loading

## Project Structure

```
app/src/main/java/com/appstairs/testproject/
├── domain/
│   └── models/
│       └── Movie.kt              # Domain model
├── data/
│   ├── api/
│   │   ├── MovieApiService.kt    # Retrofit API interface
│   │   └── dto/                  # Data transfer objects
│   ├── db/
│   │   ├── MovieDatabase.kt      # Room database
│   │   ├── MovieDao.kt           # Database access object
│   │   └── MovieEntity.kt        # Database entity
│   └── repository/
│       └── MovieRepository.kt    # Repository pattern
├── di/
│   ├── NetworkModule.kt          # Network DI module
│   └── DatabaseModule.kt         # Database DI module
├── ui/
│   ├── screens/
│   │   └── MovieListScreen.kt    # Main screen with grid
│   ├── viewmodel/
│   │   └── MovieViewModel.kt     # ViewModel with UiState
│   └── components/
│       └── MovieDetailDialog.kt  # Detail dialog component
├── MainActivity.kt                # Entry point
└── MovieApplication.kt            # Application class with Hilt

```

## Setup Instructions

### 1. Get TMDB API Key

1. Go to [The Movie Database](https://www.themoviedb.org/)
2. Create a free account
3. Go to Settings > API
4. Request an API key (choose "Developer" option)
5. Copy your API key

### 2. Add API Key to the Project

Open `app/src/main/java/com/appstairs/testproject/data/repository/MovieRepository.kt`

Replace the placeholder with your actual API key:

```kotlin
companion object {
    private const val TMDB_API_KEY = "YOUR_TMDB_API_KEY_HERE"  // <-- Add your key here
}
```

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Run the app on an emulator or physical device

## Requirements

- Android Studio (latest stable version)
- Minimum SDK: 26 (Android 8.0)
- Target SDK: 36
- JDK 11 or higher
- Internet connection for fetching movies

## How It Works

1. **Network First**: The app always tries to fetch fresh data from TMDB API
2. **Cache Updates**: Successful API responses are cached in Room database
3. **Offline Fallback**: If network fails, the app displays cached data
4. **Error Handling**: If both network and cache fail, an error message with retry button is shown

## UI Overview

- **Main Screen**: 2-column grid showing movie posters with titles
- **Loading State**: Spinner with loading message
- **Error State**: Error message with retry button
- **Detail Dialog**: Full-size poster, title, release date, rating, and overview

## Dependencies

All dependencies are managed in `gradle/libs.versions.toml`:

- Compose BOM 2024.09.00
- Hilt 2.50
- Retrofit 2.9.0
- Room 2.6.1
- Coil 2.5.0
- OkHttp 4.12.0
- Coroutines 1.7.3

## License

This project is for educational purposes.
