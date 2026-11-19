
package com.example.communitymedicineinventory

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateStaffActivity : AppCompatActivity() {

    private lateinit var staffNameInput: EditText
    private lateinit var staffEmailInput: EditText
    private lateinit var staffPasswordInput: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var createStaffAccountButton: Button
    private lateinit var cancelCreateStaffButton: Button

    private lateinit var secondaryAuth: FirebaseAuth

    // Use the SAME database URL as LoginActivity
    private val databaseUrl =
        "https://communitymedicineinventory-default-rtdb.asia-southeast1.firebasedatabase.app"

    private val database by lazy {
        FirebaseDatabase.getInstance(databaseUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_staff)

        staffNameInput = findViewById(R.id.staffNameInput)
        staffEmailInput = findViewById(R.id.staffEmailInput)
        staffPasswordInput = findViewById(R.id.staffPasswordInput)
        roleSpinner = findViewById(R.id.roleSpinner)
        createStaffAccountButton =
            findViewById(R.id.createStaffAccountButton)
        cancelCreateStaffButton =
            findViewById(R.id.cancelCreateStaffButton)

        val roles = listOf(
            "Inventory Custodian",
            "Health Worker"
        )

        roleSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        // Initialize a separate Firebase Authentication instance
        val secondaryApp = FirebaseApp.getApps(this)
            .firstOrNull { it.name == "StaffCreation" }
            ?: FirebaseApp.initializeApp(
                this,
                FirebaseApp.getInstance().options,
                "StaffCreation"
            )

        if (secondaryApp == null) {
            Toast.makeText(
                this,
                "Unable to initialize Firebase.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

        createStaffAccountButton.setOnClickListener {
            createStaffAccount()
        }

        cancelCreateStaffButton.setOnClickListener {
            finish()
        }
    }

    private fun createStaffAccount() {

        val name = staffNameInput.text.toString().trim()
        val email = staffEmailInput.text.toString().trim()
        val password = staffPasswordInput.text.toString()
        val selectedRole = roleSpinner.selectedItem.toString()

        if (name.isEmpty()) {
            staffNameInput.error = "Enter staff name"
            return
        }

        if (email.isEmpty()) {
            staffEmailInput.error = "Enter email address"
            return
        }

        if (password.isEmpty()) {
            staffPasswordInput.error = "Enter temporary password"
            return
        }

        if (password.length < 8) {
            staffPasswordInput.error =
                "Password must be at least 8 characters"
            return
        }

        val role = when (selectedRole) {
            "Inventory Custodian" -> "inventory_custodian"
            "Health Worker" -> "health_worker"
            else -> {
                showError("Please select a valid staff role.")
                return
            }
        }

        val adminUid = FirebaseAuth.getInstance()
            .currentUser?.uid

        if (adminUid.isNullOrBlank()) {
            showError("Admin session not found. Please log in again.")
            return
        }

        createStaffAccountButton.isEnabled = false
        createStaffAccountButton.text = "CREATING ACCOUNT..."

        secondaryAuth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener { result ->

            val uid = result.user?.uid

            if (uid.isNullOrBlank()) {
                secondaryAuth.signOut()
                showError("Could not retrieve staff account ID.")
                return@addOnSuccessListener
            }

            createStaffAccountButton.text = "SAVING STAFF PROFILE..."

            val staffData = mapOf(
                "name" to name,
                "email" to email,
                "role" to role,
                "createdBy" to adminUid,
                "createdAt" to System.currentTimeMillis()
            )

            // Explicitly use the same database as LoginActivity
            database.getReference("users")
                .child(uid)
                .setValue(staffData)
                .addOnSuccessListener {

                    secondaryAuth.signOut()

                    Toast.makeText(
                        this,
                        "Staff account created successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
                .addOnFailureListener { error ->

                    // Attempt to remove the incomplete Auth account
                    result.user?.delete()

                    secondaryAuth.signOut()

                    showError(
                        "Account created, but profile saving failed: " +
                                (error.message ?: "Unknown database error")
                    )
                }

        }.addOnFailureListener { error ->

            secondaryAuth.signOut()

            showError(
                error.message ?: "Failed to create staff account."
            )
        }
    }

    private fun showError(message: String) {

        createStaffAccountButton.isEnabled = true
        createStaffAccountButton.text = "CREATE STAFF ACCOUNT"

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
