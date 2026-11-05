package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : AppCompatActivity() {

    private lateinit var adapter: MedicineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val addMedicineButton =
            findViewById<Button>(R.id.addMedicineButton)

        val recyclerView =
            findViewById<RecyclerView>(R.id.medicineRecyclerView)

        val backButton = findViewById<Button>(R.id.backButton)

        adapter = MedicineAdapter(MedicineRepository.getAll()) { medicine ->
            val intent = Intent(this, MedicineDetailsActivity::class.java)
            intent.putExtra(
                MedicineDetailsActivity.EXTRA_MEDICINE_ID,
                medicine.medicineId
            )
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        addMedicineButton.setOnClickListener {
            startActivity(
                Intent(this, AddEditMedicineActivity::class.java)
            )
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            val searchInput = findViewById<EditText>(R.id.searchInput)
            adapter.filter(searchInput.text.toString())
        }
    }
}