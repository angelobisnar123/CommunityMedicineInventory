package com.example.communitymedicineinventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StockAlertsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = UiHelper.screen(this)

        layout.addView(
            UiHelper.title(this, "Stock Alerts")
        )

        val lowStock = MedicineRepository.getLowStock()

        if (lowStock.isEmpty()) {
            layout.addView(
                UiHelper.text(this, "No low-stock medicines.")
            )
        } else {
            lowStock.forEach { medicine ->
                layout.addView(
                    UiHelper.text(
                        this,
                        "${medicine.name}\n" +
                                "Available: ${medicine.quantity} ${medicine.unit}\n" +
                                "Minimum: ${medicine.minimumStock} ${medicine.unit}"
                    )
                )
            }
        }

        layout.addView(
            UiHelper.button(this, "Back to Dashboard") {
                finish()
            }
        )

        setContentView(
            UiHelper.scrollable(this, layout)
        )
    }
}