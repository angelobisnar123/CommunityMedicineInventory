package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    override fun onResume() {
        super.onResume()
        if (::dashboardLayout.isInitialized) {
            refreshDashboard()
        }
    }

    private lateinit var dashboardLayout: android.widget.LinearLayout
    private lateinit var totalText: android.widget.TextView
    private lateinit var lowStockText: android.widget.TextView

    private fun showDashboard() {
        dashboardLayout = UiHelper.screen(this)

        dashboardLayout.addView(
            UiHelper.title(this, "Dashboard")
        )

        totalText = UiHelper.text(this, "")
        lowStockText = UiHelper.text(this, "")

        dashboardLayout.addView(totalText)
        dashboardLayout.addView(lowStockText)

        dashboardLayout.addView(
            UiHelper.button(this, "Medicine Inventory") {
                startActivity(
                    Intent(this, InventoryActivity::class.java)
                )
            }
        )

        dashboardLayout.addView(
            UiHelper.button(this, "Stock Alerts") {
                startActivity(
                    Intent(this, StockAlertsActivity::class.java)
                )
            }
        )

        dashboardLayout.addView(
            UiHelper.button(this, "Logout") {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
        )

        setContentView(dashboardLayout)
        refreshDashboard()
    }

    private fun refreshDashboard() {
        totalText.text =
            "Total medicine records: ${MedicineRepository.totalMedicines()}"

        lowStockText.text =
            "Low-stock medicines: ${MedicineRepository.lowStockCount()}"
    }
}