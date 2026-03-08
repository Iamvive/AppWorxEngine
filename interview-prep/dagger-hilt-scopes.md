# Dagger Hilt: Scopes and Components

## Question: Why use `SingletonComponent` instead of `ViewModelComponent` for a Repository?

In Dagger Hilt, components dictate the **lifespan** of the dependencies they provide. 

### The Difference
1. **`SingletonComponent`**: Tied to the Android `Application` class. Dependencies live from app launch until the app is killed by the OS.
2. **`ViewModelComponent`**: Tied to a specific `ViewModel`. Dependencies are created when the ViewModel is created and destroyed when the ViewModel is cleared (e.g., when the user navigates away from the screen).

### Why Repositories belong in `SingletonComponent`
It is an architectural best practice to scope Repositories to the `SingletonComponent` for three main reasons:

1. **Caching and State:** Repositories often act as the "Single Source of Truth." If a Repository is scoped to a `ViewModel`, any in-memory caching is instantly destroyed the moment the user leaves the screen. Scoping it to the Application allows the cache to persist, saving bandwidth and making the app feel faster upon returning.
2. **Sharing Data:** If `ProductListScreen` and `ProductDetailScreen` both need the `ProductRepository`, scoping it to `SingletonComponent` ensures both ViewModels receive the *exact same instance* of the repository, allowing them to easily share data.
3. **Expensive Instantiation:** Repositories depend on heavy objects (like `Retrofit` clients or `Room` databases). You do not want to constantly rebuild a Retrofit client every time the user opens a new screen.

### Handling "Logged In" vs "Logged Out" State
Hilt does not have a built-in "User Session" component because a session is business logic, not an OS lifecycle. To handle this, the pragmatic approach is to keep the Repository in `SingletonComponent` but inject a `SessionManager`. On logout, the SessionManager triggers the Repositories to clear their internal caches/databases, rather than trying to destroy the Repository object itself.