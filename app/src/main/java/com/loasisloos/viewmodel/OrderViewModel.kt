package com.loasisloos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loasisloos.data.Order
import com.loasisloos.data.OrderItem
import com.loasisloos.data.OrderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class OrderViewModel : ViewModel() {

    private val _currentOrder = MutableStateFlow(Order())
    val currentOrder: StateFlow<Order> = _currentOrder.asStateFlow()

    fun addToCart(item: OrderItem) {
        _currentOrder.update { order ->
            val newItems = order.items + item
            order.copy(
                items = newItems,
                subtotal = calculateSubtotal(newItems),
                total = calculateTotal(newItems)
            )
        }
    }

    fun removeFromCart(itemId: String) {
        _currentOrder.update { order ->
            val newItems = order.items.filter { it.id != itemId }
            order.copy(
                items = newItems,
                subtotal = calculateSubtotal(newItems),
                total = calculateTotal(newItems)
            )
        }
    }

    fun updateCustomerName(name: String) {
        _currentOrder.update { it.copy(customerName = name) }
    }
    
    fun updateOrderType(type: OrderType) {
        _currentOrder.update { it.copy(orderType = type) }
    }

    fun updateNote(note: String) {
        _currentOrder.update { it.copy(customerNote = note) }
    }

    fun clearCart() {
        _currentOrder.value = Order(id = UUID.randomUUID().toString().substring(0, 5).uppercase())
    }

    private fun calculateSubtotal(items: List<OrderItem>): Double {
        return items.sumOf { it.totalPrice }
    }
    
    private fun calculateTotal(items: List<OrderItem>): Double {
        return calculateSubtotal(items) // Add tax logic if needed later
    }
}
