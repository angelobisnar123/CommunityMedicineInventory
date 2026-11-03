package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MedicineDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MEDICINE_ID = "medicine_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getIntExtra(EXTRA_MEDICINE_ID, -1)
        val medicine = MedicineRepository.getById(id)

        val layout = UiHelper.screen(this)

        layout.addView(
            UiHelper.title(this, "Medicine Details")
        )

        if (medicine == null) {
            layout.addView(
                UiHelper.text(this, "Medicine record not found.")
            )
        } else {
            layout.addView(
                UiHelper.text(this, "ID: ${medicine.medicineId}")
            )
            layout.addView(
                UiHelper.text(this, "Name: ${medicine.name}")
            )
            layout.addView(
                UiHelper.text(this, "Category: ${medicine.category}")
            )
            layout.addView(
                UiHelper.text(
                    this,
                    "Quantity: ${medicine.quantity} ${medicine.unit}"
                )
            )
            layout.addView(
                UiHelper.text(
                    this, "Minimum stock: ${medicine.minimumStock}"
                )
            )
            layout.addView(
                UiHelper.text(
                    this, "Expiration date: ${medicine.expirationDate}"
                )
            )
            layout.addView(
                UiHelper.text(
                    this,
                    if (medicine.isLowStock()) {
                        "Status: LOW STOCK"
                    } else {
                        "Status: Stock available"
                    }
                )
            )

            layout.addView(
                UiHelper.button(this, "Edit Medicine") {
                    val editIntent = Intent(
                        this, AddEditMedicineActivity::class.java
                    )
                    editIntent.putExtra(
                        EXTRA_MEDICINE_ID,
                        medicine.medicineId
                    )
                    startActivity(editIntent)
                }
            )
        }

        layout.addView(
            UiHelper.button(this, "Back to Inventory") {
                finish()
            }
        )

        setContentView(layout)
    }
}