package com.example.softdrinkdispenser

import androidx.compose.ui.text.toLowerCase

class VendingMachine {
    private var balance = 0
    private val price = 50
    private val validDenominations = setOf(10, 20, 40, 50, 100, 200, 500, 1000)
    private val drinks = listOf("CocaCola", "Pepsi", "Minute Maid", "U Fresh")

    fun insertMoney(amount: Int) {
        if (amount in validDenominations) {
            balance += amount
            println("Inserted: KSh $amount, Total balance: KSh $balance")
        } else {
            println("Invalid denomination. Please insert valid coins or notes.")
        }
    }

    fun selectDrink(): Boolean {
        println("Welcome to the Soft Drink Dispenser!")
        println("Available drinks: ${drinks.joinToString(", ")}")
        println("Please select a drink:")
        val chosenDrink = readln()

        return if (chosenDrink in drinks) {
            println("You've chosen $chosenDrink.")
            println("Amount required: KSh $price")
            true
        } else {
            println("Invalid choice. Please select a valid drink.")
            false
        }
    }

    fun buyDrink() {
        if (balance >= price) {
            println("Select the number of drinks you'd like to buy (Max: ${balance / price}):")
            val drinksRequested = readLine()?.toIntOrNull() ?: 1

            val maxDrinks = balance / price
            val drinksPurchased = if (drinksRequested in 1..maxDrinks) drinksRequested else 1
            val totalCost = drinksPurchased * price
            val change = balance - totalCost
            balance = change

            println("Dispensed $drinksPurchased drink(s). Change returned: KSh $change")
        } else {
            println("Insufficient funds. Please insert at least KSh $price.")
        }
    }
}

fun main() {
    val vendingMachine = VendingMachine()

    while (true) {
        if (vendingMachine.selectDrink()) {
            println("Insert money (Enter amount or 0 to cancel):")
            val amount = readln().toIntOrNull() ?: 0
            if (amount == 0) {
                println("Transaction cancelled.")
                break
            }
            vendingMachine.insertMoney(amount)
            vendingMachine.buyDrink()
        }
        println("Would you like to buy another drink? (yes/no)")
        if (readln().lowercase() != "yes") {
            println("Thank you for using the vending machine!")
            break
        }
    }
}
