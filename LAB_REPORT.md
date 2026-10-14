# Practice 6 — Architectural Design Patterns Laboratory Report

**University Laboratory Assignment**  
**Project Name:** WeatherPatterns  
**Package Name:** `uz.ttpu.weatherpatterns`  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose & Material 3  
**Target / Minimum SDK:** API 37 / API 24  

---

## 1. Laboratory Title & Objective

The objective of this laboratory assignment is to design, implement, compare, and test four fundamental Android software architectural patterns—**Model-View-Controller (MVC)**, **Model-View-Presenter (MVP)**, **Model-View-ViewModel (MVVM)**, and **Model-View-Intent (MVI)**—within a single, modern Android application using Kotlin and Jetpack Compose. All four pattern implementations reuse a single, unified shared data layer (API and Repository) to demonstrate clean architecture, lifecycle management, state survival across screen rotations, and unit testability.

---

## 2. Technologies Used

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose (BOM 2026.02.01) with Material 3 UI Components
- **Architecture Components:** AndroidX Lifecycle Runtime Compose (`2.8.7`), ViewModel Compose (`2.8.7`)
- **Asynchronous Execution:** Kotlin Coroutines (`StateFlow`, `viewModelScope`, `launch`, `delay`)
- **Testing Frameworks:** JUnit 4 (`4.13.2`), Kotlinx Coroutines Test (`1.9.0`)
- **Build System:** Android Gradle Plugin (AGP `9.4.1`) & Gradle Version Catalog (`libs.versions.toml`)

---

## 3. Project Structure

```text
uz.ttpu.weatherpatterns
├── MainActivity.kt                      // Main launcher screen with explicit intents
├── data
│   ├── api
│   │   ├── WeatherApi.kt                // Network API interface
│   │   └── FakeWeatherApi.kt            // Deterministic fake API with configurable latency
│   ├── model
│   │   └── Weather.kt                   // Weather domain data model
│   └── repository
│       └── WeatherRepository.kt         // Repository interface and implementation returning Result<Weather>
├── di
│   └── WeatherModule.kt                 // Dependency provider singleton & ViewModel factories
├── ui
│   ├── WeatherScreen.kt                 // Material 3 Compose shared weather UI
│   └── theme/                           // Color, Type, and Theme definitions
├── mvc
│   ├── model/WeatherModel.kt           // MVC Model holding state and formatting helpers
│   ├── view/WeatherView.kt             // View interface with no-parameter render()
│   ├── view/MvcActivity.kt             // View implementation querying Model directly
│   └── controller/WeatherController.kt // Controller handling input & triggering render()
├── mvp
│   ├── WeatherContract.kt              // Contract defining View and Presenter interfaces
│   ├── presenter/WeatherPresenter.kt    // Passive View Presenter with lifecycle detachView()
│   └── view/MvpActivity.kt             // Concrete Passive View implementation
├── mvvm
│   ├── viewmodel/WeatherUiState.kt      // Sealed interface representing UI states (Idle, Loading, Success, Error)
│   ├── viewmodel/WeatherViewModel.kt    // ViewModel exposing StateFlow<WeatherUiState>
│   └── view/MvvmActivity.kt             // Activity observing uiState via collectAsStateWithLifecycle()
└── mvi
    ├── model/WeatherState.kt           // Immutable unified MVI screen state
    ├── model/WeatherIntent.kt          // User intents (SelectCity, DismissError)
    ├── viewmodel/WeatherMviViewModel.kt // ViewModel processing intents via single onIntent() entry point
    └── view/MviActivity.kt             // Activity emitting intents and observing StateFlow<WeatherState>
```

---

## 4. Comprehensive Theoretical & Practical Assignment Answers

### Question 1: Purpose of Architectural Patterns in Android
Architectural patterns structure code to enforce **Separation of Concerns (SoC)**, **Decoupling**, and **Testability**. In Android, UI components (`Activity`, `Fragment`, Composables) are tightly coupled to OS lifecycle events. Architectural patterns isolate business logic, data manipulation, and state management from the Android UI framework, preventing bloated classes ("God Activities"), memory leaks, and making applications easily testable using local JVM unit tests without needing an emulator.

