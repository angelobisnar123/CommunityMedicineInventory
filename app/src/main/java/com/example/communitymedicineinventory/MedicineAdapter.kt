package com.example.communitymedicineinventory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapter(
    private var items: List<Medicine>,
    private val onClick: (Medicine) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val nameText: TextView =
            itemView.findViewById(R.id.medicineName)

        val summaryText: TextView =
            itemView.findViewById(R.id.medicineSummary)

        val statusText: TextView =
            itemView.findViewById(R.id.medicineStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicineViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)

        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MedicineViewHolder,
        position: Int
    ) {
        val medicine = items[position]

        holder.nameText.text = medicine.name

        holder.summaryText.text =
            "${medicine.category} | Stock: ${medicine.quantity} ${medicine.unit}"

        if (medicine.isLowStock()) {
            holder.statusText.text =
                "LOW STOCK | Minimum: ${medicine.minimumStock}"

            holder.statusText.setTextColor(Color.RED)
        } else {
            holder.statusText.text = "Stock available"
            holder.statusText.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            onClick(medicine)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<Medicine>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val filteredItems = if (query.isBlank()) {
            MedicineRepository.getAll()
        } else {
            MedicineRepository.getAll().filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        }

        updateData(filteredItems)
    }
}