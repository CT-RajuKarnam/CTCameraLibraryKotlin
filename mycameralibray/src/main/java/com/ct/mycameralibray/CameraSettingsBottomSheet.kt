package com.ct.mycameralibray

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.appcompat.widget.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CameraSettingsBottomSheet(
    private var myListener: MyListener,
    private var imgOrientation: String
) :
    BottomSheetDialogFragment() {
    var swWatermark: SwitchCompat? = null
    var swAddress: SwitchCompat? = null
    var swLatLng: SwitchCompat? = null
    var swTime: SwitchCompat? = null
    var swTextOverlay: SwitchCompat? = null
    var swGuideBox: SwitchCompat? = null
    var swLabel: SwitchCompat? = null
    var swHelper: SwitchCompat? = null
    var spWatermarkPosition: AppCompatSpinner? = null
    var spAspectRatio: AppCompatSpinner? = null
    var spTextAt: AppCompatSpinner? = null
    var btnApply: AppCompatButton? = null
    var btnReset: AppCompatButton? = null
    var imgCloseDialog: AppCompatImageView? = null
    var watermarkPosition = arrayOf<String?>(
        "WaterMark Position",
        "Top-Left",
        "Top-Right",
        "Bottom-Left",
        "Bottom-right"
    )
    var watermarkStatePosition: String? = ""
    var watermark = ""
    var descPosition =
        arrayOf<String?>("Label Position", "Top-Left", "Top-Right", "Bottom-Left", "Bottom-right")
    var descStatePosition: String? = ""
    var desc = ""
    var aspectRatioPosition = arrayOf<String?>("Aspect Ratio", "9:16", "3:4")
    var arStatePosition: String? = ""
    var aspectRatio = ""
    var rgAspectRatio: RadioGroup? = null
    var rbFull: AppCompatRadioButton? = null
    var rb916: AppCompatRadioButton? = null
    var rb34: AppCompatRadioButton? = null
    var rb11: AppCompatRadioButton? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.camera_settings_bottom_sheet, container, false)
        swWatermark = v.findViewById(R.id.swWatermark)
        swAddress = v.findViewById(R.id.swAddress)
        swLatLng = v.findViewById(R.id.swLatLng)
        swTime = v.findViewById(R.id.swTime)
        swTextOverlay = v.findViewById(R.id.swTextOverlay)
        swGuideBox = v.findViewById(R.id.swGuideBox)
        spWatermarkPosition = v.findViewById(R.id.spWatermarkPosition)
        spAspectRatio = v.findViewById(R.id.spAspectRatio)
        spTextAt = v.findViewById(R.id.spDescPosition)
        btnApply = v.findViewById(R.id.btnApply)
        rgAspectRatio = v.findViewById(R.id.rgAspectRatio)
        rbFull = v.findViewById(R.id.rbFull)
        rb916 = v.findViewById(R.id.rb916)
        rb34 = v.findViewById(R.id.rb34)
        rb11 = v.findViewById(R.id.rb11)
        swLabel = v.findViewById(R.id.swLabel)
        swHelper = v.findViewById(R.id.swHelper)
        btnReset = v.findViewById(R.id.btnReset)
        imgCloseDialog = v.findViewById(R.id.imgCloseDialog)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swWatermark!!.isChecked = CamPref.getIn(requireContext()).isCamShowWaterMark

        swAddress!!.isChecked = CamPref.getIn(requireContext()).isCamShowAddress
        swLatLng!!.isChecked = CamPref.getIn(requireContext()).isCamShowLatLong
        swTime!!.isChecked = CamPref.getIn(requireContext()).isCamShowTime
        swTextOverlay!!.isChecked = CamPref.getIn(requireContext()).isCamShowOverlayImg
        swGuideBox!!.isChecked = CamPref.getIn(requireContext()).isCamShowGuidBox
        swLabel!!.isChecked = CamPref.getIn(requireContext()).isCamShowLabelName
        swHelper!!.isChecked = CamPref.getIn(requireContext()).isCamShowOverlayImg

        if (imgOrientation.equals("L")) {
            rgAspectRatio!!.visibility = View.VISIBLE
            if (CamPref.getIn(requireContext()).camAspectRatio.equals("Full", ignoreCase = true)) {
                rbFull!!.isChecked = true
            } else if (CamPref.getIn(requireContext()).camAspectRatio.equals(
                    "9:16",
                    ignoreCase = true
                )
            ) {
                rb916!!.isChecked = true
            } else if (CamPref.getIn(requireContext()).camAspectRatio.equals(
                    "3:4",
                    ignoreCase = true
                )
            ) {
                rb34!!.isChecked = true
            } else if (CamPref.getIn(requireContext()).camAspectRatio.equals(
                    "1:1",
                    ignoreCase = true
                )
            ) {
                rb11!!.isChecked = true
            } else {
                rbFull!!.isChecked = false
                rb916!!.isChecked = false
                rb34!!.isChecked = false
                rb11!!.isChecked = false
            }
        }else{
            rgAspectRatio!!.visibility = View.GONE
        }
        imgCloseDialog!!.setOnClickListener {
            dismiss()
        }
        btnApply!!.setOnClickListener {
            myListener.applyListener()
            dismiss()
        }
        btnReset!!.setOnClickListener {
            CamPref.getIn(requireContext()).clearPref()
            myListener.applyListener()
            dismiss()
        }
        swWatermark!!.setOnCheckedChangeListener { _, b: Boolean ->
            if (b) {
                CamPref.getIn(requireContext()).isCamShowWaterMark = true
                spWatermarkPosition!!.setSelection(1, true)
            } else {
                CamPref.getIn(requireContext()).isCamShowWaterMark = false
                spWatermarkPosition!!.setSelection(0, true)
            }
        }
        swAddress!!.setOnCheckedChangeListener { _, b: Boolean ->
            if (b) {
                CamPref.getIn(requireContext()).isCamShowAddress = true
                swTextOverlay!!.isChecked = true
            } else {
                CamPref.getIn(requireContext()).isCamShowAddress = false
                if (!CamPref.getIn(requireContext()).isCamShowTime && !CamPref.getIn(requireContext()).isCamShowLatLong) {
                    swTextOverlay!!.isChecked = false
                }
            }
        }
        swLatLng!!.setOnCheckedChangeListener { _, b: Boolean ->
            if (b) {
                CamPref.getIn(requireContext()).isCamShowLatLong = true
                swTextOverlay!!.isChecked = true
            } else {
                CamPref.getIn(requireContext()).isCamShowLatLong = false
                if (!CamPref.getIn(requireContext()).isCamShowAddress && !CamPref.getIn(
                        requireContext()
                    ).isCamShowTime
                ) {
                    swTextOverlay!!.isChecked = false
                }
            }
        }
        swTime!!.setOnCheckedChangeListener { _, b: Boolean ->
            if (b) {
                CamPref.getIn(requireContext()).isCamShowTime = true
                swTextOverlay!!.isChecked = true
            } else {
                CamPref.getIn(requireContext()).isCamShowTime = false
                if (!CamPref.getIn(requireContext()).isCamShowAddress && !CamPref.getIn(
                        requireContext()
                    ).isCamShowLatLong
                ) {
                    swTextOverlay!!.isChecked = false
                }
            }
        }
        swTextOverlay!!.isChecked =
            CamPref.getIn(requireContext()).isCamShowAddress || CamPref.getIn(requireContext()).isCamShowLatLong || CamPref.getIn(
                requireContext()
            ).isCamShowTime
        swTextOverlay!!.setOnCheckedChangeListener { _, b: Boolean ->
            if (b) {
                spTextAt!!.setSelection(1, true)
            } else {
                spTextAt!!.setSelection(0, true)
            }
        }
        swGuideBox!!.setOnCheckedChangeListener { _, b: Boolean ->
            CamPref.getIn(
                requireContext()
            ).isCamShowGuidBox = b
        }
        swLabel!!.setOnCheckedChangeListener { _, b: Boolean ->
            CamPref.getIn(
                requireContext()
            ).isCamShowLabelName = b
        }
        swHelper!!.setOnCheckedChangeListener { _, b: Boolean ->
            CamPref.getIn(
                requireContext()
            ).isCamShowOverlayImg = b
        }
        rgAspectRatio!!.setOnCheckedChangeListener { _, i: Int ->
            when (i) {
                R.id.rbFull -> {
                    CamPref.getIn(requireContext()).camAspectRatio = "Full"
                }
                R.id.rb916 -> {
                    CamPref.getIn(requireContext()).camAspectRatio = "9:16"
                }
                R.id.rb34 -> {
                    CamPref.getIn(requireContext()).camAspectRatio = "3:4"
                }
                R.id.rb11 -> {
                    CamPref.getIn(requireContext()).camAspectRatio = "1:1"
                }
                else -> {
                    CamPref.getIn(requireContext()).camAspectRatio = "3:4"
                }
            }
        }
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            watermarkPosition
        )
        val adapter1: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, descPosition)
        val adapter2: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            aspectRatioPosition
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spWatermarkPosition!!.adapter = adapter
        spTextAt!!.adapter = adapter1
        spAspectRatio!!.adapter = adapter2

        for (i in 1 until watermarkPosition.size) {
            watermark = CamPref.getIn(requireContext()).camShowWaterMarkAt
            if (watermarkPosition[i] == watermark) {
                watermarkStatePosition = watermarkPosition[i]
                spWatermarkPosition!!.setSelection(i)
            }
        }
        for (j in 1 until descPosition.size) {
            desc = CamPref.getIn(requireContext()).camShowTextAt
            if (descPosition[j] == desc) {
                descStatePosition = descPosition[j]
                spTextAt!!.setSelection(j)
            }
        }
        for (k in 1 until aspectRatioPosition.size) {
            aspectRatio = CamPref.getIn(requireContext()).camAspectRatio
            if (aspectRatioPosition[k] == aspectRatio) {
                arStatePosition = aspectRatioPosition[k]
                spAspectRatio!!.setSelection(k)
            }
        }
        spWatermarkPosition!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                if (position > 0) {
                    if (watermarkPosition[position].equals("Top-Left", ignoreCase = true)) {
                        CamPref.getIn(requireContext()).camShowWaterMarkAtPos =
                            Gravity.TOP or Gravity.LEFT
                        CamPref.getIn(requireContext()).camShowWaterMarkAt = "Top-Left"
                    } else if (watermarkPosition[position].equals("Top-Right", ignoreCase = true)) {
                        CamPref.getIn(requireContext()).camShowWaterMarkAtPos =
                            Gravity.TOP or Gravity.RIGHT
                        CamPref.getIn(requireContext()).camShowWaterMarkAt = "Top-Right"
                    } else if (watermarkPosition[position].equals(
                            "Bottom-Left",
                            ignoreCase = true
                        )
                    ) {
                        CamPref.getIn(requireContext()).camShowWaterMarkAtPos =
                            Gravity.BOTTOM or Gravity.LEFT
                        CamPref.getIn(requireContext()).camShowWaterMarkAt = "Bottom-Left"
                    } else if (watermarkPosition[position].equals(
                            "Bottom-right",
                            ignoreCase = true
                        )
                    ) {
                        CamPref.getIn(requireContext()).camShowWaterMarkAtPos =
                            Gravity.BOTTOM or Gravity.RIGHT
                        CamPref.getIn(requireContext()).camShowWaterMarkAt = "Bottom-right"
                    } else {
                        CamPref.getIn(requireContext()).camShowWaterMarkAtPos =
                            Gravity.BOTTOM or Gravity.LEFT
                        CamPref.getIn(requireContext()).camShowWaterMarkAt = "Bottom-Left"
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        spTextAt!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                if (position > 0) {
                    if (descPosition[position].equals("Top-Left", ignoreCase = true)) {
                        CamPref.getIn(requireContext()).camDescPosition =
                            Gravity.TOP or Gravity.LEFT
                        CamPref.getIn(requireContext()).camShowTextAt = "Top-Left"
                    } else if (descPosition[position].equals("Top-Right", ignoreCase = true)) {
                        CamPref.getIn(requireContext()).camDescPosition =
                            Gravity.TOP or Gravity.RIGHT
                        CamPref.getIn(requireContext()).camShowTextAt = "Top-Right"
                    } else if (descPosition[position].equals("Bottom-Left", ignoreCase = true)) {
                        CamPref.getIn(requireContext()).camDescPosition =
                            Gravity.BOTTOM or Gravity.LEFT
                        CamPref.getIn(requireContext()).camShowTextAt = "Bottom-Left"
                    } else if (descPosition[position].equals("Bottom-right", ignoreCase = true)) {
                        CamPref.getIn(requireContext()).camDescPosition =
                            Gravity.BOTTOM or Gravity.RIGHT
                        CamPref.getIn(requireContext()).camShowTextAt = "Bottom-right"
                    } else {
                        CamPref.getIn(requireContext()).camDescPosition = 0
                        CamPref.getIn(requireContext()).camShowTextAt = "Top-Left"
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        spAspectRatio!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                if (position > 0) {
                    CamPref.getIn(requireContext()).camAspectRatio = aspectRatioPosition[position]
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CloseBottomSheetDialogTheme)
        isCancelable = false
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}