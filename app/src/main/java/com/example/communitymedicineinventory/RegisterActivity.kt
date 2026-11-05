package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullNameInput = findViewById<EditText>(R.id.fullNameInput)
        val emailInput = findViewById<EditText>(R.id.registerEmailInput)
        val passwordInput = findViewById<EditText>(R.id.registerPasswordInput)
        val confirmPasswordInput =
            findViewById<EditText>(R.id.confirmPasswordInput)

        val createAccountButton =
            findViewById<Button>(R.id.createAccountButton)

        val backButton =
            findViewById<Button>(R.id.backToLoginButton)

        createAccountButton.setOnClickListener {
            val name = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            when {
                name.isEmpty() || email.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Please complete all fields.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Enter a valid email address"
                }

                password.length < 6 -> {
                    passwordInput.error =
                        "Password must be at least 6 characters"
                }

                password != confirmPassword -> {
                    confirmPasswordInput.error = "Passwords do not match"
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Registration form completed. Account saving is not enabled yet.",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                }
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}