### Question 2: Shared Data Layer & Coroutine Cancellation
The application reuses a single shared data layer across all 4 architectural patterns:
- **`Weather` Data Class:** Domain entity storing `city: String`, `temperatureC: Int`, and `condition: String`.
- **`WeatherApi` & `FakeWeatherApi`:** Defines `suspend fun fetchWeather(city: String): Weather`. The fake implementation provides forecasts for Tashkent (24°C, Sunny), Samarkand (21°C, Cloudy), and Bukhara (27°C, Windy), throwing `IllegalArgumentException("No forecast for $city")` for unknown cities (e.g. "Atlantis").
- **`WeatherRepository` & Cancellation Safety:** Returns `Result<Weather>`. Crucially, `WeatherRepositoryImpl` uses an explicit `try-catch` block that rethrows `CancellationException` rather than swallowing it inside `runCatching`. This preserves Kotlin Coroutines structured concurrency cancellation behavior.

### Question 3: Repository Unit Testing Strategy
Repository tests in `WeatherRepositoryTest.kt` instantiate `FakeWeatherApi(latencyMs = 0L)` to eliminate network delays and run synchronously inside JUnit 4 `runBlocking` blocks.
- **Test 1:** Requests "Tashkent" and verifies `result.isSuccess == true` with temperature 24°C.
- **Test 2:** Requests "Atlantis" and verifies `result.isFailure == true` containing `IllegalArgumentException("No forecast for Atlantis")`.

### Question 4: Main Launcher Navigation & Intent Routing
`MainActivity.kt` acts as the entry point with 4 Material 3 buttons. Tapping a button fires an explicit Android `Intent` (`Intent(this, MvcActivity::class.java)`, etc.) to launch the requested architectural demonstration. Each Activity is declared in `AndroidManifest.xml` with `exported = false`. Pressing the Android back button or gesture smoothly returns the user to the launcher menu.

### Question 5: Component Roles & Data/Control Flow in Classic MVC
- **Model (`WeatherModel`):** Stores raw state (`isLoading`, `weather`, `error`) and formatting logic (`toStatusText()`).
- **View (`MvcActivity`):** Captures user taps, passes them to the Controller, and queries the Model directly during rendering.
- **Controller (`WeatherController`):** Receives user input from the View, mutates the Model state, and explicitly calls `view.render()`.
- **Control/Data Flow:** User Input $\rightarrow$ View $\rightarrow$ Controller $\rightarrow$ Mutates Model $\rightarrow$ Controller calls `view.render()` $\rightarrow$ View queries Model directly $\rightarrow$ UI updates.

### Question 6: Why `render()` in Classic MVC Has No Parameters
In traditional MVC, `render()` takes no parameters because the View holds a direct reference to the Model instance. When notified by the Controller, the View queries state directly from the Model (`model.toStatusText()`). In contrast, Passive View MVP or MVVM pass state objects or state flows directly to the View via method parameters or reactive streams.

### Question 7: Passive View MVP vs Classic MVC
In Passive View MVP, the View is completely "dumb" and contains zero domain logic. The Presenter handles all logic, formatting, and explicitly pushes ready-to-display values into View callback interface methods (`showLoading`, `showWeather`, `showError`). Unlike MVC, the MVP View never inspects domain models directly, dramatically improving testability.

### Question 8: MVP Unit Testing with Fake View
In `WeatherPresenterTest.kt`, `WeatherPresenter` is tested using a lightweight `TestWeatherView` that records callback events into a list (`events.add("weather:Tashkent")`). Injecting `CoroutineScope(Dispatchers.Unconfined)` executes coroutines synchronously, allowing 100% of presenter logic to be tested in local JVM unit tests without Android OS dependencies.

### Question 9: MVP Lifecycle, Detachment Fix & Screen Rotation Notes
- **Lifecycle Fix:** In `MvpActivity.onDestroy()`, `presenter.detachView()` is called. The Presenter cancels active fetch jobs and sets `view = null`. All callback deliveries are guarded (`if (view != null) { ... }`), preventing memory leaks and attempts to modify destroyed Activities.
- **Screen Rotation Observation:** When the device is rotated, Android destroys and recreates `MvpActivity`. Because the Presenter is instantiated inside `onCreate()`, rotation destroys the old Presenter and creates a new one, resetting UI state to initial idle state. To survive rotation in MVP, state must be saved in `onSaveInstanceState` or held in a retained component.

