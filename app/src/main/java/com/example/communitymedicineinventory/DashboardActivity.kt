
package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var totalText: TextView
    private lateinit var lowStockText: TextView

    private lateinit var inventoryButton: Button
    private lateinit var stockAlertsButton: Button
    private lateinit var logoutButton: Button

    private var databaseListener: ValueEventListener? = null

    private var userRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the role passed from LoginActivity.
        userRole = intent.getStringExtra("USER_ROLE") ?: ""

        // Allow only Inventory Custodian and Health Worker here.
        // Admin uses AdminDashboardActivity.
        if (
            userRole != "inventory_custodian" &&
            userRole != "health_worker"
        ) {
            Toast.makeText(
                this,
                "Unauthorized access.",
                Toast.LENGTH_LONG
            ).show()

            returnToLogin()
            return
        }

        // Check Firebase authentication.
        if (FirebaseAuth.getInstance().currentUser == null) {
            returnToLogin()
            return
        }

        setContentView(R.layout.activity_dashboard)

        totalText = findViewById(R.id.totalText)
        lowStockText = findViewById(R.id.lowStockText)

        inventoryButton = findViewById(R.id.inventoryButton)
        stockAlertsButton = findViewById(R.id.stockAlertsButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Open Inventory.
        inventoryButton.setOnClickListener {

            val intent = Intent(
                this,
                InventoryActivity::class.java
            )

            intent.putExtra("USER_ROLE", userRole)

            startActivity(intent)
        }

        // Open Stock Alerts.
        stockAlertsButton.setOnClickListener {

            val intent = Intent(
                this,
                StockAlertsActivity::class.java
            )

            intent.putExtra("USER_ROLE", userRole)

            startActivity(intent)
        }

        // Logout.
        logoutButton.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            returnToLogin()
        }
    }

    override fun onStart() {
        super.onStart()

        if (
            userRole != "inventory_custodian" &&
            userRole != "health_worker"
        ) {
            return
        }

        databaseListener = MedicineRepository.observeAll(

            onDataChanged = { medicines ->

                totalText.text = medicines.size.toString()

                val lowStockCount =
                    medicines.count { it.isLowStock() }

                lowStockText.text = lowStockCount.toString()
            },

            onError = { error ->
                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
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

    private fun returnToLogin() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}
