Fitness Assistant 💪 | SDG 3: Good Health and Well-being
Fitness Assistant is a comprehensive Android fitness and nutrition companion designed to help users track workouts, monitor nutrition intake, and stay motivated through community engagement. Built with Jetpack Compose, it leverages modern Android architecture to provide a seamless, high-performance experience.

The application aligns with SDG 3: Good Health and Well-being by promoting healthy eating habits through barcode scanning, encouraging regular exercise, and fostering a supportive fitness community.

✨ Features
📷 Smart Barcode Scanner
Camera Integration: Uses CameraX and ML Kit to scan food barcodes in real-time.

Live Nutrition Data: Fetches product information from OpenFoodFacts API (calories, protein, carbs, fat).

Offline Storage: Scanned items saved locally to Room Database.

☁️ Community Workout Sharing
Cloud Storage: Share workout achievements via Firebase Firestore.

Real-time Feed: View workouts shared by other users globally.

🏋️ Personal Fitness Tracking
Profile Management: Save name and fitness goal locally using Room Database.

7 Screens: Workout, Challenges, History, Profile, Scanner, Nutrition, Community.

Nutrition Dashboard: Track daily calorie and macronutrient intake.

🛠 Tech Stack
Language: Kotlin

UI Framework: Jetpack Compose (Material 3)

Local Database: Room Persistence Library

Cloud Database: Firebase Firestore

Networking: Retrofit + OpenFoodFacts API

Camera/Barcode: CameraX + ML Kit

Permissions: Accompanist Permissions

Concurrency: Kotlin Coroutines & Flow

Architecture: MVVM (ViewModel + Repository)

🚀 Getting Started
Clone the repository:

bash
git clone https://github.com/YOUR_USERNAME/A207351_CikguIzwan_Project2.git
Setup Firebase:

Add google-services.json to the app/ directory

Enable Firestore in your Firebase Console

Build and Run:

Open in Android Studio (Ladybug or newer)

Sync Gradle and run on an emulator or physical device

Developed by: ZHU PEIYI (A207351)
Project: Project 2 - Mobile Programming (TK2323/TM2213)
