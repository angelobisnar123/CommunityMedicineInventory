package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : AppCompatActivity() {

    private lateinit var adapter: MedicineAdapter
    private lateinit var searchInput: android.widget.EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = UiHelper.screen(this)

        layout.addView(
            UiHelper.title(this, "Medicine Inventory")
        )

        searchInput = UiHelper.input(
            this, "Search medicine or category"
        )
        layout.addView(searchInput)

        layout.addView(
            UiHelper.button(this, "Add Medicine") {
                startActivity(
                    Intent(this, AddEditMedicineActivity::class.java)
                )
            }
        )

        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity)
        }

        adapter = MedicineAdapter(
            MedicineRepository.getAll()
        ) { medicine ->
            val intent = Intent(
                this, MedicineDetailsActivity::class.java
            )
            intent.putExtra(
                MedicineDetailsActivity.EXTRA_MEDICINE_ID,
                medicine.medicineId
            )
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        layout.addView(
            recyclerView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        layout.addView(
            UiHelper.button(this, "Back to Dashboard") {
                finish()
            }
        )

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?, start: Int,
                before: Int, count: Int
            ) {
                filterMedicines(s.toString())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            filterMedicines(searchInput.text.toString())
        }
    }

    private fun filterMedicines(query: String) {
        val filtered = MedicineRepository.getAll().filter {
            it.name.contains(query.trim(), ignoreCase = true) ||
                    it.category.contains(query.trim(), ignoreCase = true)
        }

        adapter.updateData(filtered)
    }
}