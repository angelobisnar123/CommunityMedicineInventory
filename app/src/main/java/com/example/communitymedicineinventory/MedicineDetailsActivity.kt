package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicineDetailsActivity : AppCompatActivity() {

    private var medicineId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_details)

        medicineId = intent.getIntExtra(EXTRA_MEDICINE_ID, -1)

        val editButton =
            findViewById<Button>(R.id.editMedicineButton)

        val backButton =
            findViewById<Button>(R.id.backToInventoryButton)

        editButton.setOnClickListener {
            val intent = Intent(this, AddEditMedicineActivity::class.java)
            intent.putExtra(
                AddEditMedicineActivity.EXTRA_MEDICINE_ID,
                medicineId
            )
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        displayMedicine()
    }

    private fun displayMedicine() {
        val medicine = MedicineRepository.getById(medicineId)

        if (medicine == null) {
            Toast.makeText(
                this,
                "Medicine record not found.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.detailId).text =
            "Medicine ID: ${medicine.medicineId}"

        findViewById<TextView>(R.id.detailName).text =
            medicine.name

        findViewById<TextView>(R.id.detailCategory).text =
            "Category: ${medicine.category}"

        findViewById<TextView>(R.id.detailQuantity).text =
            "Quantity: ${medicine.quantity} ${medicine.unit}"

        findViewById<TextView>(R.id.detailMinimum).text =
            "Minimum Stock: ${medicine.minimumStock} ${medicine.unit}"

        findViewById<TextView>(R.id.detailExpiration).text =
            "Expiration Date: ${medicine.expirationDate}"

        findViewById<TextView>(R.id.detailStatus).text =
            if (medicine.isLowStock()) {
                "Status: LOW STOCK - Restocking required"
            } else {
                "Status: Stock available"
            }
    }

    override fun onResume() {
        super.onResume()

        if (medicineId != -1) {
            displayMedicine()
        }
    }

    companion object {
        const val EXTRA_MEDICINE_ID = "medicine_id"
    }
}