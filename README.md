# Offline Day Planner v2

An Android application built with Kotlin and Jetpack Compose for offline day planning.

## Features

- Offline day planning
- Modern Material 3 UI
- Built with Jetpack Compose
- Room database for local storage

## Getting the APK

### Option 1: Download from GitHub Actions (Recommended)

1. Go to the **Actions** tab in this repository
2. Click on the latest successful workflow run
3. Scroll down to **Artifacts**
4. Click on **OfflineDayPlanner-APK** to download the APK file

### Option 2: Build Locally

If you want to build the APK locally:

1. Install Java JDK 17 or later
2. Clone this repository
3. Run: `./gradlew assembleRelease`
4. Find the APK in: `app/build/outputs/apk/release/app-release.apk`

## Requirements

- Android API level 26+ (Android 8.0+)
- Target SDK: 35

## Development

This project uses:
- Kotlin
- Jetpack Compose
- Room Database
- Material 3 Design
- Navigation Compose

## License

This project is open source.
