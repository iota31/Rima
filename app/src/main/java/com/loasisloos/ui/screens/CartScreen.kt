package com.loasisloos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.PrintDisabled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.loasisloos.data.OrderType
import com.loasisloos.ui.components.OrderItemRow
import com.loasisloos.ui.components.RadioSelector
import com.loasisloos.ui.theme.RedPrimary
import com.loasisloos.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    orderViewModel: OrderViewModel = viewModel()
) {
    val order by orderViewModel.currentOrder.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val printerService = remember { com.loasisloos.printer.PrinterService(context) }
    val printerState by printerService.manager.connectionState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panier", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Retour", tint = Color.White) }
                },
                actions = {
                    // Printer Status Indicator
                    val (icon, color) = when(printerState) {
                        is com.loasisloos.printer.PrinterConnectionState.Connected -> Pair(Icons.Default.Print, Color.Green)
                        is com.loasisloos.printer.PrinterConnectionState.Scanning,
                        is com.loasisloos.printer.PrinterConnectionState.Connecting -> Pair(Icons.Default.Print, Color.Yellow)
                        else -> Pair(Icons.Default.PrintDisabled, Color.Red)
                    }
                    
                    if (printerState is com.loasisloos.printer.PrinterConnectionState.Error) {
                        val errorMsg = (printerState as com.loasisloos.printer.PrinterConnectionState.Error).message
                        AlertDialog(
                            onDismissRequest = { printerService.manager.disconnect() },
                            title = { Text("Erreur Imprimante") },
                            text = {
                                Column {
                                    Text(errorMsg, fontWeight = FontWeight.Bold, color = Color.Red)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Dépannage :", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("1. Vérifiez que l'imprimante est allumée.")
                                    Text("2. Vérifiez que le Bluetooth est activé.")
                                    Text("3. Vérifiez qu'il y a du papier.")
                                    Text("4. Redémarrez l'imprimante.")
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { printerService.manager.disconnect() }) {
                                    Text("OK")
                                }
                            },
                            containerColor = Color.White,
                            titleContentColor = Color.Black,
                            textContentColor = Color.Black
                        )
                    }

                    Icon(icon, contentDescription = "Printer Status", tint = color, modifier = Modifier.padding(end = 16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
             Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1E1E))) {
                 // Order Type
                 Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                     FilterChip(
                         selected = order.orderType == OrderType.SUR_PLACE,
                         onClick = { orderViewModel.updateOrderType(OrderType.SUR_PLACE) },
                         label = { Text("SUR PLACE") },
                         colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary, labelColor = Color.White)
                     )
                     FilterChip(
                         selected = order.orderType == OrderType.A_EMPORTER,
                         onClick = { orderViewModel.updateOrderType(OrderType.A_EMPORTER) },
                         label = { Text("À EMPORTER") },
                         colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary, labelColor = Color.White)
                     )
                 }
                 
                 Divider(color = Color.DarkGray)
                 
                 Row(
                     Modifier.fillMaxWidth().padding(16.dp),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Text("Total", style = MaterialTheme.typography.titleLarge, color = Color.White)
                     Text(String.format("%.2f€", order.total), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = RedPrimary)
                 }
                 
                 Button(
                     onClick = { printerService.printOrder(order) },
                     modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                     colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                     enabled = order.items.isNotEmpty()
                 ) {
                     Text(
                         if (printerState is com.loasisloos.printer.PrinterConnectionState.Scanning) "CONNEXION..." else "IMPRIMER COMMANDE",
                         fontWeight = FontWeight.Bold
                     )
                 }
             }
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Info
            item {
                OutlinedTextField(
                    value = order.customerName ?: "",
                    onValueChange = { orderViewModel.updateCustomerName(it) },
                    label = { Text("Nom du Client / Table") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = RedPrimary
                    )
                )
            }
            
            // Items
            items(order.items) { item ->
                OrderItemRow(
                    item = item,
                    onRemove = { orderViewModel.removeFromCart(item.id) }
                )
            }

            // General Note
            item {
                OutlinedTextField(
                    value = order.customerNote ?: "",
                    onValueChange = { orderViewModel.updateNote(it) },
                    label = { Text("Note générale pour la cuisine") },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                     colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = RedPrimary
                    )
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
