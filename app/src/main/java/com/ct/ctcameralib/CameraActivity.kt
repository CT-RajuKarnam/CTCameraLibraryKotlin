package com.ct.ctcameralib

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ct.ctcameralib.databinding.ActivityCameraBinding
import com.ct.mycameralibray.CameraFragment
import com.ct.mycameralibray.ImageTags

class CameraActivity : AppCompatActivity(), CameraFragment.CamImages {

    var imageCount: Int = 0
    var imagesList: ArrayList<ImageTags> = ArrayList()
    lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            imageCount = savedInstanceState.getInt("position")
            imagesList = savedInstanceState.getSerializable("images_list") as ArrayList<ImageTags>

            if (imagesList[imageCount].imgOrientation.equals("P")) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

        }
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val camFrag = CameraFragment;
        camFrag.setCamImages(this)



        if (null != intent) {

            if (savedInstanceState == null) {
                imageCount = intent.getIntExtra("position", 0)
                imagesList = intent?.getSerializableExtra("images_list") as ArrayList<ImageTags>
                if (imagesList[imageCount].imgOrientation.equals("P")) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imageCount = savedInstanceState.getInt("position")
        imagesList = savedInstanceState.getSerializable("images_list") as ArrayList<ImageTags>

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("position", imageCount!!)
        outState.putSerializable("images_list", imagesList)

        Log.e("onsave", "onsave")
        super.onSaveInstanceState(outState)
    }

    override fun myCamImages(myCameraImages: ArrayList<ImageTags>, pos: Int) {
        Log.e("@@@@@", "Camera Start Again")
        Log.e("@@@@@", "Camera images" + myCameraImages)
        Log.e("@@@@@", "Camera Pos" + pos)
        if (myCameraImages[pos].imgOrientation.equals("P")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        imageCount = pos;
        imagesList = myCameraImages

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = CameraFragment()
        val bundle = Bundle()
        bundle.putSerializable("images_list", myCameraImages)
        bundle.putInt("position", pos)
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()

    }


}