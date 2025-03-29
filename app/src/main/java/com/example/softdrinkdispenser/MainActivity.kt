package com.example.softdrinkdispenser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softdrinkdispenser.ui.theme.SoftDrinkDispenserTheme

const val PRODUCTPRICE = 50

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoftDrinkDispenserTheme {
                Scaffold(
                    topBar = {
                        TopNavBar(title = "SoftDrink Dispenser")
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        VendingMachineScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    title: String,
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun VendingMachineScreen() {
    var insertedAmount by remember { mutableIntStateOf(0) }
    var transactionMessage by remember { mutableStateOf("Insert money to buy a drink.") }
    var purchasedProducts by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Display transaction message and current balance
        TransactionCard(
            message = if (purchasedProducts.isNotEmpty()) {
                "Purchased: ${purchasedProducts.joinToString()}\nBalance: KShs $insertedAmount"
            } else {
                transactionMessage
            }
        )
        // Display product grid
        ProductGrid(
            canPurchase = insertedAmount >= PRODUCTPRICE,
            onProductSelect = { productName ->
                if (insertedAmount >= PRODUCTPRICE) {
                    purchasedProducts = purchasedProducts + productName
                    insertedAmount -= PRODUCTPRICE
                    transactionMessage = "Added $productName. Balance: KShs $insertedAmount"
                } else {
                    transactionMessage = "Insufficient funds! Insert at least KShs $PRODUCTPRICE."
                }
            },
        )

        // Display currency buttons
        CurrencyButtons { amount ->
            insertedAmount += amount
            transactionMessage = "Inserted: KShs $insertedAmount. Total: KShs " +
                    "${insertedAmount + (purchasedProducts.size * PRODUCTPRICE)}"
        }

        // Dispense button (Only enabled if products were purchased)
        Button(
            onClick = {
                val productList = purchasedProducts.joinToString()
                transactionMessage = "Dispensed: $productList. Change returned: KShs $insertedAmount"
                insertedAmount = 0 // Reset balance
                purchasedProducts = emptyList()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = purchasedProducts.isNotEmpty()
        ) {
            Text("Dispense Drinks")
        }
    }
}

@Composable
fun TransactionCard(message: String) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrencyButtons(onInsert: (Int) -> Unit) {
    val denominations = listOf(10, 20, 40, 50, 100, 200, 500, 1000)

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        denominations.forEach { amount ->
            Button(
                onClick = { onInsert(amount) },
                modifier = Modifier.padding(2.dp)
            ) {
                Text(text = amount.toString())
            }
        }
    }
}

@Composable
fun ProductGrid(
    canPurchase: Boolean,
    onProductSelect: (String) -> Unit,
) {
    val dispenserProducts = listOf(
        DispenserProducts(painterResource(R.drawable.cola), "CocaCola"),
        DispenserProducts(painterResource(R.drawable.sprite), "Sprite"),
        DispenserProducts(painterResource(R.drawable.energy), "Golden Eagle"),
        DispenserProducts(painterResource(R.drawable.fanta), "Fanta"),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(dispenserProducts) { product ->
            ProductItem(
                product = product,
                enabled = canPurchase,
                onClick = { onProductSelect(product.name) }
            )
        }
    }
}

@Composable
fun ProductItem(
    product: DispenserProducts,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = product.icon,
                contentDescription = product.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "KShs 50", fontSize = 12.sp)
        }
    }
}
