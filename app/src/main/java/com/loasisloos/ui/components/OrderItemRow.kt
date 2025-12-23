package com.loasisloos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.loasisloos.data.ComboType
import com.loasisloos.data.OrderItem
import com.loasisloos.ui.theme.RedPrimary

@Composable
fun OrderItemRow(
    item: OrderItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Quantity x Product Name
                Text(
                    text = "${item.quantity}x ${item.product.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Combo info
                if (item.comboType != ComboType.SEUL) {
                    Text(
                        text = if (item.comboType == ComboType.AVEC_FRITES) "+ Frites" else "Menu Complet",
                        color = RedPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Customizations listing
                val details = buildString {
                    item.selectedBread?.let { appendLine("Pain: $it") }
                    if (item.selectedMeats.isNotEmpty()) appendLine("Viandes: ${item.selectedMeats.joinToString()}")
                    if (item.selectedSauces.isNotEmpty()) appendLine("Sauces: ${item.selectedSauces.joinToString()}")
                    if (item.selectedSupplements.isNotEmpty()) appendLine("Supp.: ${item.selectedSupplements.joinToString{it.name}}")
                    item.selectedDrink?.let { appendLine("Boisson: $it") }
                    if (item.itemNote.isNotEmpty()) appendLine("Note: ${item.itemNote}")
                }
                
                if (details.isNotEmpty()) {
                    Text(
                        text = details.trim(),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.2f€", item.totalPrice),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, "Supprimer", tint = Color.Gray)
                }
            }
        }
    }
}
