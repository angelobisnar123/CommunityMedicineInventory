
package com.example.communitymedicineinventory

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

object UiHelper {

    fun screen(context: Context): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.WHITE)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun title(context: Context, value: String): TextView {
        return TextView(context).apply {
            text = value
            textSize = 24f
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 8, 0, 20)
        }
    }

    fun text(context: Context, value: String): TextView {
        return TextView(context).apply {
            text = value
            textSize = 16f
            setTextColor(Color.BLACK)
            setPadding(0, 8, 0, 8)
        }
    }

    fun input(context: Context, hintText: String): EditText {
        return EditText(context).apply {
            hint = hintText
            textSize = 16f
            setTextColor(Color.BLACK)
            setHintTextColor(Color.GRAY)

            // Correct single-line configuration
            setSingleLine(true)

            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                setStroke(1, Color.GRAY)
                cornerRadius = 8f
            }

            setPadding(12, 8, 12, 8)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }
    }

    fun button(
        context: Context,
        label: String,
        action: () -> Unit
    ): Button {
        return Button(context).apply {
            text = label
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.BLACK)

            setOnClickListener {
                action()
            }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 10
            }
        }
    }

    fun space(context: Context, height: Int = 12): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                height
            )
        }
    }

    // Required by AddEditMedicineActivity and StockAlertsActivity
    fun scrollable(
        context: Context,
        content: View
    ): ScrollView {
        return ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            isFillViewport = true
            addView(content)
        }
    }
}
