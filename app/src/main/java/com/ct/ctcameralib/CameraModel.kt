package com.ct.ctcameralib

import java.io.Serializable

data class CameraModel(

    var imgSno: String? = null,
    var imgName: String? = null,
    var imgOrnt: String? = null,
    var imgPath: String? = null,
    var switch: String? = null
) : Serializable