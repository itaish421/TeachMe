package com.example.teachme

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.Toast
import com.example.teachme.models.TeacherDetails
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

data object Dialogs {


    fun openPasswordResetDialog(context: Context) {

        val view = LayoutInflater.from(context).inflate(R.layout.forgot_pass, null, false)

        val etEmail = view.findViewById<TextInputEditText>(R.id.emailAddressEt)
        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Send link") { _, _ ->
                val email = etEmail.text.toString()
                if (email.isEmpty()) {
                    Toast.makeText(context, "Email empty", Toast.LENGTH_LONG).show()
                } else {
                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Email sent, check your email box and spam box",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    fun openTeacherExtraDetailsDialog(
        context: Context,
        onDetailsSelected: (TeacherDetails) -> Unit,
    ): AlertDialog {
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

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Ok") { _, _ ->
                if (etPrice.text.toString().isEmpty()
                    || etDesc.text.toString().isEmpty()
                    || subjects.isEmpty()
                ) {
                    Toast.makeText(
                        context,
                        "Please fill all the fields and choose at-least 1 subject",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }
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
            .create()
        dialog.show()
        return dialog
    }
}