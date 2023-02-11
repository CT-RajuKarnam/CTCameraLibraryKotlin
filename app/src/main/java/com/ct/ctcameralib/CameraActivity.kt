package com.ct.ctcameralib

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ct.ctcameralib.databinding.ActivityCameraBinding
import com.ct.mycameralibray.ImageTags

class CameraActivity : AppCompatActivity(), CameraFragment.CamImages {

   /* var imageCount = 0
    private var imagesList: ArrayList<ImageTags> = ArrayList()
*/
    lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val camFrag = CameraFragment;
        camFrag.setCamImages(this)
        if (null != intent) {
            val imageCount = intent.getIntExtra("position", 0)
            val imagesList = intent?.getSerializableExtra("images_list") as ArrayList<ImageTags>
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

    override fun myCamImages(myCameraImages: ArrayList<ImageTags>, pos: Int) {
        Log.e("@@@@@","Camera Start Again")
        Log.e("@@@@@","Camera images"+myCameraImages)
        Log.e("@@@@@","Camera Pos"+pos)
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