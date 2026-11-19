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
| **Min / Target SDK** | 23 / 36 |
| **Architecture** | Single module organized by feature; screen ViewModels + repositories |
| **DI** | Dagger Hilt |
| **UI** | AndroidX AppCompat, Material Components, ConstraintLayout, Data Binding |
| **Navigation** | AndroidX Navigation Component (Safe Args) |
| **Networking** | Retrofit 2, OkHttp, Gson, Logging Interceptor |
| **Local storage** | Room (favorites), DataStore (preferences) |
| **Async** | Kotlin Coroutines, StateFlow, SharedFlow |
| **Lifecycle** | AndroidX Lifecycle (ViewModel, repeatOnLifecycle) |
| **Images** | Coil, Glide |
| **Other** | Jsoup (HTML parsing), Shimmer (loading), CircleImageView |

---

## Project structure

Source code lives under `app/src/main/java/com/oudombun/chelfy`.

```text
app/                    Application, activity host, splash
core/
  common/               Shared constants and request results
  model/                Recipe and API models
  ui/                   Shared widgets and data binding adapters
data/
  local/database/       Room database, DAO, converters, entities
  remote/               Retrofit API, API keys, network monitoring
  preferences/          DataStore preferences
  repository/           RecipeRepository interface and implementation
di/                     Hilt bindings and providers
feature/
  recipes/              Home feed, adapters, RecipesViewModel
  search/               Search screens, adapters, SearchViewModel
  filters/              Filter sheet and FiltersViewModel
  detail/               Recipe details and DetailViewModel
  cooking/              Cooking sheet and step mapping
  favorites/            Favorites screen, adapter, FavoritesViewModel
  mealplan/             Meal planning screens and MealPlanViewModel
```

Each screen exposes read-only StateFlow state from its ViewModel. Fragments collect state inside `viewLifecycleOwner.repeatOnLifecycle(STARTED)`, so collectors stop with the view. Detail feedback uses SharedFlow for transient messages. Views call ViewModel methods, and repositories own network and database access.

Search observes persisted filters and cancels obsolete requests with `transformLatest`. Room and DataStore remain Flow sources. Favorites changes run in a Room transaction and match recipe IDs, preserving the existing database schema. Network monitoring unregisters callbacks when collection ends. The launcher alias preserves the installed app’s original launcher component after the package reorganization.

The UI uses XML layouts, View Binding, and row-level Data Binding. The project uses Kotlin DSL build scripts and `gradle/libs.versions.toml` for dependency versions.

---

## Getting started

1. Clone the repo and open it in Android Studio.
2. Use JDK 17. Existing `KEYSTORE_PASSWORD` and `KEY_PASSWORD` Gradle properties select the project’s signing keys. A fresh checkout can build debug with the standard Android debug key.
3. The app uses [Spoonacular](https://spoonacular.com/food-api). Existing API configuration is in `app/build.gradle.kts`.
4. Build and run on a device or emulator with API 23 or newer.

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest :app:lintDebug
```

The build uses Gradle 8.13, AGP 8.13.2, Kotlin 2.2.10, and Hilt 2.57.2. Coroutine regression tests cover search cancellation, detail reloads, cached meal plans, calorie targets, and cooldown restoration.

---

## Play Store

**Chelfy** is available on Google Play:

**[https://play.google.com/store/apps/details?id=com.oudombun.chelfy](https://play.google.com/store/apps/details?id=com.oudombun.chelfy)**

---

## License

This project is for portfolio/interview use. Recipe data is provided by Spoonacular; see their terms for API usage.
