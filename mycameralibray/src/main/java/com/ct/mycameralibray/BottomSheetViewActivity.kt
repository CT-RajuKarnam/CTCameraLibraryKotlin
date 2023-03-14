package com.ct.mycameralibray

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ct.mycameralibray.MyListener

class BottomSheetViewActivity : AppCompatActivity(),MyListener {

    var myListener: MyListener? = null
    var imgOrientation:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet_view)

        myListener = this

        val bottomSheet = CameraSettingsBottomSheet(myListener!!,imgOrientation)
        bottomSheet.show(supportFragmentManager, "CameraBottomSheet")
    }

    override fun applyListener() {

    }
}