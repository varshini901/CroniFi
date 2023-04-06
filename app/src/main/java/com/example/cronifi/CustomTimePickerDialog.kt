package com.example.cronifi

import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.NumberPicker

class CustomTimePickerDialog (
    context: Context,
    private val onTimeSetListener: TimePickerDialog.OnTimeSetListener,
    private val hourOfDay: Int,
    private val minute: Int,
) : TimePickerDialog(context, TimePickerDialog.THEME_HOLO_LIGHT,onTimeSetListener, hourOfDay, minute,true) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val minuteSpinner = findViewById<View>(Resources.getSystem().getIdentifier("minute", "id", "android")) as? NumberPicker
        minuteSpinner?.apply {
            minValue = 0
            maxValue = 3
            displayedValues = arrayOf("00", "15", "30", "45")

        }
    }

}