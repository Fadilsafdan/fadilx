package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.FadixRepository
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("XFADI", appName)
  }

  @Test
  fun `verify Casablanca delivery rules and outside Casablanca 40 MAD fee`() {
    val repo = FadixRepository(object : com.example.data.local.FadixDao {
      override fun getAllProducts() = emptyFlow<List<com.example.data.model.ProductEntity>>()
      override suspend fun getAllProductsList() = emptyList<com.example.data.model.ProductEntity>()
      override fun getProductsByCategory(category: String) = emptyFlow<List<com.example.data.model.ProductEntity>>()
      override fun searchProducts(query: String) = emptyFlow<List<com.example.data.model.ProductEntity>>()
      override suspend fun getProductById(id: Long) = null
      override suspend fun getProductCount() = 0
      override suspend fun insertProduct(product: com.example.data.model.ProductEntity) = 0L
      override suspend fun insertProducts(products: List<com.example.data.model.ProductEntity>) {}
      override suspend fun updateProduct(product: com.example.data.model.ProductEntity) {}
      override suspend fun deleteProduct(product: com.example.data.model.ProductEntity) {}
      override fun getCartItems() = emptyFlow<List<com.example.data.model.CartItemEntity>>()
      override suspend fun insertOrUpdateCartItem(item: com.example.data.model.CartItemEntity) {}
      override suspend fun deleteCartItem(productId: Long) {}
      override suspend fun clearCart() {}
      override fun getAllOrders() = emptyFlow<List<com.example.data.model.OrderEntity>>()
      override fun getOrdersByPhone(phone: String) = emptyFlow<List<com.example.data.model.OrderEntity>>()
      override suspend fun getOrderById(orderId: Long) = null
      override suspend fun getOrderItems(orderId: Long) = emptyList<com.example.data.model.OrderItemEntity>()
      override fun getOrderItemsFlow(orderId: Long) = emptyFlow<List<com.example.data.model.OrderItemEntity>>()
      override fun getAllOrderItems() = emptyFlow<List<com.example.data.model.OrderItemEntity>>()
      override suspend fun insertOrder(order: com.example.data.model.OrderEntity) = 1L
      override suspend fun insertOrderItems(items: List<com.example.data.model.OrderItemEntity>) {}
      override suspend fun updateOrderStatus(orderId: Long, status: String) {}
      override fun getCustomerProfile() = emptyFlow<com.example.data.model.CustomerProfileEntity?>()
      override suspend fun insertOrUpdateProfile(profile: com.example.data.model.CustomerProfileEntity) {}
    })

    // 1. Inside Casablanca with >= 500 MAD should be free (0.0 MAD)
    assertEquals(0.0, repo.calculateDeliveryFee("داخل الدار البيضاء", 600.0), 0.01)

    // 2. Inside Casablanca with < 500 MAD should be 20.0 MAD
    assertEquals(20.0, repo.calculateDeliveryFee("داخل الدار البيضاء", 350.0), 0.01)

    // 3. Outside Casablanca / Periphery should strictly be 40.0 MAD
    assertEquals(40.0, repo.calculateDeliveryFee("ضواحي الدار البيضاء والمناطق الخارجية", 1200.0), 0.01)
  }

  @Test
  fun `verify wholesale package price and unit calculation`() {
    val sting = com.example.data.model.ProductEntity(
      id = 1L,
      name = "ستينغ بوري بلاست بلاستيك 25 سل",
      category = com.example.data.model.Categories.DRINKS,
      wholesalePrice = 81.0,
      packageUnit = "باكية 18 قارورة",
      unitsPerPackage = 18
    )

    // Full Package Price: 81.0 MAD
    assertEquals(81.0, sting.wholesalePrice, 0.001)

    // Units per package: 18
    assertEquals(18, sting.unitsPerPackage)

    // Unit price: 81.0 / 18 = 4.50 MAD
    assertEquals(4.50, sting.pricePerUnit, 0.001)

    // Display selling unit badge: "باكية"
    assertEquals("باكية", sting.displaySellingUnit)

    // Cart total calculation: 2 packages = 162.00 MAD
    val cartItem = com.example.data.model.CartItemWithProduct(sting, quantity = 2)
    assertEquals(162.00, cartItem.subtotal, 0.001)
  }

  @Test
  fun `verify tea and oil wholesale card details and strikethrough price`() {
    val tea = com.example.data.model.ProductEntity(
      id = 101L,
      name = "شاي أخضر طبيعي",
      category = com.example.data.model.Categories.FOOD,
      wholesalePrice = 450.0,
      packageUnit = "كرتونة",
      unitsPerPackage = 24
    )
    assertEquals(450.0, tea.wholesalePrice, 0.001)
    assertEquals(24, tea.unitsPerPackage)
    assertEquals(18.75, tea.pricePerUnit, 0.001)
    assertEquals("كرتونة", tea.displaySellingUnit)
    assertEquals("علبة", tea.unitTypeLabel)

    val oil = com.example.data.model.ProductEntity(
      id = 102L,
      name = "زيت المائدة (1 لتر)",
      category = com.example.data.model.Categories.FOOD,
      wholesalePrice = 320.0,
      packageUnit = "كرتونة",
      unitsPerPackage = 12
    )
    assertEquals(320.0, oil.wholesalePrice, 0.001)
    assertEquals(12, oil.unitsPerPackage)
    assertEquals(26.666, oil.pricePerUnit, 0.01)
    assertEquals(328.0, oil.originalPrice ?: 0.0, 0.01)
    assertEquals("حبة", oil.unitTypeLabel)

    val mayo = com.example.data.model.ProductEntity(
      id = 103L,
      name = "سطار - مايونيز 37 سنتيلتر",
      category = com.example.data.model.Categories.FOOD,
      wholesalePrice = 170.13,
      packageUnit = "كرتونة",
      unitsPerPackage = 12
    )
    assertEquals(170.13, mayo.wholesalePrice, 0.001)
    assertEquals(12, mayo.unitsPerPackage)
    assertEquals(14.1775, mayo.pricePerUnit, 0.01)
    assertEquals(185.0, mayo.originalPrice ?: 0.0, 0.01)
    assertEquals("وحدة", mayo.unitTypeLabel)
  }
}
