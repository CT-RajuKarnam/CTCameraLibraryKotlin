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
import com.ct.mycameralibray.CamPref
import com.ct.mycameralibray.CameraFragment
import com.ct.mycameralibray.ImageTags
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ImagesActivity : AppCompatActivity(), CameraFragment.CamListImages {

    lateinit var binding: ActivityImagesBinding
    var imagesList: ArrayList<ImageTags>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val camFrag = CameraFragment
        camFrag.setCamListImages(this)
        binding.lvImgTags.adapter = Pictures(imagesList)
        loadData()

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
            if (!imagesList!![position].imgPath.isNullOrEmpty()) {
                val uri = Uri.parse(imagesList!![position].imgPath)
                holder.captureImage.setImageURI(uri)
                holder.captureImage.visibility = View.VISIBLE
                holder.btnSelectImage.visibility = View.GONE
            }
            holder.btnSelectImage.setOnClickListener {
                val intent = Intent(this@ImagesActivity, CameraActivity::class.java)
                intent.putExtra("images_list", imagesList)
                intent.putExtra("position", position)
                startActivity(intent)
            }

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


    private fun loadData() {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(loadJSONFromAsset())
            val images = jsonObject.getJSONArray("CamImages")
            imagesList?.clear()
            for (i in 0 until images.length()) {
                imagesList?.add(Gson().fromJson(images[i].toString(), ImageTags::class.java))
            }
            binding.lvImgTags.adapter?.notifyDataSetChanged()

            val jObj = jsonObject.getJSONObject("CamSettings")
            CamPref.getIn(this).isCamShowWaterMark = jObj.getBoolean("camShowWaterMark")
            CamPref.getIn(this).camShowWaterMarkAt = jObj.getString("camShowWaterMarkAt")
            CamPref.getIn(this).camWaterMarkUrl = jObj.getString("camWaterMarkUrl")
            CamPref.getIn(this).isCamShowAddress = jObj.getBoolean("camShowAddress")
            CamPref.getIn(this).isCamShowLatLong = jObj.getBoolean("camShowLatLong")
            CamPref.getIn(this).isCamShowOverlayImg = jObj.getBoolean("camShowOverlayImg")
            CamPref.getIn(this).isCamShowTime = jObj.getBoolean("camShowTime")
            CamPref.getIn(this).isCamShowLabelName = jObj.getBoolean("camShowLabelName")
            CamPref.getIn(this).isCamShowGuidBox = jObj.getBoolean("camShowGuidBox")
            CamPref.getIn(this).isCamShowGuidLines = jObj.getBoolean("camShowGuidLines")
            CamPref.getIn(this).camShowTextAt = jObj.getString("camShowTextAt")
            CamPref.getIn(this).camFlashMode = jObj.getString("camFlashMode")
            CamPref.getIn(this).camAspectRatio = jObj.getString("camAspectRatio")
            CamPref.getIn(this).isCamSavePicExternal = jObj.getBoolean("camSavePicExternal")
            CamPref.getIn(this).camFolderName = jObj.getString("camFolderName")
            CamPref.getIn(this).camOriRegixL = jObj.getString("camOriRegixL")
            CamPref.getIn(this).camOriRegixP = jObj.getString("camOriRegixP")
        } catch (e: JSONException) {
            e.printStackTrace()
        }




}


    override fun myCamListImages(myCameraImages: ArrayList<ImageTags>) {
        imagesList?.clear()
        imagesList = myCameraImages
        binding.lvImgTags.adapter = Pictures(imagesList)
        Log.e("####", "Camera Images@" + imagesList!!.get(0).imgPath)
        Log.e("####", "Camera Images#" + imagesList!!.get(0).imgName)
    }

}