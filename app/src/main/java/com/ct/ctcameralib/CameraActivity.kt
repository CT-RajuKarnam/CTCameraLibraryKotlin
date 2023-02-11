package com.ct.ctcameralib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ct.ctcameralib.databinding.ActivityCameraBinding
import com.ct.mycameralibray.ImageTags

class CameraActivity : AppCompatActivity() {

    private var extras: Bundle? = null
    var imageCount = 0
    private var imagesList: ArrayList<ImageTags> = ArrayList()

    lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        extras = intent.extras
        imageCount = intent.getIntExtra("position", 0)
        imagesList = intent?.getSerializableExtra("images_list") as ArrayList<ImageTags>

        Log.e("imageCount" , "@@@@$imageCount")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = CameraFragment()
        val bundle = Bundle()
        bundle.putSerializable("images_list", imagesList)
        bundle.putInt("position", imageCount)
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }

    

}