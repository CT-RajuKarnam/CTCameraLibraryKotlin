package com.ct.ctcameralib

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.ct.ctcameralib.databinding.ActivityImagesBinding
import com.ct.ctcameralib.databinding.LayoutImageTagsBinding
import com.ct.mycameralibray.ImageTags
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ImagesActivity : AppCompatActivity() {

    lateinit var binding: ActivityImagesBinding
    var imagesList: ArrayList<ImageTags>? = ArrayList()
    val TAG: String = "ImagesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras != null) {
            imagesList?.clear()
            imagesList = intent.extras!!.getSerializable("images_list") as ArrayList<ImageTags>
            if (imagesList != null && imagesList!!.size > 0) {
                binding.lvImgTags.adapter = Pictures(imagesList)
            }
        } else {
            binding.lvImgTags.adapter = Pictures(imagesList)
            checkcamera()
        }


    }

    @SuppressLint("ViewHolder")
    inner class Pictures(var list: ArrayList<ImageTags>?) :
        RecyclerView.Adapter<Pictures.MyViewHolder>() {

        var bitmap: Bitmap? = null

        inner class MyViewHolder(bindingView: LayoutImageTagsBinding) :
            RecyclerView.ViewHolder(bindingView.root) {
            var imageName: TextView = bindingView.tvPicture
            var btnSelectImage: ImageView = bindingView.btnGallery
            var captureImage: AppCompatImageView = bindingView.ivPicture
            var btnEdit: ImageView = bindingView.btnCrop
            var btnDelete: ImageView = bindingView.btnDelete

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pictures.MyViewHolder {
            val view = LayoutImageTagsBinding.inflate(
                LayoutInflater.from(this@ImagesActivity),
                parent,
                false
            )
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: Pictures.MyViewHolder, position: Int) {
            holder.imageName.text = list!![position].imgName

            if(!imagesList!![position].imgPath.isNullOrEmpty()) {
                Log.e(TAG, "abcd" + imagesList!![position].imgPath.toString())
                val uri = Uri.parse(imagesList!![position].imgPath)
                Log.e("!!!!!",imagesList!![position].imgPath)
                Log.e("@@@@",uri.toString())
                holder.captureImage.setImageURI(uri)
                holder.captureImage.visibility = View.VISIBLE
                holder.btnSelectImage.visibility = View.GONE
            }

            Log.e(TAG, "onBindViewHolder: $list")

            holder.btnSelectImage.setOnClickListener {

                val intent = Intent(this@ImagesActivity, CameraActivity::class.java)
//                val fragment = CameraFragment()
//                val bundle = Bundle()
                intent.putExtra("images_list", imagesList)
                intent.putExtra("position", position)
//                fragment.arguments = bundle
                startActivity(intent)
//                finish()
            }
            /*holder.captureImage.setImageBitmap(
                Bitmap.createScaledBitmap(
                    bitmap!!, 80, 80, true
                )
            )*/
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }
    }

    fun loadJSONFromAsset(): String? {
        val json: String = try {
            val `is` = this.assets.open("camera.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }


    private fun checkcamera() {
        var `object`: JSONObject? = null
        try {
            `object` = JSONObject(loadJSONFromAsset())
            val images = `object`.getJSONArray("CamImages")
            imagesList?.clear()
            for (i in 0 until images.length()) {
                val rec = images.getJSONObject(i)
                imagesList?.add(Gson().fromJson(images[i].toString(), ImageTags::class.java))
            }
            binding.lvImgTags.adapter?.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}