package com.loasisloos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.loasisloos.data.*
import com.loasisloos.ui.components.MultiSelector
import com.loasisloos.ui.components.RadioSelector
import com.loasisloos.ui.theme.RedPrimary
import com.loasisloos.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    orderViewModel: OrderViewModel = viewModel()
) {
    val product = MenuRepository.categories.flatMap { it.items }.find { it.id == productId }

    if (product == null) {
        Text("Produit introuvable", color = Color.White)
        return
    }

    // --- State ---
    var quantity by remember { mutableStateOf(1) }
    var comboType by remember { mutableStateOf(ComboType.SEUL) }
    var selectedBread by remember { mutableStateOf<String?>(null) }
    var selectedMeats by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSauces by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSupplements by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedDrink by remember { mutableStateOf<String?>(null) }
    var itemNote by remember { mutableStateOf("") }

    // --- Price Calculation ---
    val totalPrice = remember(quantity, comboType, selectedSupplements) {
        var price = product.basePrice
        // Add combo price
        if (comboType == ComboType.AVEC_FRITES) price += 1.5 // approximate
        if (comboType == ComboType.MENU_COMPLET) price += 2.5 // approximate
        
        // Add supplements
        val supplementObjects = MenuRepository.getSupplements().filter { selectedSupplements.contains(it.name) }
        price += supplementObjects.sumOf { it.price }
        
        price * quantity
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Retour", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val orderItem = OrderItem(
                         product = product,
                         quantity = quantity,
                         comboType = comboType,
                         selectedBread = selectedBread,
                         selectedMeats = selectedMeats,
                         selectedSauces = selectedSauces,
                         selectedSupplements = MenuRepository.getSupplements().filter { selectedSupplements.contains(it.name) },
                         selectedDrink = selectedDrink,
                         itemNote = itemNote,
                         totalPrice = totalPrice
                    )
                    orderViewModel.addToCart(orderItem)
                    onAddToCart()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Ajouter au panier - ${String.format("%.2f", totalPrice)}€", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Combo Options
            if (product.hasComboOptions) {
                RadioSelector(
                    title = "Formule",
                    options = listOf("Seul", "+ Frites", "Menu (+ Frites + Boisson)"),
                    selectedOption = when(comboType) {
                        ComboType.SEUL -> "Seul"
                        ComboType.AVEC_FRITES -> "+ Frites"
                        ComboType.MENU_COMPLET -> "Menu (+ Frites + Boisson)"
                    },
                    onOptionSelected = { 
                        comboType = when(it) {
                            "Seul" -> ComboType.SEUL
                            "+ Frites" -> ComboType.AVEC_FRITES
                            else -> ComboType.MENU_COMPLET
                        }
                    }
                )
            }

            // Bread / Galette
            if (product.hasBreadOptions) {
                RadioSelector(
                    title = "Pain",
                    options = MenuRepository.getBreads(),
                    selectedOption = selectedBread,
                    onOptionSelected = { selectedBread = it }
                )
            }

            // Meats
            if (product.hasMeatSelection) {
                MultiSelector(
                    title = "Viandes",
                    options = MenuRepository.getMeats(),
                    selectedOptions = selectedMeats,
                    onOptionToggle = { 
                        if (selectedMeats.contains(it)) {
                            selectedMeats = selectedMeats - it
                        } else if (selectedMeats.size < product.maxMeats) {
                            selectedMeats = selectedMeats + it
                        }
                    },
                    maxSelection = product.maxMeats
                )
            }

            // Sauces
            if (product.hasSauceSelection || product.hasComboOptions) { // Usually sauce allowed for burgers too
                MultiSelector(
                    title = "Sauces",
                    options = MenuRepository.getSauces(),
                    selectedOptions = selectedSauces,
                    onOptionToggle = { 
                         if (selectedSauces.contains(it)) selectedSauces = selectedSauces - it
                         else selectedSauces = selectedSauces + it
                    }
                )
            }

            // Supplements
            if (product.hasSupplements) {
                MultiSelector(
                    title = "Suppléments",
                    options = MenuRepository.getSupplements().map { it.name + " (+${it.price}€)" },
                    selectedOptions = selectedSupplements.map { it + " (+${MenuRepository.getSupplements().find{ s -> s.name == it.split(" (+")[0] }?.price}€)" }, // Hacky mapping back and forth, simplified for now
                    onOptionToggle = { selectedLabel ->
                        val name = selectedLabel.split(" (+")[0]
                        if (selectedSupplements.contains(name)) selectedSupplements = selectedSupplements - name
                        else selectedSupplements = selectedSupplements + name
                    }
                )
            }
            
            // Drink selection if Menu
            if (comboType == ComboType.MENU_COMPLET) {
                 RadioSelector(
                    title = "Boisson",
                    options = listOf("Coca-Cola", "Coca Zéro", "Fanta", "Sprite", "Ice Tea", "Eau"),
                    selectedOption = selectedDrink,
                    onOptionSelected = { selectedDrink = it }
                )
            }

            // Note
            OutlinedTextField(
                value = itemNote,
                onValueChange = { itemNote = it },
                label = { Text("Remarque spéciale") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = RedPrimary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
        }
    }
}
