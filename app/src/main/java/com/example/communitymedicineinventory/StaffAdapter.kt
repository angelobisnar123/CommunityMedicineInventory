package com.example.communitymedicineinventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StaffAdapter(
    private var staffList: List<Staff>
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    class StaffViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val staffName: TextView = itemView.findViewById(R.id.staffName)
        val staffEmail: TextView = itemView.findViewById(R.id.staffEmail)
        val staffRole: TextView = itemView.findViewById(R.id.staffRole)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StaffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff, parent, false)

        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StaffViewHolder,
        position: Int
    ) {
        val staff = staffList[position]

        holder.staffName.text = staff.name
        holder.staffEmail.text = staff.email

        holder.staffRole.text = when (staff.role) {
            "inventory_custodian" -> "Inventory Custodian"
            "health_worker" -> "Health Worker"
            else -> staff.role
        }
    }

    override fun getItemCount(): Int = staffList.size

    fun updateList(newList: List<Staff>) {
        staffList = newList
        notifyDataSetChanged()
    }
}