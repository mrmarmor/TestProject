# Quick Setup Guide

## Step 1: Get Your TMDB API Key

1. Visit https://www.themoviedb.org/
2. Sign up for a free account
3. Navigate to: **Settings → API**
4. Request an API key (select "Developer")
5. Copy your API key (v3 auth)

## Step 2: Add API Key to Project

Open this file:
```
app/src/main/java/com/appstairs/testproject/data/repository/MovieRepository.kt
```

Find line 21 and replace `YOUR_TMDB_API_KEY_HERE` with your actual API key:

```kotlin
private const val TMDB_API_KEY = "your_actual_api_key_here"
```

## Step 3: Build and Run

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click the "Run" button (green play icon) or press Shift+F10
4. Select your emulator or connected device

## What You'll See

- **Main Screen**: A 2-column grid of popular movie posters
- **Tap any movie**: Opens a dialog with:
  - Full-size poster image
  - Movie title
  - Release date and rating
  - Movie overview/description
  - Close button

## Features

- **Offline Mode**: Movies are cached locally, so they're available even without internet
- **Network First**: Always tries to fetch fresh data from TMDB
- **Loading States**: Shows a spinner while loading
- **Error Handling**: Displays error message with retry button if something goes wrong

## Troubleshooting

### Build Errors?
- Make sure you're using JDK 11 or higher
- Run `./gradlew clean` and then rebuild

### Network Errors?
- Check that you added a valid API key
- Ensure your device/emulator has internet access
- Check that the API key is active on TMDB

### No Movies Showing?
- Check logcat for API errors
- Verify the API key is correct
- Make sure the INTERNET permission is in AndroidManifest.xml (it should be)

## Project Stats

- **18 Kotlin files** created
- **MVVM architecture** with clean separation of concerns
- **Full dependency injection** with Hilt
- **Offline-first** with Room database caching
- **Modern UI** with Jetpack Compose
- **Production-ready** architecture
