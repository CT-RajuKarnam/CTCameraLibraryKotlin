package com.ct.mycameralibray

import android.content.Context
import android.widget.Toast

public class ToasterMessage {
    fun s(c: Context?, message: String?) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    }
}
