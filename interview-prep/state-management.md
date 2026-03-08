# Architecture: Imperative vs. Reactive State

## Question: Is it a good approach to use `var isFetching` and `var currentSkip` in a ViewModel?

Using standalone `var` properties alongside a `StateFlow` is a pragmatic approach, but it breaks the core principle of **Unidirectional Data Flow (MVI)**.

### The Problem (Imperative State)
```kotlin
// Bad: Fragmented State
private var currentSkip = 0
private var isFetching = false
private val _uiState = MutableStateFlow<UiState>(...)
```
In this setup, the "Truth" of the screen is fragmented. The UI knows about the `products` list, but only the ViewModel secretly knows about `currentSkip`. As the screen gets more complex (adding pull-to-refresh, sorting, filtering), these standalone variables easily get out of sync with the main `StateFlow`, leading to bizarre bugs.

### The Solution (Pure Reactive State)
To build a highly scalable ViewModel, we must enforce a **Single Source of Truth**.

1. **Move data into the State:** The immutable `UiState` data class should remember everything about itself, including its pagination position.
2. **Use Coroutine Jobs for Locking:** Instead of manually flipping boolean flags (`isFetching = true/false`), utilize Kotlin's built-in `Job` tracking.

```kotlin
// Good: Pure Reactive State
data class Success(
    val products: List<Product>,
    val currentSkip: Int = 0 // Stored immutably in the state
) : UiState()

class ProductViewModel : ViewModel() {
    private var fetchJob: Job? = null // Job tracker

    fun loadNextPage() {
        // Concurrency lock: ignore spam clicks if already running
        if (fetchJob?.isActive == true) return 

        val currentState = _uiState.value as? Success ?: return
        val nextSkip = currentState.currentSkip + 20
        
        fetchJob = viewModelScope.launch {
            val newData = repository.getProducts(skip = nextSkip)
            _uiState.value = Success(
                products = currentState.products + newData,
                currentSkip = nextSkip // Update state
            )
        }
    }
}
```
This guarantees that the ViewModel and the UI are always in perfect sync, and it leverages the native concurrency tools provided by Kotlin Coroutines.