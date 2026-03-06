# Chelfy

A food recipe Android app that brings culinary inspiration to your pocket. Browse recipes, save favorites, search by ingredients or diet, and get daily meal plans—powered by the [Spoonacular](https://spoonacular.com/food-api) API.

[![Get it on Google Play](https://img.shields.io/badge/Google_Play-Get_the_app-414141?style=for-the-badge&logo=google-play)](https://play.google.com/store/apps/details?id=com.oudombun.chelfy)

---

## Features

- **Recipe discovery** – Browse recipes with filters for meal type (main course, dessert, breakfast, etc.) and diet (vegetarian, vegan, ketogenic, gluten-free, etc.).
- **Search** – Search recipes by keyword; results open from Home or Search with optional filter bottom sheet.
- **Recipe details** – View ingredients, step-by-step instructions (analyzed instructions), and open instructions in a bottom sheet.
- **Favorites** – Save and remove recipes to a local favorites list; data persists with Room (works offline).
- **Meal plan** – Generate daily meal plans with optional target calories and diet; open meal items in a detail screen.
- **Food jokes** – Light-hearted food jokes in the “Food Joke” tab.
- **Preferences** – Meal type and diet type preferences are stored with DataStore and used for default filters.
- **UI/UX** – Splash screen, bottom navigation (Home, Favorites, Food Joke), navigation transitions, shimmer loading, and dark/light theme support.

---

## Tech stack

| Category | Technologies |
|----------|--------------|
| **Language** | Kotlin |
| **Min / Target SDK** | 23 / 33 |
| **Architecture** | Single module; ViewModel + Repository pattern |
| **DI** | Dagger Hilt |
| **UI** | AndroidX AppCompat, Material Components, ConstraintLayout, Data Binding |
| **Navigation** | AndroidX Navigation Component (Safe Args) |
| **Networking** | Retrofit 2, OkHttp, Gson, Logging Interceptor |
| **Local storage** | Room (favorites), DataStore (preferences) |
| **Async** | Kotlin Coroutines, Flow, LiveData |
| **Lifecycle** | AndroidX Lifecycle (ViewModel, LiveData) |
| **Images** | Coil, Glide |
| **Other** | Jsoup (HTML parsing), Shimmer (loading), CircleImageView |

---

## Project structure (high level)

- `app/src/main/java/com/oudombun/chelfy/`
  - **data** – API (`FoodRecipesApi`), `RemoteDataSource`, `Repository`, `DataStoreRepository`, Room entities/DAOs
  - **ui** – `SplashScreen`, `MainActivity`, `DetailsActivity`, fragments (recipes, favorites, search, meal plan), bottom sheets
  - **viewmodel** – `RecipeViewModel`, `MainViewModel`
  - **bindingAdapter** – Data binding adapters for lists and UI
  - **adapters** – RecyclerView adapters (e.g. `FavoriteRecipesAdapter`)
  - **models** – Data classes for API and UI
  - **util** – Constants, extensions, diff util

---

## Getting started

1. Clone the repo and open it in Android Studio.
2. Add `KEYSTORE_PASSWORD` and `KEY_PASSWORD` in `gradle.properties` if you need to build signed debug/release (see `app/build.gradle`).
3. The app uses [Spoonacular](https://spoonacular.com/food-api); API keys are in `app/build.gradle` (replace with your own for production).
4. Build and run on a device or emulator (API 23+).

---

## Play Store

**Chelfy** is available on Google Play:

**[https://play.google.com/store/apps/details?id=com.oudombun.chelfy](https://play.google.com/store/apps/details?id=com.oudombun.chelfy)**

---

## License

This project is for portfolio/interview use. Recipe data is provided by Spoonacular; see their terms for API usage.
