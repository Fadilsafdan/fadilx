package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // مواد غذائية, مشروبات وعصائر, بسكويت وكوكيز, حلويات وسكاكر, شوكولاتة, رقائق ومقرمشات
    val wholesalePrice: Double, // Full Package/Box Price in MAD
    val packageUnit: String, // e.g. "باكية", "كرتون 20 علبة", "صندوق 12 قارورة"
    val unitsPerPackage: Int = 12, // Number of units inside one package/box
    val stock: Int = 100,
    val imageUrl: String = "",
    val description: String = "",
    val isAvailable: Boolean = true
) {
    // Calculated Price per individual unit
    val pricePerUnit: Double
        get() = if (unitsPerPackage > 0) wholesalePrice / unitsPerPackage else wholesalePrice

    // Selling Unit label (Package / Box / Carton)
    val displaySellingUnit: String
        get() = when {
            packageUnit.contains("باكية") -> "باكية"
            packageUnit.contains("صندوق") -> "صندوق"
            packageUnit.contains("حزمة") -> "حزمة"
            packageUnit.contains("كيس") -> "كيس"
            packageUnit.contains("علبة") -> "علبة"
            packageUnit.contains("كانز") -> "كانز"
            else -> "كرتونة"
        }

    val unitTypeLabel: String
        get() = when {
            name.contains("زيت") || name.contains("مياه") || name.contains("عصير") || name.contains("حليب") -> "حبة"
            name.contains("شاي") || name.contains("طماطم") || name.contains("مكرونة") -> "علبة"
            name.contains("بسكويت") || name.contains("كيك") || name.contains("شوكولاتة") -> "قطعة"
            packageUnit.contains("قارورة") -> "قارورة"
            packageUnit.contains("علبة") -> "علبة"
            packageUnit.contains("حبة") -> "حبة"
            packageUnit.contains("كانز") -> "كانز"
            else -> "وحدة"
        }

    // Optional strikethrough original price if on sale
    val originalPrice: Double?
        get() = when {
            name.contains("زيت المائدة") -> 328.00
            name.contains("ستينغ") -> 89.00
            name.contains("شاي أخضر") -> 475.00
            name.contains("سطار") || name.contains("مايونيز") -> 185.00
            wholesalePrice >= 400.0 -> wholesalePrice * 1.08
            wholesalePrice in 100.0..399.0 && (id % 2L == 0L) -> wholesalePrice * 1.06
            else -> null
        }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: Long,
    val quantity: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

data class CartItemWithProduct(
    val product: ProductEntity,
    val quantity: Int
) {
    val subtotal: Double
        get() = product.wholesalePrice * quantity
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String, // e.g. FDX-2026-1042
    val customerPhone: String,
    val grocerName: String,
    val shopName: String,
    val deliveryAddress: String,
    val deliveryZone: String, // "داخل الدار البيضاء" or "ضواحي الدار البيضاء والمناطق الخارجية"
    val deliveryFee: Double,
    val subtotal: Double,
    val totalAmount: Double,
    val notes: String = "",
    val status: String = "جديد", // جديد, قيد التجهيز, في الطريق للتوصيل, تم التسليم, ملغي
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val packageUnit: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)

data class OrderWithItems(
    val order: OrderEntity,
    val items: List<OrderItemEntity>
)

@Entity(tableName = "customer_profile")
data class CustomerProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val phone: String = "",
    val grocerName: String = "",
    val shopName: String = "",
    val deliveryAddress: String = "",
    val deliveryZone: String = "داخل الدار البيضاء",
    val notes: String = "",
    val isLoggedIn: Boolean = false
)

object Categories {
    const val ALL = "جميع المنتجات"
    const val FOOD = "مواد غذائية"
    const val SWEETS_BISCUITS = "حلويات وبسكويت"
    const val DRINKS = "مشروبات"
    const val CANNED = "معلبات"

    const val BISCUITS = "بسكويت وكوكيز"
    const val SWEETS = "حلويات وسكاكر"
    const val CHOCOLATE = "شوكولاتة"
    const val SNACKS = "رقائق ومقرمشات"

    val list = listOf(
        ALL,
        FOOD,
        SWEETS_BISCUITS,
        DRINKS,
        CANNED,
        BISCUITS,
        SWEETS,
        CHOCOLATE,
        SNACKS
    )
}
