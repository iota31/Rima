package com.loasisloos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.loasisloos.data.MenuRepository
import com.loasisloos.ui.components.ProductCard
import com.loasisloos.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    categoryId: String,
    onProductClick: (String, String) -> Unit, // productId, categoryId
    onBackClick: () -> Unit
) {
    val category = remember(categoryId) {
        MenuRepository.categories.find { it.id == categoryId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        category?.name ?: "Produits",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        if (category != null) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp), // Wider cards for products
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(category.items) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id, categoryId) }
                    )
                }
            }
        } else {
             Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                 Text("Catégorie introuvable", color = Color.White)
             }
        }
    }
}
