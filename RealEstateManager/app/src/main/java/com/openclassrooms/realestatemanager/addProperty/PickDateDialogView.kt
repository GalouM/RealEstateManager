package com.openclassrooms.realestatemanager.addProperty


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.DialogFragment

import java.util.*
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.util.Log
import android.widget.DatePicker
import com.openclassrooms.realestatemanager.R

/**
 * A simple [Fragment] subclass.
 *
 */
class PickDateDialogView(private val dateViewId: View, private var existingDate: Calendar?) :
        DialogFragment(), DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener {

    interface OnOkButtonListener{
        fun onOkButtonListener(calendar: Calendar, view: View)
    }

    private lateinit var callback: OnOkButtonListener

    private var pickedDate: Calendar = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCallbackToParent()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if(existingDate == null){
            existingDate = Calendar.getInstance()
        }
        val year = existingDate!!.get(Calendar.YEAR)
        val month = existingDate!!.get(Calendar.MONTH)
        val day = existingDate!!.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(activity, this, year, month, day)
        datePickerDialog.setButton(BUTTON_POSITIVE, getString(R.string.ok_button), this)
        datePickerDialog.setButton(BUTTON_NEGATIVE, getString(R.string.cancel_button), this)

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog
    }

    private fun createCallbackToParent() {
        try {
            callback = targetFragment as OnOkButtonListener
        } catch (e: ClassCastException) {
            throw ClassCastException(e.toString() + "must implement OnOKButtonListener")
        }

    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        if(button == BUTTON_POSITIVE){
            Log.e("ok button", pickedDate.toString())
            setDate()
            callback.onOkButtonListener(pickedDate, dateViewId)
        }

        if(button == BUTTON_NEGATIVE){
            dialog?.cancel()
        }
    }

    private fun setDate(){
        val month = datePickerDialog.datePicker.month
        val day = datePickerDialog.datePicker.dayOfMonth
        val year = datePickerDialog.datePicker.year
        pickedDate.set(year, month, day)
    }

    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {}

}
