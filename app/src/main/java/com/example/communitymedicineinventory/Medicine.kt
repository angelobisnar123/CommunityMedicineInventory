package com.example.communitymedicineinventory

data class Medicine(
    var medicineId: String = "",
    var name: String = "",
    var category: String = "",
    var quantity: Int = 0,
    var minimumStock: Int = 0,
    var expirationDate: String = "",
    var unit: String = ""
) {
    fun isLowStock(): Boolean {
        return quantity <= minimumStock
    }
}