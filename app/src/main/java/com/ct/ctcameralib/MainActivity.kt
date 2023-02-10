package com.ct.ctcameralib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ct.mycameralibray.ToasterMessage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textview)
        textView.setOnClickListener {
            ToasterMessage().s(this,"message")
        }
    }
}