package com.ct.mycameralibray

import java.io.Serializable

class ImageTags : Serializable {
    var imgId: String? = null
    var imgSno: String? = null
    var imgName: String? = null
    var imgMand: String? = null
    var imgFrontCam: String? = null
    var imgLogo: String? = null
    var imgOrientation: String? = null
    var imgLat: String? = null
    var imgLong: String? = null
    var imgTime: String? = null
    var imgFlag: String? = "0"
    var imgPath: String? = null
    var imgOverlayLogo: String? = null
}