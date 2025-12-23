package com.loasisloos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loasisloos.ui.theme.LOasisLoosTheme
import com.loasisloos.ui.theme.DarkBackground

// Placeholder screens for now
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LOasisLoosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val navController = rememberNavController()
                    // Share ViewModel across screens
                    val orderViewModel: com.loasisloos.viewmodel.OrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    
                    NavHost(navController = navController, startDestination = "menu") {
                        
                        // 1. Main Categories
                        composable("menu") { 
                           com.loasisloos.ui.screens.MenuScreen(
                               onCategoryClick = { categoryId ->
                                   navController.navigate("category/$categoryId")
                               },
                               onCartClick = {
                                   navController.navigate("cart")
                               },
                               viewModel = androidx.lifecycle.viewmodel.compose.viewModel() // Scoped to screen is fine for menu
                           )
                        }
                        
                        // 2. Product List for Category
                        composable("category/{categoryId}") { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
                            com.loasisloos.ui.screens.CategoryProductsScreen(
                                categoryId = categoryId,
                                onProductClick = { productId, _ ->
                                    navController.navigate("product/$productId")
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        
                        // 3. Product Detail (Customization)
                        composable("product/{productId}") { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
                            com.loasisloos.ui.screens.ProductDetailScreen(
                                productId = productId,
                                onBackClick = { navController.popBackStack() },
                                onAddToCart = {
                                    navController.popBackStack() // Go back to list or menu? 
                                    // Usually for ordering apps, pop back to category list allows adding more items quickly
                                    // Or navigate to Cart? 
                                    // Let's just pop back for flow efficiency
                                },
                                orderViewModel = orderViewModel // Shared instance
                            )
                        }
                        
                        // 4. Cart
                        composable("cart") {
                            com.loasisloos.ui.screens.CartScreen(
                                onBackClick = { navController.popBackStack() },
                                orderViewModel = orderViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
