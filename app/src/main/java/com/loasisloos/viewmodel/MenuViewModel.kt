package com.loasisloos.viewmodel

import androidx.lifecycle.ViewModel
import com.loasisloos.data.Category
import com.loasisloos.data.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    // We'll track the cart count here later
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        _categories.value = MenuRepository.categories
    }
}
