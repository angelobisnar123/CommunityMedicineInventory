package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val totalText = findViewById<TextView>(R.id.totalText)
        val lowStockText = findViewById<TextView>(R.id.lowStockText)

        val inventoryButton =
            findViewById<Button>(R.id.inventoryButton)

        val stockAlertsButton =
            findViewById<Button>(R.id.stockAlertsButton)

        val logoutButton =
            findViewById<Button>(R.id.logoutButton)

        totalText.text = MedicineRepository.totalMedicines().toString()
        lowStockText.text = MedicineRepository.lowStockCount().toString()

        inventoryButton.setOnClickListener {
            startActivity(
                Intent(this, InventoryActivity::class.java)
            )
        }

        stockAlertsButton.setOnClickListener {
            startActivity(
                Intent(this, StockAlertsActivity::class.java)
            )
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        findViewById<TextView>(R.id.totalText).text =
            MedicineRepository.totalMedicines().toString()

        findViewById<TextView>(R.id.lowStockText).text =
            MedicineRepository.lowStockCount().toString()
    }
}