# Offline Day Planner v2

An Android application built with Kotlin and Jetpack Compose for offline day planning.

## Features

- Offline day planning
- Modern Material 3 UI
- Built with Jetpack Compose
- Room database for local storage
- Task management with notifications

## Getting the APK

### Option 1: Download from GitHub Actions (Recommended)

#### For Development/Testing:
1. Go to the **Actions** tab in this repository
2. Click on the latest successful **"Build Android APK"** workflow run
3. Scroll down to **Artifacts**
4. Download **OfflineDayPlanner-APK** (Release version) or **OfflineDayPlanner-Debug-APK** (Debug version)

#### For Production Releases:
1. Go to the **Releases** tab in this repository
2. Click on the latest release
3. Download the APK file from the release assets

### Option 2: Build Locally

If you want to build the APK locally:

1. Install Java JDK 17 or later
2. Clone this repository
3. Run: `./gradlew assembleRelease` (for release APK)
4. Run: `./gradlew assembleDebug` (for debug APK)
5. Find the APK in: `app/build/outputs/apk/release/app-release.apk` or `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions Workflows

This repository includes several automated workflows:

- **Build Android APK**: Builds both debug and release APKs on every push
- **Build Signed Android APK**: Advanced build with additional optimizations
- **Release APK**: Creates GitHub releases with APK files when tags are pushed

## Creating a New Release

To create a new release with APK:

```bash
git tag v2.1.0
git push origin v2.1.0
```

This will automatically trigger the release workflow and create a GitHub release with the APK.

## Requirements

- Android API level 26+ (Android 8.0+)
- Target SDK: 35
- Java 17+ for building

## Development

This project uses:
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Room Database** - Local data persistence
- **Material 3 Design** - Material Design components
- **Navigation Compose** - Navigation between screens
- **KSP** - Kotlin Symbol Processing for Room

## Build Optimizations

The release build includes:
- Code minification and obfuscation
- Resource shrinking
- APK splitting for smaller downloads
- Universal APK support for all architectures

## License

This project is open source.
