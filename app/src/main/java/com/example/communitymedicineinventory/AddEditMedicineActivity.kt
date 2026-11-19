
package com.example.communitymedicineinventory

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AddEditMedicineActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var quantityInput: EditText
    private lateinit var minimumInput: EditText
    private lateinit var unitInput: EditText
    private lateinit var expirationInput: EditText

    private lateinit var saveMedicineButton: Button
    private lateinit var cancelButton: Button

    private var medicineId: String = ""
    private var userRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRole = intent.getStringExtra("USER_ROLE") ?: ""

        if (userRole != "admin" &&
            userRole != "inventory_custodian"
        ) {
            Toast.makeText(
                this,
                "You are not authorized to manage medicines.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContentView(R.layout.activity_add_edit_medicine)

        nameInput = findViewById(R.id.nameInput)
        categoryInput = findViewById(R.id.categoryInput)
        quantityInput = findViewById(R.id.quantityInput)
        minimumInput = findViewById(R.id.minimumInput)
        unitInput = findViewById(R.id.unitInput)
        expirationInput = findViewById(R.id.expirationInput)

        saveMedicineButton = findViewById(R.id.saveMedicineButton)
        cancelButton = findViewById(R.id.cancelButton)

        medicineId = intent.getStringExtra("MEDICINE_ID") ?: ""

        if (medicineId.isNotBlank()) {
            loadMedicine()
            saveMedicineButton.text = "Update Medicine"
        }

        saveMedicineButton.setOnClickListener {
            saveMedicine()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadMedicine() {

        MedicineRepository.getById(
            medicineId,
            onResult = { medicine ->

                if (medicine != null) {
                    nameInput.setText(medicine.name)
                    categoryInput.setText(medicine.category)
                    quantityInput.setText(medicine.quantity.toString())
                    minimumInput.setText(medicine.minimumStock.toString())
                    unitInput.setText(medicine.unit)
                    expirationInput.setText(medicine.expirationDate)
                } else {
                    Toast.makeText(
                        this,
                        "Medicine not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
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

    private fun saveMedicine() {

        if (userRole != "admin" &&
            userRole != "inventory_custodian"
        ) {
            Toast.makeText(
                this,
                "You are not authorized to save medicines.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val name = nameInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()
        val quantityText = quantityInput.text.toString().trim()
        val minimumText = minimumInput.text.toString().trim()
        val unit = unitInput.text.toString().trim()
        val expiration = expirationInput.text.toString().trim()

        if (
            name.isEmpty() ||
            category.isEmpty() ||
            quantityText.isEmpty() ||
            minimumText.isEmpty() ||
            unit.isEmpty() ||
            expiration.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Please complete all fields.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val quantity = quantityText.toIntOrNull()
        val minimum = minimumText.toIntOrNull()

        if (quantity == null || minimum == null ||
            quantity < 0 || minimum < 0
        ) {
            Toast.makeText(
                this,
                "Enter valid non-negative quantities.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).apply {
            isLenient = false
        }

        val validDate = try {
            val parsedDate = dateFormat.parse(expiration)
            parsedDate != null &&
                    dateFormat.format(parsedDate) == expiration
        } catch (e: Exception) {
            false
        }

        if (!validDate) {
            Toast.makeText(
                this,
                "Enter a valid expiration date (YYYY-MM-DD).",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val medicine = Medicine(
            medicineId = medicineId,
            name = name,
            category = category,
            quantity = quantity,
            minimumStock = minimum,
            expirationDate = expiration,
            unit = unit
        )

        saveMedicineButton.isEnabled = false

        MedicineRepository.save(
            medicine,
            onSuccess = {
                Toast.makeText(
                    this,
                    if (medicineId.isBlank()) {
                        "Medicine added successfully!"
                    } else {
                        "Medicine updated successfully!"
                    },
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },
            onError = { error ->
                saveMedicineButton.isEnabled = true

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}
