
package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var backButton: Button

    private lateinit var auth: FirebaseAuth

    private val handler = Handler(Looper.getMainLooper())
    private var loginCompleted = false

    // Your Firebase Realtime Database URL
    private val databaseUrl =
        "https://communitymedicineinventory-default-rtdb.asia-southeast1.firebasedatabase.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        backButton = findViewById(R.id.backButton)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            loginUser()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loginUser() {

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty()) {
            emailInput.error = "Enter your email"
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Enter your password"
            return
        }

        loginCompleted = false

        loginButton.isEnabled = false
        loginButton.text = "AUTHENTICATING..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser?.uid

                if (uid == null) {
                    auth.signOut()
                    showLoginError("Unable to identify account.")
                    return@addOnSuccessListener
                }

                Log.d("LOGIN_DEBUG", "Logged-in UID: $uid")

                getUserRole(uid)
            }
            .addOnFailureListener { error ->

                showLoginError(
                    error.message ?: "Login failed."
                )
            }
    }

    private fun getUserRole(uid: String) {

        loginButton.text = "CHECKING ACCOUNT..."

        val database = FirebaseDatabase.getInstance(databaseUrl)

        val userRef = database.getReference("users")
            .child(uid)

        val timeout = Runnable {

            if (!loginCompleted) {

                loginCompleted = true

                auth.signOut()

                showLoginError(
                    "Database connection timed out. Check your internet and Firebase settings."
                )
            }
        }

        handler.postDelayed(timeout, 15000)

        userRef.get()
            .addOnSuccessListener { snapshot ->

                if (loginCompleted) {
                    return@addOnSuccessListener
                }

                loginCompleted = true
                handler.removeCallbacks(timeout)

                if (!snapshot.exists()) {

                    auth.signOut()

                    showLoginError(
                        "User profile not found. Check your Firebase users folder and UID."
                    )

                    return@addOnSuccessListener
                }

                val role = snapshot.child("role")
                    .getValue(String::class.java)
                    ?.trim()
                    ?.lowercase()

                Log.d("LOGIN_DEBUG", "User profile: ${snapshot.value}")
                Log.d("LOGIN_DEBUG", "Account role: $role")

                when (role) {

                    "admin" -> {
                        openAdminDashboard()
                    }

                    "inventory_custodian" -> {
                        openRoleDashboard("inventory_custodian")
                    }

                    "health_worker" -> {
                        openRoleDashboard("health_worker")
                    }

                    null, "" -> {

                        auth.signOut()

                        showLoginError(
                            "Role is missing from your Firebase user profile."
                        )
                    }

                    else -> {

                        auth.signOut()

                        showLoginError(
                            "Unknown account role: $role"
                        )
                    }
                }
            }
            .addOnFailureListener { error ->

                if (loginCompleted) {
                    return@addOnFailureListener
                }

                loginCompleted = true
                handler.removeCallbacks(timeout)

                auth.signOut()

                showLoginError(
                    "Database error: ${error.message}"
                )
            }
    }

    // Admin dashboard
    private fun openAdminDashboard() {

        val intent = Intent(
            this,
            AdminDashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    // Inventory Custodian and Health Worker dashboard
    private fun openRoleDashboard(role: String) {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        intent.putExtra("USER_ROLE", role)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun showLoginError(message: String) {

        loginButton.isEnabled = true
        loginButton.text = "LOGIN"

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {

        handler.removeCallbacksAndMessages(null)

        super.onDestroy()
    }
}
