package com.example.communitymedicineinventory

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapter(
    private var items: List<Medicine>,
    private val onClick: (Medicine) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(
        val container: LinearLayout,
        val nameText: TextView,
        val summaryText: TextView,
        val statusText: TextView
    ) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicineViewHolder {
        val context = parent.context

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.WHITE)
        }

        val name = TextView(context).apply {
            textSize = 18f
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
        }

        val summary = TextView(context).apply {
            textSize = 14f
            setTextColor(Color.DKGRAY)
        }

        val status = TextView(context).apply {
            textSize = 14f
            setTextColor(Color.BLACK)
        }

        container.addView(name)
        container.addView(summary)
        container.addView(status)

        return MedicineViewHolder(
            container, name, summary, status
        )
    }

    override fun onBindViewHolder(
        holder: MedicineViewHolder,
        position: Int
    ) {
        val medicine = items[position]

        holder.nameText.text = medicine.name
        holder.summaryText.text =
            "${medicine.category} | Stock: ${medicine.quantity} ${medicine.unit}"

        holder.statusText.text =
            if (medicine.isLowStock()) {
                "LOW STOCK | Minimum: ${medicine.minimumStock}"
            } else {
                "Stock available"
            }

        holder.container.setOnClickListener {
            onClick(medicine)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Medicine>) {
        items = newItems
        notifyDataSetChanged()
    }
}