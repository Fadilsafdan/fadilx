package com.example.data.repository

import com.example.data.local.FadixDao
import com.example.data.local.FadixDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CartItemWithProduct
import com.example.data.model.Categories
import com.example.data.model.CustomerProfileEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Random

class FadixRepository(private val dao: FadixDao) {

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        FadixDatabase.seedInitialProducts(dao)
    }

    // Products
    fun getProducts(category: String, searchQuery: String): Flow<List<ProductEntity>> {
        return dao.getAllProducts().combine(
            combine(
                dao.getAllProducts(),
                dao.getAllProducts()
            ) { a, _ -> a }
        ) { allProducts, _ ->
            allProducts.filter { product ->
                val matchesCategory = (category == Categories.ALL || product.category == category)
                val matchesSearch = if (searchQuery.isBlank()) {
                    true
                } else {
                    product.name.contains(searchQuery, ignoreCase = true) ||
                            product.category.contains(searchQuery, ignoreCase = true) ||
                            product.description.contains(searchQuery, ignoreCase = true)
                }
                matchesCategory && matchesSearch
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProductById(id: Long): ProductEntity? = withContext(Dispatchers.IO) {
        dao.getProductById(id)
    }

    suspend fun addProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        dao.deleteProduct(product)
        dao.deleteCartItem(product.id)
    }

    // Cart
    val cartItemsWithProducts: Flow<List<CartItemWithProduct>> =
        combine(dao.getCartItems(), dao.getAllProducts()) { cartItems, products ->
            val productMap = products.associateBy { it.id }
            cartItems.mapNotNull { cartItem ->
                productMap[cartItem.productId]?.let { product ->
                    CartItemWithProduct(product = product, quantity = cartItem.quantity)
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun addToCart(productId: Long, delta: Int = 1) = withContext(Dispatchers.IO) {
        val currentItems = dao.getCartItems().first()
        val existing = currentItems.find { it.productId == productId }
        val newQuantity = (existing?.quantity ?: 0) + delta

        if (newQuantity <= 0) {
            dao.deleteCartItem(productId)
        } else {
            dao.insertOrUpdateCartItem(
                CartItemEntity(productId = productId, quantity = newQuantity)
            )
        }
    }

    suspend fun setItemQuantity(productId: Long, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            dao.deleteCartItem(productId)
        } else {
            dao.insertOrUpdateCartItem(
                CartItemEntity(productId = productId, quantity = quantity)
            )
        }
    }

    suspend fun removeFromCart(productId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCartItem(productId)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        dao.clearCart()
    }

    // Delivery Fee Calculator
    // Casablanca: 0 MAD if subtotal >= 500 MAD, else 20 MAD
    // Outside Casablanca & Periphery: 40 MAD (preserved as specified in prompt)
    fun calculateDeliveryFee(zone: String, subtotal: Double): Double {
        return if (zone.contains("خارج") || zone.contains("ضواحي") || zone.contains("المناطق الخارجية")) {
            40.0
        } else {
            if (subtotal >= 500.0) 0.0 else 20.0
        }
    }

    // Checkout & Orders
    suspend fun placeOrder(
        customerPhone: String,
        grocerName: String,
        shopName: String,
        deliveryAddress: String,
        deliveryZone: String,
        notes: String
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val items = cartItemsWithProducts.first()
        if (items.isEmpty()) {
            return@withContext Result.failure(Exception("السلة فارغة. يرجى إضافة منتجات للطلب."))
        }

        val subtotal = items.sumOf { it.subtotal }
        val deliveryFee = calculateDeliveryFee(deliveryZone, subtotal)
        val totalAmount = subtotal + deliveryFee

        val randomNum = 1000 + Random().nextInt(9000)
        val orderNumber = "FDX-2026-$randomNum"

        val order = OrderEntity(
            orderNumber = orderNumber,
            customerPhone = customerPhone,
            grocerName = grocerName,
            shopName = shopName,
            deliveryAddress = deliveryAddress,
            deliveryZone = deliveryZone,
            deliveryFee = deliveryFee,
            subtotal = subtotal,
            totalAmount = totalAmount,
            notes = notes,
            status = "جديد",
            createdAt = System.currentTimeMillis()
        )

        val orderId = dao.insertOrder(order)
        val orderItems = items.map { item ->
            OrderItemEntity(
                orderId = orderId,
                productId = item.product.id,
                productName = item.product.name,
                packageUnit = item.product.packageUnit,
                unitPrice = item.product.wholesalePrice,
                quantity = item.quantity,
                subtotal = item.subtotal
            )
        }

        dao.insertOrderItems(orderItems)
        // Clear cart strictly after saving order
        dao.clearCart()

        Result.success(order.copy(id = orderId))
    }

    // Orders retrieval
    fun getAllOrdersWithItems(): Flow<List<OrderWithItems>> {
        return combine(dao.getAllOrders(), dao.getAllOrderItems()) { orders, allItems ->
            val itemsByOrder = allItems.groupBy { it.orderId }
            orders.map { order ->
                OrderWithItems(
                    order = order,
                    items = itemsByOrder[order.id] ?: emptyList()
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getCustomerOrdersWithItems(phone: String): Flow<List<OrderWithItems>> {
        return combine(dao.getOrdersByPhone(phone), dao.getAllOrderItems()) { orders, allItems ->
            val itemsByOrder = allItems.groupBy { it.orderId }
            orders.map { order ->
                OrderWithItems(
                    order = order,
                    items = itemsByOrder[order.id] ?: emptyList()
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updateOrderStatus(orderId, status)
    }

    // Customer Profile
    val customerProfile: Flow<CustomerProfileEntity?> = dao.getCustomerProfile()

    suspend fun saveProfile(profile: CustomerProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun logoutCustomer() = withContext(Dispatchers.IO) {
        val current = dao.getCustomerProfile().first()
        if (current != null) {
            dao.insertOrUpdateProfile(current.copy(isLoggedIn = false))
        }
    }
}
