package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StockAlertsActivity : AppCompatActivity() {

    private lateinit var adapter: MedicineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_alerts)

        val description =
            findViewById<TextView>(R.id.stockAlertDescription)

        val recyclerView =
            findViewById<RecyclerView>(R.id.stockAlertsRecyclerView)

        val backButton =
            findViewById<Button>(R.id.backToDashboardButton)

        adapter = MedicineAdapter(MedicineRepository.getLowStock()) { medicine ->
            val intent = Intent(this, MedicineDetailsActivity::class.java)

            intent.putExtra(
                MedicineDetailsActivity.EXTRA_MEDICINE_ID,
                medicine.medicineId
            )

            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            finish()
        }

        updateAlerts()
    }

    private fun updateAlerts() {
        val lowStockMedicines = MedicineRepository.getLowStock()

        adapter.updateData(lowStockMedicines)

        val description =
            findViewById<TextView>(R.id.stockAlertDescription)

        description.text =
            if (lowStockMedicines.isEmpty()) {
                "No medicines have reached their minimum stock level."
            } else {
                "${lowStockMedicines.size} medicine(s) need restocking."
            }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            updateAlerts()
        }
    }
}