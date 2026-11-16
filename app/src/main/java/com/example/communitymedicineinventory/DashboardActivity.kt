package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var totalText: TextView
    private lateinit var lowStockText: TextView

    private lateinit var inventoryButton: Button
    private lateinit var stockAlertsButton: Button
    private lateinit var logoutButton: Button

    private var databaseListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        totalText = findViewById(R.id.totalText)
        lowStockText = findViewById(R.id.lowStockText)

        inventoryButton = findViewById(R.id.inventoryButton)
        stockAlertsButton = findViewById(R.id.stockAlertsButton)
        logoutButton = findViewById(R.id.logoutButton)

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

    override fun onStart() {
        super.onStart()

        databaseListener = MedicineRepository.observeAll(
            onDataChanged = { medicines ->
                totalText.text = medicines.size.toString()

                val lowStockCount =
                    medicines.count { it.isLowStock() }

                lowStockText.text = lowStockCount.toString()
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onStop() {
        super.onStop()

        databaseListener?.let {
            MedicineRepository.removeListener(it)
        }

        databaseListener = null
    }
}