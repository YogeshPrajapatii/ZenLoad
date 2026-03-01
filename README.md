# ZenLoad 🚀

A modern, high-performance Media Downloader for Android built with Clean Architecture. ZenLoad goes beyond simple API calls by integrating native Python (`yt-dlp`) and `FFmpeg` engines directly into the app to fetch, merge, and download high-quality media streams in the background.

## 📸 Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/9429a6f4-6bf8-41ad-97fe-a20a865d7b15" width="24%" />
  <img src="https://github.com/user-attachments/assets/67225105-d251-4cc2-9946-588bcd264496" width="24%" />
  <img src="https://github.com/user-attachments/assets/ab6a6ab2-a80a-4ac6-8b0d-55d6a251be18" width="24%" />
  <img src="https://github.com/user-attachments/assets/6c2d3dfe-0618-460c-ac3d-517dfdccb7da" width="24%" />
</p>

## ✨ Key Features
* 🎨 **Glassmorphic UI:** Built entirely with **Jetpack Compose**, featuring a highly responsive design and seamless Light/Dark mode toggling.
* ⚙️ **Native Core Integration:** Embedded `yt-dlp` and `FFmpeg` to extract audio/video formats (up to 4K and 320kbps).
* ⬆️ **Background Resilience:** Utilizes **WorkManager** and Foreground Services to ensure downloads continue flawlessly even if the app is minimized.
* 📂 **File Management:** Manages real device storage using the Storage Access Framework (SAF) and tracks media via **Room Database**.
* 🎬 **Direct Playback:** Uses Android Intents and `FileProvider` to open downloaded media directly from the app's library.

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM
* **Dependency Injection:** Dagger-Hilt
* **Concurrency:** Coroutines & Flow
* **Background Tasks:** WorkManager
* **Local Database:** Room
* **Media Engines:** yt-dlp-android, FFmpeg

## 🚀 Getting Started
1. Clone the repository: `git clone https://github.com/YogeshPrajapatii/ZenLoad.git`
2. Open the project in Android Studio.
3. Build and Run on your device/emulator.

**Note on First Launch:** *The app fetches the absolute latest `yt-dlp` core on startup. The very first format fetch might take 10-15 seconds as it initializes the engine. Subsequent fetches are much faster.*
