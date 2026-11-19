
package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var adminWelcomeText: TextView
    private lateinit var createStaffButton: Button
    private lateinit var viewStaffButton: Button
    private lateinit var adminInventoryButton: Button
    private lateinit var adminAlertsButton: Button
    private lateinit var adminLogoutButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        auth = FirebaseAuth.getInstance()

        // Make sure an authenticated user is present.
        if (auth.currentUser == null) {
            returnToLogin()
            return
        }

        adminWelcomeText = findViewById(R.id.adminWelcomeText)
        createStaffButton = findViewById(R.id.createStaffButton)
        viewStaffButton = findViewById(R.id.viewStaffButton)
        adminInventoryButton = findViewById(R.id.adminInventoryButton)
        adminAlertsButton = findViewById(R.id.adminAlertsButton)
        adminLogoutButton = findViewById(R.id.adminLogoutButton)

        val email = auth.currentUser?.email ?: "Administrator"

        adminWelcomeText.text = "Welcome, Admin\n$email"

        // Create Staff.
        createStaffButton.setOnClickListener {
            startActivity(
                Intent(this, CreateStaffActivity::class.java)
            )
        }

        // View Staff.
        viewStaffButton.setOnClickListener {
            startActivity(
                Intent(this, StaffListActivity::class.java)
            )
        }

        // Open Inventory as Admin.
        adminInventoryButton.setOnClickListener {

            val intent = Intent(
                this,
                InventoryActivity::class.java
            )

            intent.putExtra("USER_ROLE", "admin")

            startActivity(intent)
        }

        // Open Stock Alerts as Admin.
        adminAlertsButton.setOnClickListener {

            val intent = Intent(
                this,
                StockAlertsActivity::class.java
            )

            intent.putExtra("USER_ROLE", "admin")

            startActivity(intent)
        }

        // Logout.
        adminLogoutButton.setOnClickListener {

            auth.signOut()
            returnToLogin()
        }
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
