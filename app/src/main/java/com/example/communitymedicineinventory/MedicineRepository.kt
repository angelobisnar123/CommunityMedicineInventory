
package com.example.communitymedicineinventory

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object MedicineRepository {

    private val medicinesRef = FirebaseDatabase.getInstance(
        "https://communitymedicineinventory-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("medicines")

    fun observeAll(
        onDataChanged: (List<Medicine>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val medicines = mutableListOf<Medicine>()

                for (child in snapshot.children) {

                    val medicine = child.getValue(Medicine::class.java)

                    if (medicine != null) {
                        medicine.medicineId = child.key ?: ""
                        medicines.add(medicine)
                    }
                }

                onDataChanged(medicines)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }

        medicinesRef.addValueEventListener(listener)

        return listener
    }

    fun removeListener(listener: ValueEventListener) {
        medicinesRef.removeEventListener(listener)
    }

    fun save(
        medicine: Medicine,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val id = medicine.medicineId.ifBlank {
            medicinesRef.push().key ?: ""
        }

        if (id.isBlank()) {
            onError("Unable to generate medicine ID.")
            return
        }

        medicine.medicineId = id

        medicinesRef.child(id)
            .setValue(medicine)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to save medicine.")
            }
    }

    fun getById(
        id: String,
        onResult: (Medicine?) -> Unit,
        onError: (String) -> Unit
    ) {

        medicinesRef.child(id)
            .get()
            .addOnSuccessListener { snapshot ->

                val medicine = snapshot.getValue(Medicine::class.java)

                if (medicine != null) {
                    medicine.medicineId = id
                }

                onResult(medicine)
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to retrieve medicine.")
            }
    }

    fun delete(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        medicinesRef.child(id)
            .removeValue()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to delete medicine.")
            }
    }
}
