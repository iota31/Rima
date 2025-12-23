package com.loasisloos.data

import java.time.LocalDateTime
import java.util.UUID

enum class OrderType {
    SUR_PLACE,
    A_EMPORTER,
    LIVRAISON
}

enum class ComboType {
    SEUL,
    AVEC_FRITES,
    MENU_COMPLET // + Frites + Boisson
}

data class Order(
    val id: String = UUID.randomUUID().toString().substring(0, 5).uppercase(),
    val customerName: String = "",
    val orderType: OrderType = OrderType.SUR_PLACE,
    val tableNumber: String? = null,
    val items: List<OrderItem> = emptyList(),
    val customerNote: String = "",
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isPaid: Boolean = false
)

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product,
    val quantity: Int = 1,
    
    // Selections
    val comboType: ComboType = ComboType.SEUL,
    val selectedSize: ProductSize? = null,
    val selectedBread: String? = null,
    val selectedMeats: List<String> = emptyList(),
    val selectedSauces: List<String> = emptyList(),
    val selectedSupplements: List<Supplement> = emptyList(),
    val selectedDrink: String? = null,
    val isGratinė: Boolean = false,
    
    val itemNote: String = "", // Specific instruction for this item
    val totalPrice: Double = 0.0
)
