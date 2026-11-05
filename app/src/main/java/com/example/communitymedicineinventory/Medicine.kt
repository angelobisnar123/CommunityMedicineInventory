package com.example.communitymedicineinventory

data class Medicine(
    val medicineId: Int,
    var name: String,
    var category: String,
    var quantity: Int,
    var minimumStock: Int,
    var expirationDate: String,
    var unit: String
) {
    fun isLowStock(): Boolean {
        return quantity <= minimumStock
    }
}