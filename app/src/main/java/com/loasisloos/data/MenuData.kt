package com.loasisloos.data

data class Category(
    val id: String,
    val name: String,
    val icon: String? = null,
    val items: List<Product>
)

data class Product(
    val id: String,
    val name: String,
    val imageRes: Int? = null, // Placeholder for resource ID
    val basePrice: Double,
    val categoryId: String,
    
    // Feature flags based on menu analysis
    val hasComboOptions: Boolean = false,      // Burger/Panini: Seul vs Frites vs Menu
    val hasMeatSelection: Boolean = false,     // Tacos, Sandwichs, Assiette
    val maxMeats: Int = 0,                     // 1, 2, or 3
    val hasSauceSelection: Boolean = false,
    val hasSupplements: Boolean = false,
    val hasGratinéOptions: Boolean = false,
    val hasSizeOptions: Boolean = false,       // Tacos M/L/XL, Pizza sizes
    val hasBreadOptions: Boolean = false,      // Galette/Naan/Falluche
    val availableSizes: List<ProductSize> = emptyList()
)

data class ProductSize(
    val name: String, // "M", "L", "Junior", "Senior"
    val priceModifier: Double = 0.0
)

data class Supplement(
    val id: String,
    val name: String,
    val price: Double
)
