package com.ct.ctcameralib

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ct.ctcameralib.databinding.ActivityZoomImageBinding
import com.ct.mycameralibray.ImageTags
import kotlin.math.max
import kotlin.math.min


class ZoomImageActivity : AppCompatActivity() {

    lateinit var binding: ActivityZoomImageBinding

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    var imageCount = 0
    var imagesList: ArrayList<ImageTags> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagesList = intent.getSerializableExtra("images_list") as ArrayList<ImageTags>
        imageCount = intent.getIntExtra("position", 0)

        Glide.with(this).load(imagesList[imageCount].imgPath).into(binding.imageView)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            binding.imageView.scaleX = scaleFactor
            binding.imageView.scaleY = scaleFactor
            return true
        }
    }

}