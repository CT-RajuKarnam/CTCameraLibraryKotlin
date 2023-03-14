package com.ct.mycameralibray

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ct.mycameralibray.*
import com.ct.mycameralibray.databinding.FragmentCameraBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class CameraFragment : Fragment(), SensorEventListener, MyListener {

    lateinit var binding: FragmentCameraBinding
    var twoDecimalForm = DecimalFormat("#.######")


    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var camera: Camera? = null
    private var imagesPath = ""
    private var oldBitmap: Bitmap? = null
    private var oldUri: Uri? = null
    private var imagesList: ArrayList<ImageTags> = ArrayList()
    var imageCount = 0
    var shutterFlag = false
    var x = 0
    var y = 0
    var z = 0
    private var mLastClickTime: Long = 0
    private var toastAngle: Int = 0
    var cameraSelector: CameraSelector? = null

    var accelerometer: Sensor? = null
    var sm: SensorManager? = null

    var myListener: MyListener? = null

    var appLocationService: AppLocationService? = null


    companion object Ratio {
        var camImages: CamImages? = null
        var camListImages: CamListImages? = null

        @JvmName("setCamImages1")
        fun setCamImages(cameraImages: CamImages) {
            camImages = cameraImages
        }

        @JvmName("setCamListImages1")
        fun setCamListImages(cameraListImages: CamListImages) {
            camListImages = cameraListImages
        }

        var ASPECT_RATIO: Double = 0.0

    }

    interface CamListImages {
        fun myCamListImages(myCameraImages: ArrayList<ImageTags>)
    }

    interface CamImages {
        fun myCamImages(myCameraImages: ArrayList<ImageTags>, pos: Int)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        imageCount = arguments?.getInt("position")!!
        imagesList = arguments?.getSerializable("images_list") as ArrayList<ImageTags>
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appLocationService = AppLocationService(binding.root.context)
        myListener = this

        //Aspect Ratio
        if (imagesList[imageCount].imgOrientation.equals("P")) {
            CamPref.getIn(binding.root.context).orientationFlag = false
            ASPECT_RATIO = 3.0 / 4.0
        } else {
            CamPref.getIn(binding.root.context).orientationFlag = true
            ASPECT_RATIO = 4.0 / 3.0
        }

        //enable permissions for camera
        if (allPermissionsGranted()) {
            startSensor()
            Log.e("####","Start Camera @L ")
            startCamera()
        } else {
            startSensor()
            startCamera()
            ActivityCompat.requestPermissions(
                requireActivity(),
                mutableListOf(Manifest.permission.CAMERA).apply {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }.toTypedArray(),
                1001
            )
        }

        binding.imgSettings.setOnClickListener {
            val bottomSheet = imagesList[imageCount].imgOrientation?.let { it1 ->
                CameraSettingsBottomSheet(myListener!!,
                    it1
                )
            }
            bottomSheet?.show(requireActivity().supportFragmentManager, "CameraBottomSheet")
        }

        //capture image
        binding.captureImg.setOnClickListener {

            shutterFlag = if (CamPref.getIn(binding.root.context)
                    .orientationFlag
            ) { //(x>=7 and (z<=5 and z>-1) (y>-3 and y<4)//land scape
                x > 6 && z > -1 && z < 6 && y > -3 && y < 4
            } else { //portait for take paper docs
                x < 4 && x > -4 && y >= 0 && z > -1  && z<3
            }

            if (shutterFlag) {
                binding.titleName.visibility = View.GONE
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                try {
                    val bitmap: Bitmap = binding.cameraView.bitmap!!

                    /*by ram*/
                    val fl_view = binding.flView
                    //binding.txtTimeStamp.gravity = Gravity.LEFT
                    val bitmapNew: Bitmap = copyBitmap(bitmap)
                    val canvas = Canvas(bitmapNew)
                    canvas.drawARGB(0, 0, 0, 0)
                    fl_view.isDrawingCacheEnabled = true
                    fl_view.measure(
                        View.MeasureSpec.makeMeasureSpec(canvas.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(canvas.height, View.MeasureSpec.EXACTLY)
                    )
                    fl_view.layout(0, 0, fl_view.measuredWidth, fl_view.measuredHeight)
                    fl_view.draw(canvas)


                    /*end by ram*/

                    binding.selfie.setImageBitmap(bitmapNew)

                    binding.selfie.visibility = View.VISIBLE
                    cameraExecutor.shutdown()
                    binding.flView.visibility = View.GONE
                    binding.flViewHide.visibility = View.GONE
                    binding.cameraView.visibility = View.GONE
                    binding.titleName.visibility = View.GONE
                    binding.captureLayout.visibility = View.GONE

                    camera?.cameraControl?.enableTorch(false)
                    if (bitmap != null) {
                        Handler(Looper.myLooper()!!).postDelayed({
                            captureView(binding.constraintLayout, requireActivity().window) {
                                storeImage(it)
                                oldBitmap = it
                            }
                        }, 500)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                if (CamPref.getIn(binding.root.context).orientationFlag) {
                    showToast("Please capture photo in landscape only", toastAngle)
                } else {
                    showToast("Please capture photo in portrait only", toastAngle)

                }
            }
        }

        binding.btnRetake.setOnClickListener {
            binding.linearLayout.visibility = View.GONE
            binding.cameraView.visibility = View.VISIBLE
            binding.captureLayout.visibility = View.VISIBLE
            binding.flView.visibility = View.VISIBLE
            binding.flViewHide.visibility = View.VISIBLE
            binding.selfie.visibility = View.GONE
            oldUri = null
            startCamera()
        }

        binding.btnSaveAndClose.setOnClickListener {
            Log.e("TAG", "getFilePath: ${oldUri?.path}")

            imagesList[imageCount].imgPath = oldUri?.path
            try {
                camListImages?.myCamListImages(imagesList)
                requireActivity().finish()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (imageCount == doneClose()) {
            binding.btnSaveAndNext.text = "DONE & CLOSE"
        }

        if (imageCount == doneClose()) {
            binding.titleName.visibility = View.GONE
            binding.btnSaveAndNext.setOnClickListener {
                Log.d("TAG", "getFilePath: ${oldUri?.path}")
                imagesList[imageCount].imgPath = oldUri?.path
                try {
                    Log.e("#####", "onCreate: $$$$")
                    printPaths()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            binding.btnSaveAndNext.setOnClickListener {
                Log.d("TAG", "getFilePath1: ${oldUri?.path}")
                imagesList[imageCount].imgPath = oldUri?.path
                try {
                    camImages?.myCamImages(imagesList, skipImages())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                oldUri = null
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        /*by ram for timer*/
        val t1 = Timer("frame", true)
        t1.schedule(object : TimerTask() {
            override fun run() {
                binding.txtTimeStamp.post(Runnable {
                    if (binding.txtTimeStamp != null) {
                        var desc = ""
                        if (CamPref.getIn(binding.root.context).isCamShowTime) {
                            desc = DateFormat.format("dd-MM-yyyy HH:mm:ss", Date()).toString()
                        }

                        if (CamPref.getIn(binding.root.context).isCamShowLatLong) {
                            desc =
                                desc + "\nLat: " + twoDecimalForm.format(appLocationService!!.getLatitude()) + ", Lng:" + twoDecimalForm.format(
                                    appLocationService!!.getLongitude()
                                )
                        }
                        if (CamPref.getIn(binding.root.context).isCamShowAddress) {
                            desc = desc + "\nAddress: " + appLocationService!!.getAddress()
                        }
                        binding.txtTimeStamp.text = desc
                    }
                })
            }
        }, 1000, 1000)

    }

    private fun allPermissionsGranted() = mutableListOf(Manifest.permission.CAMERA).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray().all {
        ContextCompat.checkSelfPermission(
            binding.root.context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(binding.root.context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            /*val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build()
                .also { it.setSurfaceProvider(binding.cameraView.surfaceProvider) }*/
            var preview: Preview? = null;
            if(CamPref.getIn(context).camAspectRatio.equals("3:4")){
                preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build()
                    .also { it.setSurfaceProvider(binding.cameraView.surfaceProvider) }
            }else{
                preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
                    .also { it.setSurfaceProvider(binding.cameraView.surfaceProvider) }
            }

            imageCapture = ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()

            /*val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                    Log.d("TAG", "startCamera: $luma")
                })
            }
*/
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            /*imageAnalysis.setAnalyzer(cameraExecutor, object : ImageAnalysis.Analyzer {
                override fun analyze(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val pixels = IntArray(bitmap.width * bitmap.height)
                    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                    var sum = 0
                    for (pixel in pixels) {
                        sum += Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)
                    }
                    val avgPixelValue = sum / (3 * pixels.size)
                    Log.d("analysis", "Average pixel value: $avgPixelValue")
                    image.close()
                }
            })*/


            //front camera and flash off
            if (imagesList[imageCount].imgName!!.contains("Selfie")) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//                CamPref.getIn(binding.root.context).camSwitch = "front"
                if (camera?.cameraInfo?.hasFlashUnit() == true) {
                    binding.btnFlash.visibility = View.VISIBLE
                } else {
                    binding.btnFlash.visibility = View.INVISIBLE
                }
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//                CamPref.getIn(binding.root.context).camSwitch = "back"
            }
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector!!,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(binding.root.context))


        //rotateCamera
       /* binding.rotateCameraImg.setOnClickListener {
            if(CamPref.getIn(binding.root.context).camSwitch == "front"){
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                CamPref.getIn(binding.root.context).camSwitch = "back"
            } else if(CamPref.getIn(binding.root.context).camSwitch == "back"){
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                CamPref.getIn(binding.root.context).camSwitch = "front"
            }
        }*/

        //flash
        binding.btnFlash.setOnClickListener {
            if (camera?.cameraInfo?.hasFlashUnit() == true) {
                binding.btnFlash.visibility = View.VISIBLE
                camera?.cameraControl?.enableTorch(camera?.cameraInfo?.torchState?.value == TorchState.OFF)
                if (camera?.cameraInfo?.torchState?.value == TorchState.OFF) {
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                    CamPref.getIn(binding.root.context).camFlashMode = "off"
                } else if (camera?.cameraInfo?.torchState?.value == TorchState.ON) {
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
                    CamPref.getIn(binding.root.context).camFlashMode = "on"
                }
            } else {
                binding.btnFlash.visibility = View.GONE
            }
        }

        //focusing the image
        binding.cameraView.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val factory = binding.cameraView.meteringPointFactory
                    val point = factory.createPoint(event.x, event.y)
                    val action = FocusMeteringAction.Builder(point).build()
                    camera?.cameraControl?.startFocusAndMetering(action)
                    true
                }
                else -> {
                    false
                }
            }
        }

        //pitch to zoom
        val scaleGestureDetector = ScaleGestureDetector(binding.root.context, listener)
        binding.cameraView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }

        //zoom seekbar
        if(imagesList[imageCount].imgOrientation.equals("P",true)) {
            binding.horizontalSeekbar?.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    camera!!.cameraControl.setLinearZoom(progress.toFloat() / seekBar!!.max)
                    val zoomRatio = progress.toFloat() / seekBar.max
                    val df = DecimalFormat("#.#")
                    var zoomval = "" + df.format(zoomRatio.toDouble())
                    zoomval = if (zoomval.length == 1) "$zoomval.0" else zoomval
                    binding.tvZoomLevel.text = "$zoomval x"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } else if(imagesList[imageCount].imgOrientation.equals("L",true)){
            binding.verticalSeekbar?.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    camera!!.cameraControl.setLinearZoom(progress.toFloat() / seekBar!!.max)
                    val zoomRatio = progress.toFloat() / seekBar.max
                    val df = DecimalFormat("#.#")
                    var zoomval = "" + df.format(zoomRatio.toDouble())
                    zoomval = if (zoomval.length == 1) "$zoomval.0" else zoomval
                    binding.tvZoomLevel.text = "$zoomval x"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

        }

        //Flash check
        if (CamPref.getIn(binding.root.context).camFlashMode == "off") {
            Handler(Looper.myLooper()!!).postDelayed({
                if (camera?.cameraInfo?.hasFlashUnit() == true) {
                    binding.btnFlash.visibility = View.VISIBLE
                    camera?.cameraControl?.enableTorch(false)
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                }
            }, 0)


        } else if (CamPref.getIn(binding.root.context).camFlashMode == "on") {
            Handler(Looper.myLooper()!!).postDelayed({
                if (camera?.cameraInfo?.hasFlashUnit() == true) {
                    binding.btnFlash.visibility = View.VISIBLE
                    camera?.cameraControl?.enableTorch(true)
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
                }

            }, 0)
        }



        if (imagesList[imageCount].imgOverlayLogo!!.isNotEmpty() && CamPref.getIn(binding.root.context).isCamShowOverlayImg) {

            Log.e("@####", "OverLy" + imagesList[imageCount].imgOverlayLogo)
            binding.imgOverlay.visibility = View.VISIBLE
            Glide.with(this)
                .load(imagesList[imageCount].imgOverlayLogo)
                .into(binding.imgOverlay)

            if(imagesList[imageCount].imgFrontCam.equals("y")){
                binding.imgOverlay.visibility = View.GONE
                binding.imgfrontOverlay?.visibility = View.VISIBLE
            }else{
                binding.imgOverlay.visibility = View.VISIBLE
                binding.imgfrontOverlay?.visibility = View.GONE
            }

        } else {
            Log.e("######", "OverLy" + imagesList[imageCount].imgOverlayLogo)
            binding.imgOverlay.visibility = View.GONE
            binding.imgfrontOverlay?.visibility = View.GONE
        }

        if (CamPref.getIn(binding.root.context).camWaterMarkUrl.isNotEmpty() && CamPref.getIn(binding.root.context).isCamShowWaterMark) {
            binding.watermarkLogo.visibility = View.VISIBLE
            Glide.with(this)
                .load(CamPref.getIn(binding.root.context).camWaterMarkUrl)
                .into(binding.watermarkLogo)
            val params = binding.watermarkLogo.layoutParams as FrameLayout.LayoutParams
            params.gravity = CamPref.getIn(binding.root.context).camShowWaterMarkAtPos
            binding.watermarkLogo.layoutParams = params
        } else {
            Log.e("######", "OverLy" + imagesList[imageCount].imgOverlayLogo)
            binding.watermarkLogo.visibility = View.GONE
        }

        /*Text overlay*/
        if (CamPref.getIn(binding.root.context).isCamShowTime || CamPref.getIn(binding.root.context).isCamShowAddress ||
            CamPref.getIn(binding.root.context).isCamShowLatLong
        ) {
            binding.txtTimeStamp.visibility = View.VISIBLE
            binding.txtTimeStamp.gravity = CamPref.getIn(binding.root.context).camDescPosition

        } else {
            binding.txtTimeStamp.visibility = View.GONE
        }

        if (CamPref.getIn(binding.root.context).isCamShowLabelName) {
            binding.txtTitle.text = imagesList[imageCount].imgName
            binding.txtTitle.visibility = View.VISIBLE

        } else {
            binding.txtTitle.visibility = View.GONE
        }

        binding.flViewHide.visibility = View.VISIBLE


        binding.captureLayout.visibility = View.VISIBLE

        Handler(Looper.myLooper()!!).postDelayed({
            val fl_params = binding.flView.getLayoutParams() as RelativeLayout.LayoutParams
            fl_params.width = binding.cameraView.measuredWidth
            fl_params.height = binding.cameraView.measuredHeight
            fl_params.addRule(RelativeLayout.CENTER_IN_PARENT)
            binding.flView.layoutParams = fl_params
        }, 300)
    }


    private val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = camera?.cameraInfo?.zoomState?.value?.zoomRatio!! * detector.scaleFactor
            camera?.cameraControl?.setZoomRatio(scale)
            return true
        }
    }
    private fun captureView(view: View, window: Window, bitmapCallback: (Bitmap) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Above Android O, use PixelCopy
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val location = IntArray(2)
            view.getLocationInWindow(location)
            PixelCopy.request(
                window,
                Rect(
                    location[0],
                    location[1],
                    location[0] + view.width,
                    location[1] + view.height
                ),
                bitmap,
                {
                    if (it == PixelCopy.SUCCESS) {
                        bitmapCallback.invoke(bitmap)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } else {
            val tBitmap = Bitmap.createBitmap(
                view.width, view.height, Bitmap.Config.RGB_565
            )
            val canvas = Canvas(tBitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
            bitmapCallback.invoke(tBitmap)
        }
    }

    private fun storeImage(bitmap: Bitmap): Uri {
        binding.linearLayout.visibility = View.VISIBLE
        binding.captureLayout.visibility = View.GONE

        try {

            var resizeBitmap: Bitmap? = null
            val imagepathTemp: String

            if (bitmap.width >= 1280) {
                try {
                    imagepathTemp = getFilePath(binding.root.context, bitmap)!!
                    resizeBitmap = compressImage(imagepathTemp)
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                }
            } else {
                resizeBitmap = bitmap
            }

            val imagesPath = if (resizeBitmap!!.width > resizeBitmap.height) {
                val angleToRotate: Int = setCameraDisplayOrientation()
                val rotatedBitmap: Bitmap = myRotation(resizeBitmap, angleToRotate)!!
                getFilePath(binding.root.context, rotatedBitmap)
            } else {
                getFilePath(binding.root.context, resizeBitmap)
            }
            oldUri = Uri.parse(imagesPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Uri.parse(imagesPath)
    }

    //Save Images into internal memory
    private fun getFilePath(context: Context?, bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("images", Context.MODE_PRIVATE)
        if (!directory.exists()) {
            directory.mkdir()
        }
        // Create imageDir
        val filename = "" + System.currentTimeMillis()
        val mypath = File(directory, "$filename.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val muri = Uri.fromFile(mypath)
        return muri.path
    }

    private fun compressImage(filePath: String?): Bitmap? {
        var scaledBitmap: Bitmap? = null
        var bmp: Bitmap?
        var actualWidth: Int
        var actualHeight: Int
        val options = BitmapFactory.Options()
        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        bmp = BitmapFactory.decodeFile(filePath, options)
        // Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        actualHeight = options.outHeight
        actualWidth = options.outWidth
        //      max Height and width values of the compressed image is taken as 816x612
        val maxWidth = 1280.0f
        val maxHeight = 960.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp!!,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return scaledBitmap
    }

    private fun setCameraDisplayOrientation(): Int {

        val orientationEventListener by lazy {
            object : OrientationEventListener(binding.root.context) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == ORIENTATION_UNKNOWN) {
                        return
                    }
                    val rotation = when (orientation) {
                        in 45 until 135 -> Surface.ROTATION_270
                        in 135 until 225 -> Surface.ROTATION_180
                        in 225 until 315 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }
                    imageCapture?.targetRotation = rotation
                }

            }
        }
        orientationEventListener.enable()
        return 0
    }

    private fun myRotation(bitmap: Bitmap, degree: Int): Bitmap? {
        val w = bitmap.width
        val h = bitmap.height
        val mtx = Matrix()
        mtx.setRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    private fun showToast(message: String, toastAngle: Int): Toast {
        var clearToast: Toast? = null

        class RotatedTextView(text: String, context: Context?) :
            View(context) {
            private var text = ""
            private val paint = Paint()
            private val bounds = Rect()

            init {
                this.text = text
            }

            override fun onDraw(canvas: Canvas) {
                val scale = resources.displayMetrics.density
                paint.textSize = 14 * scale + 0.5f // convert dps to pixels
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(75, 75, 75)
                paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)
                paint.getTextBounds(text, 0, text.length, bounds)
                val padding = (14 * scale + 0.5f).toInt() // convert dps to pixels
                val offset_y = (32 * scale + 0.5f).toInt() // convert dps to pixels
                canvas.save()
                canvas.rotate(
                    toastAngle.toFloat(),
                    (canvas.width / 2).toFloat(),
                    (canvas.height / 2).toFloat()
                )
                canvas.drawRect(
                    (canvas.width / 2 - bounds.width() / 2 + bounds.left - padding).toFloat(),
                    (
                            canvas.height / 2 + bounds.top - padding + offset_y).toFloat(),
                    (
                            canvas.width / 2 - bounds.width() / 2 + bounds.right + padding).toFloat(),
                    (
                            canvas.height / 2 + bounds.bottom + padding + offset_y).toFloat(),
                    paint
                )
                paint.color = Color.WHITE
                canvas.drawText(
                    text,
                    (canvas.width / 2 - bounds.width() / 2).toFloat(),
                    (canvas.height / 2 + offset_y).toFloat(),
                    paint
                )
                canvas.restore()
            }
        }
        clearToast?.cancel()
        clearToast = Toast(binding.root.context)
        val text: View = RotatedTextView(message, binding.root.context)
        clearToast.view = text
        clearToast.duration = Toast.LENGTH_SHORT
        clearToast.show()
        return clearToast
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(binding.root.context, "permission not granted", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().finish()
            }
        }


    }

    // image analyzer listener
    class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()
            listener(luma)
            image.close()
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }


    private fun doneClose(): Int {
        var value = 0
        for (i in imageCount until imagesList.size) {
            if (imagesList[i].imgPath == null || imagesList[i].imgPath == "") {
                value = i
            }
        }
        return value
    }

    private fun printPaths() {
        camListImages?.myCamListImages(imagesList)
        requireActivity().finish()
    }

    private fun skipImages(): Int {
        var value = 0
        for (i in imageCount until imagesList.size) {
            if (imagesList[i].imgPath == null || imagesList[i].imgPath == "") {
                value = i
                break
            }
        }
        return value
    }


    private fun startSensor() {
        sm = requireActivity().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        accelerometer = sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sm!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Toast.makeText(binding.root.context, "Sensor is not supported.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        x = sensorEvent?.values!![0].toInt()
        y = sensorEvent.values!![1].toInt()
        z = sensorEvent.values!![2].toInt()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            try {
                camListImages?.myCamListImages(imagesList)
                requireActivity().finish()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun applyListener() {
        startCamera()
        /*water mark position*/



        if (CamPref.getIn(binding.root.context).isCamShowLabelName) {
            binding.txtTitle.visibility = View.VISIBLE

        } else {
            binding.txtTitle.visibility = View.GONE
        }



    }

    fun copyBitmap(src: Bitmap): Bitmap {
        val config = if (src.config != null) src.config else Bitmap.Config.ARGB_8888
        val copy = Bitmap.createBitmap(src.width, src.height, config)
        val canvas = Canvas(copy)
        val paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        }
        canvas.drawBitmap(src, 0f, 0f, paint)
        return copy
    }


}