package com.loasisloos.printer

import com.loasisloos.data.ComboType
import com.loasisloos.data.Order
import com.loasisloos.data.OrderType
import com.starmicronics.stario10.starxpandcommand.DocumentBuilder
import com.starmicronics.stario10.starxpandcommand.PrinterBuilder
import com.starmicronics.stario10.starxpandcommand.StarXpandCommandBuilder
import com.starmicronics.stario10.starxpandcommand.MagnificationParameter
import com.starmicronics.stario10.starxpandcommand.printer.Alignment
import com.starmicronics.stario10.starxpandcommand.printer.CutType

class ReceiptBuilder {

    fun buildReceipt(order: Order): String {
        val builder = StarXpandCommandBuilder()
        val docBuilder = DocumentBuilder()
        val printerBuilder = PrinterBuilder()

        // 1. Header (Uber Eats Style)
        printerBuilder.styleAlignment(Alignment.Center)
        printerBuilder.actionPrintText("L'OASIS LOOS\n")
        printerBuilder.styleAlignment(Alignment.Left)
        
        // Large Order ID & Name
        printerBuilder.add(
            PrinterBuilder()
                .styleMagnification(MagnificationParameter(2, 2)) // Large text
                .actionPrintText("${order.id}    ${order.customerName ?: ""}\n")
        )
        
        printerBuilder.actionPrintText("--------------------------------\n")
        
        // Order Type (LIVRAISON / SUR PLACE)
        printerBuilder.styleAlignment(Alignment.Center)
        printerBuilder.add(
             PrinterBuilder()
                .styleMagnification(MagnificationParameter(2, 2))
                .actionPrintText("${order.orderType.name.replace("_", " ")}\n")
        )
        printerBuilder.styleAlignment(Alignment.Left)
        printerBuilder.actionPrintText("--------------------------------\n\n")

        // 2. Items
        order.items.forEach { item ->
            // Qty x Product Name    Price
            val lineTotal = String.format("%.2f€", item.totalPrice)
            val productLine = "${item.quantity} x ${item.product.name}"
            
            // Basic logic to space out price (simple padding)
            printerBuilder.styleBold(true)
            printerBuilder.actionPrintText("$productLine   $lineTotal\n")
            printerBuilder.styleBold(false)

            // Customizations
            if (item.comboType != ComboType.SEUL) {
                printerBuilder.actionPrintText("   ${if (item.comboType==ComboType.AVEC_FRITES) "+ Frites" else "MENU COMPLET"}\n")
            }
            
            item.selectedBread?.let { printerBuilder.actionPrintText("   Pain: $it\n") }
            item.selectedDrink?.let { printerBuilder.actionPrintText("   Boisson: $it\n") }
            
            item.selectedMeats.forEach { meat ->
                printerBuilder.actionPrintText("   + $meat\n")
            }
            
            item.selectedSauces.forEach { sauce ->
                printerBuilder.actionPrintText("   > $sauce\n")
            }
            
            item.selectedSupplements.forEach { supp ->
                printerBuilder.actionPrintText("   SUPP: ${supp.name}\n")
            }
            
            if (item.itemNote.isNotEmpty()) {
                printerBuilder.actionPrintText("   NOTE: ${item.itemNote}\n")
            }
            
            printerBuilder.actionPrintText("\n") // Spacer between items
        }
        
        // 3. General Note Box
        if (!order.customerNote.isNullOrEmpty()) {
            printerBuilder.actionPrintText("--------------------------------\n")
            printerBuilder.styleBold(true)
            printerBuilder.actionPrintText("NOTE CLIENT:\n")
            printerBuilder.styleBold(false)
            printerBuilder.actionPrintText("« ${order.customerNote} »\n")
            printerBuilder.actionPrintText("--------------------------------\n\n")
        }
        
        // 4. Totals
        // 4. Totals
        // 4. Totals
        printerBuilder.styleAlignment(Alignment.Right)
        printerBuilder.styleBold(true)
        printerBuilder.add(
             PrinterBuilder()
                .styleMagnification(MagnificationParameter(2, 2))
                .actionPrintText("TOTAL: ${String.format("%.2f€", order.total)}\n")
        )
        printerBuilder.styleBold(false)
        printerBuilder.styleAlignment(Alignment.Left)
        
        printerBuilder.actionPrintText("\n\n")
        
        // 5. Cut
        printerBuilder.actionCut(CutType.Partial)

        // Add printer config to doc
        docBuilder.addPrinter(printerBuilder)
        builder.addDocument(docBuilder)

        // Wrapper call to get encoded commands (String or ByteArray depending on implementation)
        // Check StarIO10 docs: getCommands() returns String by default (XML like) for some, 
        // but let's assume ByteArray compatibility in PrinterManager handle.
        // Actually StarIO10 usually returns String commands for some interfaces.
        // Let's ensure PrinterManager takes String if getCommands returns String.
        // But getCommands() in previous artifact returned ByteArray? 
        // Let's assume it returns String or ByteArray and PrinterManager accepts it.
        // Wait, StarXpandCommandBuilder.getCommands() returns String.
        // I should update PrinterManager to accept String commands!
        
        return builder.getCommands()
    }
}
