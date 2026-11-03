package com.example.communitymedicineinventory

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AddEditMedicineActivity : AppCompatActivity() {

    private var medicineId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        medicineId = intent.getIntExtra(
            MedicineDetailsActivity.EXTRA_MEDICINE_ID, -1
        )

        val existing = MedicineRepository.getById(medicineId)
        val editing = existing != null

        val layout = UiHelper.screen(this)

        layout.addView(
            UiHelper.title(
                this,
                if (editing) "Edit Medicine" else "Add Medicine"
            )
        )

        val nameInput = UiHelper.input(this, "Medicine name")
        val categoryInput = UiHelper.input(this, "Category")
        val quantityInput = UiHelper.input(this, "Quantity")
        val minimumInput = UiHelper.input(this, "Minimum stock")
        val expirationInput =
            UiHelper.input(this, "Expiration date (YYYY-MM-DD)")
        val unitInput =
            UiHelper.input(this, "Unit (tablets, bottles, etc.)")

        quantityInput.inputType = InputType.TYPE_CLASS_NUMBER
        minimumInput.inputType = InputType.TYPE_CLASS_NUMBER

        if (existing != null) {
            nameInput.setText(existing.name)
            categoryInput.setText(existing.category)
            quantityInput.setText(existing.quantity.toString())
            minimumInput.setText(existing.minimumStock.toString())
            expirationInput.setText(existing.expirationDate)
            unitInput.setText(existing.unit)
        }

        layout.addView(nameInput)
        layout.addView(categoryInput)
        layout.addView(quantityInput)
        layout.addView(minimumInput)
        layout.addView(expirationInput)
        layout.addView(unitInput)

        layout.addView(
            UiHelper.button(this, "Save Medicine") {
                val name = nameInput.text.toString().trim()
                val category = categoryInput.text.toString().trim()
                val quantity = quantityInput.text.toString().toIntOrNull()
                val minimum = minimumInput.text.toString().toIntOrNull()
                val expiration = expirationInput.text.toString().trim()
                val unit = unitInput.text.toString().trim()

                val dateFormat = SimpleDateFormat(
                    "yyyy-MM-dd", Locale.US
                ).apply {
                    isLenient = false
                }

                val validDate = try {
                    val date = dateFormat.parse(expiration)
                    date != null &&
                            dateFormat.format(date) == expiration
                } catch (_: Exception) {
                    false
                }

                if (
                    name.isBlank() ||
                    category.isBlank() ||
                    quantity == null ||
                    minimum == null ||
                    quantity < 0 ||
                    minimum < 0 ||
                    unit.isBlank() ||
                    !validDate
                ) {
                    Toast.makeText(
                        this,
                        "Check all fields and use YYYY-MM-DD for the date.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@button
                }

                val medicine = Medicine(
                    medicineId = if (editing) {
                        medicineId
                    } else {
                        MedicineRepository.nextId()
                    },
                    name = name,
                    category = category,
                    quantity = quantity,
                    minimumStock = minimum,
                    expirationDate = expiration,
                    unit = unit
                )

                MedicineRepository.save(medicine)

                Toast.makeText(
                    this,
                    "Medicine saved successfully.",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        )

        layout.addView(
            UiHelper.button(this, "Cancel") {
                finish()
            }
        )

        setContentView(
            UiHelper.scrollable(this, layout)
        )
    }
}