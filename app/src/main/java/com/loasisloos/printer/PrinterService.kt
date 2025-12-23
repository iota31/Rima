package com.loasisloos.printer

import android.content.Context
import com.loasisloos.data.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrinterService(private val context: Context) {

    val manager = PrinterManager(context)
    private val scope = CoroutineScope(Dispatchers.Main)
    private val receiptBuilder = ReceiptBuilder()

    fun printOrder(order: Order) {
        scope.launch {
            // Ensure connected
            // Logic to auto-connect could go here
            if (manager.connectionState.value !is PrinterConnectionState.Connected) {
                manager.discoverAndConnect()
            }
            
            // Build and print
            val commands = receiptBuilder.buildReceipt(order)
            manager.print(commands)
        }
    }
}
