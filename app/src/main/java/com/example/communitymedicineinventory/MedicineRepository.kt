package com.example.communitymedicineinventory

object MedicineRepository {

    private val medicines = mutableListOf(
        Medicine(1, "Paracetamol", "Pain Reliever", 120, 30, "2027-08-15", "tablets"),
        Medicine(2, "Amoxicillin", "Antibiotic", 20, 25, "2027-05-20", "capsules"),
        Medicine(3, "Cetirizine", "Antihistamine", 60, 15, "2027-11-10", "tablets")
    )

    fun getAll(): List<Medicine> {
        return medicines.toList()
    }

    fun getById(id: Int): Medicine? {
        return medicines.find { it.medicineId == id }
    }

    fun getLowStock(): List<Medicine> {
        return medicines.filter { it.isLowStock() }
    }

    fun nextId(): Int {
        return (medicines.maxOfOrNull { it.medicineId } ?: 0) + 1
    }

    fun save(medicine: Medicine) {
        val index = medicines.indexOfFirst {
            it.medicineId == medicine.medicineId
        }

        if (index >= 0) {
            medicines[index] = medicine
        } else {
            medicines.add(medicine)
        }
    }

    fun totalMedicines(): Int {
        return medicines.size
    }

    fun lowStockCount(): Int {
        return getLowStock().size
    }
}