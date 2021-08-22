package com.example.blogapp.util

import android.widget.EditText

open class Validate {

    companion object {

        fun inputValidate(input: EditText): Boolean {
            if (input.text.toString().isEmpty()) {

                return false
            }
            return true
        }
    }

}
