package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener

class InventoryActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var addMedicineButton: Button
    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var backButton: Button

    private lateinit var adapter: MedicineAdapter

    private var allMedicines = listOf<Medicine>()
    private var databaseListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        searchInput = findViewById(R.id.searchInput)
        addMedicineButton = findViewById(R.id.addMedicineButton)
        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        backButton = findViewById(R.id.backButton)

        adapter = MedicineAdapter(emptyList()) { medicine ->
            val intent = Intent(this, MedicineDetailsActivity::class.java)
            intent.putExtra("MEDICINE_ID", medicine.medicineId)
            startActivity(intent)
        }

        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
        medicineRecyclerView.adapter = adapter

        addMedicineButton.setOnClickListener {
            startActivity(
                Intent(this, AddEditMedicineActivity::class.java)
            )
        }

        backButton.setOnClickListener {
            finish()
        }

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
                filterMedicines(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onStart() {
        super.onStart()

        databaseListener = MedicineRepository.observeAll(
            onDataChanged = { medicines ->
                allMedicines = medicines
                filterMedicines(searchInput.text.toString())
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

    private fun filterMedicines(query: String) {
        val filtered = allMedicines.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }

        adapter.updateList(filtered)
    }
}