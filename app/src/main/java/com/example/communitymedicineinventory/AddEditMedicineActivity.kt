package com.example.communitymedicineinventory

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditMedicineActivity : AppCompatActivity() {

    private var medicineId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_medicine)

        val formTitle = findViewById<TextView>(R.id.formTitle)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val categoryInput = findViewById<EditText>(R.id.categoryInput)
        val quantityInput = findViewById<EditText>(R.id.quantityInput)
        val minimumInput = findViewById<EditText>(R.id.minimumInput)
        val expirationInput = findViewById<EditText>(R.id.expirationInput)
        val unitInput = findViewById<EditText>(R.id.unitInput)

        val saveButton =
            findViewById<Button>(R.id.saveMedicineButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        medicineId = intent.getIntExtra(EXTRA_MEDICINE_ID, -1)

        // Load existing medicine when editing.
        if (medicineId != -1) {
            formTitle.text = "Edit Medicine"

            val medicine = MedicineRepository.getById(medicineId)

            if (medicine != null) {
                nameInput.setText(medicine.name)
                categoryInput.setText(medicine.category)
                quantityInput.setText(medicine.quantity.toString())
                minimumInput.setText(medicine.minimumStock.toString())
                expirationInput.setText(medicine.expirationDate)
                unitInput.setText(medicine.unit)
            } else {
                Toast.makeText(
                    this,
                    "Medicine record not found.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val category = categoryInput.text.toString().trim()
            val quantityText = quantityInput.text.toString().trim()
            val minimumText = minimumInput.text.toString().trim()
            val expirationDate = expirationInput.text.toString().trim()
            val unit = unitInput.text.toString().trim()

            val quantity = quantityText.toIntOrNull()
            val minimumStock = minimumText.toIntOrNull()

            when {
                name.isEmpty() -> nameInput.error = "Enter medicine name"
                category.isEmpty() -> categoryInput.error = "Enter category"
                quantity == null || quantity < 0 ->
                    quantityInput.error = "Enter a valid quantity"

                minimumStock == null || minimumStock < 0 ->
                    minimumInput.error = "Enter a valid minimum stock"

                !expirationDate.matches(
                    Regex("^\\d{4}-\\d{2}-\\d{2}$")
                ) -> expirationInput.error =
                    "Use YYYY-MM-DD format"

                unit.isEmpty() -> unitInput.error = "Enter medicine unit"

                else -> {
                    val medicine = Medicine(
                        medicineId = if (medicineId == -1) {
                            MedicineRepository.nextId()
                        } else {
                            medicineId
                        },
                        name = name,
                        category = category,
                        quantity = quantity,
                        minimumStock = minimumStock,
                        expirationDate = expirationDate,
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
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_MEDICINE_ID = "medicine_id"
    }
}