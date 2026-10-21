package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FadixDatabase
import com.example.data.model.CartItemWithProduct
import com.example.data.model.Categories
import com.example.data.model.CustomerProfileEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.ProductEntity
import com.example.data.repository.FadixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val titleArabic: String) {
    object Splash : Screen("البداية")
    object Home : Screen("الرئيسية")
    object CategoriesList : Screen("الأقسام")
    object Favorites : Screen("المفضلة")
    object Offers : Screen("العروض")
    data class CategoryProducts(val categoryName: String, val subCategory: String = "الكل") : Screen(categoryName)
    data class ProductDetail(val productId: Long) : Screen("تفاصيل المنتج")
    object Cart : Screen("سلة المشتريات")
    object Checkout : Screen("إتمام الطلب")
    object Orders : Screen("طلباتي")
    object Account : Screen("حسابي")
    object Login : Screen("تسجيل الدخول")
    object Admin : Screen("لوحة الإدارة")
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val id: Long = System.currentTimeMillis()
)

class FadixViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FadixDatabase.getDatabase(application)
    private val repository = FadixRepository(database.fadixDao())

    // Current navigation destination
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen navigation stack for proper Back handling
    private val navigationBackStack = mutableListOf<Screen>(Screen.Home)

    // Category and Search query
    private val _selectedCategory = MutableStateFlow(Categories.ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Products Flow
    val products: StateFlow<List<ProductEntity>> =
        combine(_selectedCategory, _searchQuery) { cat, q ->
            Pair(cat, q)
        }.combine(database.fadixDao().getAllProducts()) { (cat, query), allList ->
            allList.filter { product ->
                val matchesCategory = when (cat) {
                    Categories.ALL -> true
                    Categories.FOOD -> product.category == Categories.FOOD
                    Categories.SWEETS_BISCUITS -> product.category == Categories.SWEETS_BISCUITS ||
                            product.category == Categories.BISCUITS ||
                            product.category == Categories.SWEETS ||
                            product.category == Categories.CHOCOLATE
                    Categories.DRINKS -> product.category == Categories.DRINKS || product.category.contains("مشروب")
                    Categories.CANNED -> product.category == Categories.CANNED ||
                            product.name.contains("طماطم") ||
                            product.name.contains("معلب") ||
                            product.name.contains("صلصة")
                    else -> product.category == cat
                }
                val matchesSearch = if (query.isBlank()) {
                    true
                } else {
                    product.name.contains(query, ignoreCase = true) ||
                            product.category.contains(query, ignoreCase = true) ||
                            product.packageUnit.contains(query, ignoreCase = true)
                }
                matchesCategory && matchesSearch
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist / Favorites Flow
    private val _favoriteProductIds = MutableStateFlow<Set<Long>>(setOf(1L, 2L))
    val favoriteProductIds: StateFlow<Set<Long>> = _favoriteProductIds.asStateFlow()

    val favoriteProducts: StateFlow<List<ProductEntity>> =
        combine(_favoriteProductIds, database.fadixDao().getAllProducts()) { favIds, allProds ->
            allProds.filter { favIds.contains(it.id) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(productId: Long) {
        val current = _favoriteProductIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
            showNotification("تمت إزالة المنتج من المفضلة")
        } else {
            current.add(productId)
            showNotification("تمت إضافة المنتج إلى المفضلة ❤️")
        }
        _favoriteProductIds.value = current
    }

    // Cart items
    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItemsWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart counts mapping: ProductId -> Quantity
    val cartQuantities: StateFlow<Map<Long, Int>> = repository.cartItemsWithProducts
        .combine(MutableStateFlow(Unit)) { items, _ ->
            items.associate { it.product.id to it.quantity }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val totalCartCount: StateFlow<Int> = cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Delivery Zone selection
    private val _selectedDeliveryZone = MutableStateFlow("داخل الدار البيضاء")
    val selectedDeliveryZone: StateFlow<String> = _selectedDeliveryZone.asStateFlow()

    val deliveryFee: StateFlow<Double> =
        combine(_selectedDeliveryZone, cartSubtotal) { zone, subtotal ->
            repository.calculateDeliveryFee(zone, subtotal)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val orderTotalAmount: StateFlow<Double> =
        combine(cartSubtotal, deliveryFee) { subtotal, fee ->
            subtotal + fee
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Customer Profile
    val customerProfile: StateFlow<CustomerProfileEntity?> = repository.customerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Orders
    val allOrdersWithItems: StateFlow<List<OrderWithItems>> = repository.getAllOrdersWithItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerOrders: StateFlow<List<OrderWithItems>> =
        combine(customerProfile, repository.getAllOrdersWithItems()) { profile, allOrders ->
            if (profile != null && profile.phone.isNotBlank()) {
                allOrders.filter { it.order.customerPhone == profile.phone }
            } else {
                allOrders
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin State
    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    // Temporary UI notification (Toast/Snackbar)
    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    // Order Success Dialog State
    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navigationBackStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBackPress(): Boolean {
        if (navigationBackStack.size > 1) {
            navigationBackStack.removeAt(navigationBackStack.size - 1)
            _currentScreen.value = navigationBackStack.last()
            return true
        }
        return false
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun setDeliveryZone(zone: String) {
        _selectedDeliveryZone.value = zone
    }

    // Cart Operations
    fun addToCart(productId: Long, delta: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, delta)
            showNotification(if (delta > 0) "تمت إضافة المنتج إلى السلة" else "تم إنقاص الكمية")
        }
    }

    fun setItemQuantity(productId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.setItemQuantity(productId, quantity)
            if (quantity > 0) {
                showNotification("تم تحديث السلة ($quantity عبوة)")
            } else {
                showNotification("تم حذف المنتج من السلة")
            }
        }
    }

    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showNotification("تم حذف المنتج من السلة")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            showNotification("تم إفراغ السلة بالكامل")
        }
    }

    // Checkout
    fun placeOrder(
        grocerName: String,
        shopName: String,
        phone: String,
        address: String,
        zone: String,
        notes: String
    ) {
        if (_isPlacingOrder.value) return // Prevent duplicate clicks

        if (phone.isBlank() || phone.length < 9) {
            showNotification("يرجى إدخال رقم هاتف صحيح للتواصل", isError = true)
            return
        }
        if (shopName.isBlank()) {
            showNotification("يرجى إدخال اسم المحل أو البقالة", isError = true)
            return
        }
        if (address.isBlank()) {
            showNotification("يرجى إدخال عنوان التوصيل بالتفصيل", isError = true)
            return
        }

        viewModelScope.launch {
            _isPlacingOrder.value = true
            try {
                val result = repository.placeOrder(
                    customerPhone = phone.trim(),
                    grocerName = grocerName.trim().ifBlank { "التاجر" },
                    shopName = shopName.trim(),
                    deliveryAddress = address.trim(),
                    deliveryZone = zone,
                    notes = notes.trim()
                )
                result.onSuccess { order ->
                    _lastPlacedOrder.value = order
                    // Also update customer profile so next time their info is ready
                    repository.saveProfile(
                        CustomerProfileEntity(
                            id = 1,
                            phone = phone.trim(),
                            grocerName = grocerName.trim(),
                            shopName = shopName.trim(),
                            deliveryAddress = address.trim(),
                            deliveryZone = zone,
                            notes = notes.trim(),
                            isLoggedIn = true
                        )
                    )
                    showNotification("تم تأكيد طلبكم بنجاح برقم: ${order.orderNumber}")
                }.onFailure { err ->
                    showNotification(err.message ?: "فشل في حفظ الطلب، يرجى المحاولة ثانية", isError = true)
                }
            } finally {
                _isPlacingOrder.value = false
            }
        }
    }

    fun dismissOrderDialog() {
        _lastPlacedOrder.value = null
        navigateTo(Screen.Orders)
    }

    // Customer Authentication & Profile
    fun authenticatePhone(
        phone: String,
        grocerName: String,
        shopName: String,
        address: String,
        zone: String
    ) {
        viewModelScope.launch {
            repository.saveProfile(
                CustomerProfileEntity(
                    id = 1,
                    phone = phone.trim(),
                    grocerName = grocerName.trim(),
                    shopName = shopName.trim(),
                    deliveryAddress = address.trim(),
                    deliveryZone = zone,
                    isLoggedIn = true
                )
            )
            showNotification("تم تسجيل الدخول بنجاح! مرحباً بك في فاديكس FADIX")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutCustomer()
            showNotification("تم تسجيل الخروج")
        }
    }

    // Admin Dashboard operations
    fun unlockAdmin(pin: String): Boolean {
        return if (pin == "1234" || pin == "0000") {
            _isAdminUnlocked.value = true
            showNotification("تم الدخول إلى لوحة إدارة FADIX")
            true
        } else {
            showNotification("رمز المرور غير صحيح", isError = true)
            false
        }
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
    }

    fun addProduct(
        name: String,
        category: String,
        price: Double,
        packageUnit: String,
        unitsPerPackage: Int = 12,
        stock: Int,
        description: String
    ) {
        viewModelScope.launch {
            val newProduct = ProductEntity(
                name = name.trim(),
                category = category,
                wholesalePrice = price,
                packageUnit = packageUnit.trim(),
                unitsPerPackage = if (unitsPerPackage > 0) unitsPerPackage else 12,
                stock = stock,
                description = description.trim(),
                isAvailable = true
            )
            repository.addProduct(newProduct)
            showNotification("تمت إضافة المنتج الجديد بنجاح")
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
            showNotification("تم تحديث بيانات المنتج")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            showNotification("تم حذف المنتج من المخزون")
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            showNotification("تم تحديث حالة الطلب إلى: $status")
        }
    }

    fun showNotification(message: String, isError: Boolean = false) {
        _notification.value = UiNotification(message = message, isError = isError)
    }

    fun clearNotification() {
        _notification.value = null
    }
}