### Question 10: MVVM Package Classification & Framework Independence
`WeatherViewModel` and `WeatherUiState` belong strictly to the **Presentation Layer** (`uz.ttpu.weatherpatterns.mvvm.viewmodel`). They depend only on Kotlin Coroutines and Jetpack Lifecycle (`ViewModel`), containing zero imports of `android.view.*`, `android.content.*`, or Compose UI classes.

### Question 11: MVVM State Survival Across Screen Rotation
- **Survival Mechanism:** When the screen rotates, Android's `ViewModelStoreOwner` retains the `WeatherViewModel` instance in memory across the destruction-recreation boundary.
- **In-flight Coroutine Work:** Active coroutines launched inside `viewModelScope` are bound to the ViewModel lifetime, continuing uninterrupted during rotation. When `MvvmActivity` is recreated, `collectAsStateWithLifecycle()` re-subscribes to `StateFlow<WeatherUiState>` and immediately displays the latest forecast without refetching.

### Question 12: Four Common MVI Architectural Violations
1. Exposing multiple public state mutation functions instead of a single `onIntent(intent)` entry point.
2. Modifying state directly inside Compose UI elements or Activities.
3. Exposing `MutableStateFlow` directly instead of a read-only `StateFlow`.
4. Overwriting state non-atomically without preserving previous unchanged state properties via `copy()`.

### Question 13: Single StateFlow & Unidirectional Data Flow Benefits in MVI
MVI enforces Unidirectional Data Flow (User Action $\rightarrow$ `WeatherIntent` $\rightarrow$ ViewModel Processing $\rightarrow$ Immutable State $\rightarrow$ UI Render). Emitting a single immutable `WeatherState` via a single `StateFlow` guarantees that the UI can never enter contradictory parallel states (e.g., displaying both a loading spinner and an error banner simultaneously).

### Question 14: Comprehensive Architectural Comparison Table

| Pattern | Main Logic-Holding Class | Imports `android.*`? | Imports `androidx.*`? | What the View Holds a Reference To | Survives Rotation? |
| :--- | :--- | :---: | :---: | :--- | :---: |
| **MVC** | `WeatherController` | **NO** | **NO** | Direct reference to `WeatherModel` (to query state) and `WeatherController` (to send input). | **NO** (Re-instantiated in `Activity.onCreate()`) |
| **MVP** | `WeatherPresenter` | **YES** (`android.util.Log`) | **NO** | Reference to `WeatherContract.Presenter` interface. | **NO** (Detached/destroyed in `Activity.onDestroy()`) |
| **MVVM** | `WeatherViewModel` | **NO** | **YES** (`androidx.lifecycle.ViewModel`, `viewModelScope`) | Reference to `WeatherViewModel` (observes `StateFlow<WeatherUiState>`). | **YES** (Retained by Android `ViewModelStoreOwner`) |
| **MVI** | `WeatherMviViewModel` | **NO** | **YES** (`androidx.lifecycle.ViewModel`, `viewModelScope`) | Reference to `WeatherMviViewModel` (observes single `StateFlow<WeatherState>` and emits `WeatherIntent`). | **YES** (Retained by Android `ViewModelStoreOwner`) |

### Question 15: Scenario-Based Architectural Pattern Selection

