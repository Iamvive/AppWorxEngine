# Jetpack Compose: Recomposition & State Persistence

## Question 1: How does appending items to a list impact Recomposition in a `LazyColumn`?

When you append items to a `List` stored in a `StateFlow`, it triggers a recomposition. However, `LazyColumn` is optimized to only draw what is visible on the screen. 

**The Performance Trap:** 
If you use `items(list)` or `itemsIndexed(list)` without providing a `key`, Compose might get confused about which items are new and which are old when the list size changes. To be safe, it might recompose *every visible item* on the screen, causing dropped frames or stuttering.

**The Fix:**
Always provide a stable, unique `key` (like a database ID) to the `LazyColumn`.
```kotlin
itemsIndexed(
    items = state.products,
    key = { _, product -> product.id } // The performance optimization
) { index, product ->
    ProductItem(product)
}
```
By providing the ID, Compose can flawlessly diff the list. It will reuse the existing Composable nodes for the old items and *only* execute the Composable function for the newly appearing items.

---

## Question 2: Will `var state by remember { mutableStateOf(...) }` survive configuration changes?

**No.** 
`remember` stores the state within the Composition tree. If the user rotates their screen, changes the system theme, or changes the font size, the Android OS destroys the underlying `Activity` and recreates it. The Composition tree is thrown away, and your state is lost (resetting to the default value).

**The Fix:**
Use `rememberSaveable`. 
```kotlin
var showSortMenu by rememberSaveable { mutableStateOf(false) }
```
`rememberSaveable` works exactly like `remember`, but it automatically hooks into the Android OS's `Bundle` mechanism (the modern equivalent of `onSaveInstanceState`). When the screen rotates, it serializes the value into the Bundle, the Activity restarts, and Compose restores the value perfectly.