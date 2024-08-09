package com.example.teachme

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.GridLayout
import com.example.teachme.models.TeacherDetails
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

data object Dialogs {


    fun openTeacherExtraDetailsDialog(
        context: Context,
        onDetailsSelected: (TeacherDetails) -> Unit,
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.teacher_details, null, false)


        val etPrice = view.findViewById<TextInputEditText>(R.id.etPrice)
        val etPriceLayout = view.findViewById<TextInputLayout>(R.id.etPriceLayout)

        val etDesc = view.findViewById<TextInputEditText>(R.id.etDescription)
        val etDescLayout = view.findViewById<TextInputLayout>(R.id.etDescriptionLayout)

        val subjectsGrid = view.findViewById<GridLayout>(R.id.subjectsGrid)

        subjectsGrid.columnCount = 2
        subjectsGrid.rowCount = Constants.SUBJECTS.size / 2

        val subjects = mutableSetOf<String>()//

        Constants.SUBJECTS.forEach { subject ->
            val checkBox = CheckBox(context)
            checkBox.text = subject
            checkBox.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    subjects.add(subject)
                }
            }
            subjectsGrid.addView(checkBox)
        }

        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Ok") { _, _ ->
                val price = etPrice.text.toString().toDouble()
                val desc = etDesc.text.toString()
                val subs = subjects.toList()
                onDetailsSelected.invoke(
                    TeacherDetails(
                        pricePerHour = price,
                        description = desc,
                        subjects = subs
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .show()

    }
}