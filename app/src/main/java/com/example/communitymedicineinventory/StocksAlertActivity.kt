package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener

class StockAlertsActivity : AppCompatActivity() {

    private lateinit var stockAlertDescription: TextView
    private lateinit var stockAlertsRecyclerView: RecyclerView
    private lateinit var backToDashboardButton: Button

    private lateinit var adapter: MedicineAdapter

    private var databaseListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_alerts)

        stockAlertDescription =
            findViewById(R.id.stockAlertDescription)

        stockAlertsRecyclerView =
            findViewById(R.id.stockAlertsRecyclerView)

        backToDashboardButton =
            findViewById(R.id.backToDashboardButton)

        adapter = MedicineAdapter(emptyList()) { medicine ->
            val intent = Intent(
                this,
                MedicineDetailsActivity::class.java
            )

            intent.putExtra("MEDICINE_ID", medicine.medicineId)
            startActivity(intent)
        }

        stockAlertsRecyclerView.layoutManager =
            LinearLayoutManager(this)

        stockAlertsRecyclerView.adapter = adapter

        backToDashboardButton.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        databaseListener = MedicineRepository.observeAll(
            onDataChanged = { medicines ->
                val lowStockMedicines =
                    medicines.filter { it.isLowStock() }

                adapter.updateList(lowStockMedicines)

                stockAlertDescription.text =
                    if (lowStockMedicines.isEmpty()) {
                        "All medicines have sufficient stock."
                    } else {
                        "${lowStockMedicines.size} medicine(s) need restocking."
                    }
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