#### Scenario 1: About Screen with Two Static Text Views
- **Recommendation:** **No Architecture / Simple Composable (Direct Data Layer Access).**
- **Lecture Justification:** YAGNI (You Aren't Gonna Need It) & K.I.S.S. (Keep It Simple, Stupid). Introducing ViewModels, Presenters, or StateFlows for completely static text adds unnecessary boilerplate without architectural benefit.
- **Trade-off:** Minimal structural abstraction, but optimal for static displays.

#### Scenario 2: Live Chat Screen (Message List, Typing Indicator, Connection Status)
- **Recommendation:** **Model-View-Intent (MVI) or MVVM with Single StateFlow.**
- **Lecture Justification:** Complex multi-state asynchronous UIs require strict Unidirectional Data Flow and immutable state models. MVI prevents race conditions between typing indicators, incoming messages, and network reconnect events.
- **Trade-off:** Higher initial boilerplate (Intent types, state copy operations), but vastly superior state predictability and debugging.

#### Scenario 3: 5-Year-Old App with 40 Activities Accessing Database Directly, 2 Developers, Release in 1 Month
- **Recommendation:** **Pragmatic Layered MVVM (or Repository Pattern Extraction First).**
- **Lecture Justification:** Incremental Refactoring & Risk Minimization. Rewriting 40 Activities in 30 days with 2 developers will guarantee regression bugs and release failure. The team should extract a shared Repository data layer first, then migrate critical high-churn Activities to MVVM using Jetpack ViewModels.
- **Trade-off:** Temporary coexistence of legacy and modern code, but ensures a stable on-time release.

#### Coexistence in a Single Application
**Yes, different architectures can coexist in a single application.** Modern Android applications frequently use simpler patterns for static screens (e.g. About, Terms of Service) alongside MVVM or MVI for complex dynamic features (e.g. Live Chat, Checkout).

---

## 5. Verification & Test Execution Results

All 10 unit tests executed successfully on the local JVM via `:app:testDebugUnitTest`:

```text
uz.ttpu.weatherpatterns.ExampleUnitTest > addition_isCorrect PASSED
uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryTest > getWeather_forTashkent_returnsSuccessWithTemperature24 PASSED
uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryTest > getWeather_forAtlantis_returnsFailure PASSED
uz.ttpu.weatherpatterns.mvp.presenter.WeatherPresenterTest > selectCity_knownCity_showsLoadingThenWeather PASSED
uz.ttpu.weatherpatterns.mvp.presenter.WeatherPresenterTest > selectCity_unknownCity_showsLoadingThenError PASSED
uz.ttpu.weatherpatterns.mvp.presenter.WeatherPresenterTest > selectCity_sequentialRequests_completesLatestRequest PASSED
uz.ttpu.weatherpatterns.mvp.presenter.WeatherPresenterTest > detachView_cancelsJobAndPreventsCallbacks PASSED
uz.ttpu.weatherpatterns.mvi.viewmodel.WeatherMviViewModelTest > selectCity_knownCity_endsContentStateWithWeatherAndNoLoading PASSED
uz.ttpu.weatherpatterns.mvi.viewmodel.WeatherMviViewModelTest > selectCity_unknownCity_endsWithErrorState PASSED
uz.ttpu.weatherpatterns.mvi.viewmodel.WeatherMviViewModelTest > dismissError_clearsErrorAndPreservesPreviousWeather PASSED

BUILD SUCCESSFUL in 2s
10 actionable tasks: 10 executed (10 passed)
```

---

## 6. How to Run & Demonstrate the Application

1. **Open Project:** Open `WeatherPatterns` in Android Studio.
2. **Build Debug APK:** Run `Gradle -> :app:assembleDebug` or click **Run 'app'** (`Shift + F10`).
3. **Launch Screen (`MainActivity`):**
   - Click **"1. MVC (Model-View-Controller)"** to open `MvcActivity`.
   - Click **"2. MVP (Model-View-Presenter)"** to open `MvpActivity`.
   - Click **"3. MVVM (Model-View-ViewModel)"** to open `MvvmActivity`.
   - Click **"4. MVI (Model-View-Intent)"** to open `MviActivity`.
4. **Demonstrate Weather Operations:**
   - Tap **Tashkent**: Displays loading status for 3 seconds, then shows `Tashkent: 24°C, Sunny`.
   - Tap **Samarkand**: Shows `Samarkand: 21°C, Cloudy`.
   - Tap **Bukhara**: Shows `Bukhara: 27°C, Windy`.
   - Tap **Atlantis**: Shows `Error: No forecast for Atlantis`.
   - In **MVI**, tap **"Dismiss Error"**: Clears error banner and restores previous Tashkent forecast.
5. **Demonstrate Screen Rotation Behavior:**
   - On **MVVM** or **MVI**, select Tashkent. While loading or after completion, rotate the device. Notice forecast state is preserved without re-fetching!
   - On **MVC** or **MVP**, rotate the device. Notice state resets to initial state because Controller/Presenter instances are recreated with the Activity.

---

## 7. Conclusion

The `WeatherPatterns` laboratory project successfully demonstrates the design, implementation, testing, and comparative analysis of MVC, MVP, MVVM, and MVI architectural design patterns in Android using Kotlin, Jetpack Compose, and Material 3. All core requirements, coroutine safety rules, unit tests, and theoretical questions have been fully verified and completed.
