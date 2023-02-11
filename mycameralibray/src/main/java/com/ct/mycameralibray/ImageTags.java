package com.ct.mycameralibray;

import java.io.Serializable;

public class ImageTags implements Serializable {
    private String imgId = "";
    private String imgSno = "";
    private String imgName = "";
    private String imgMand = "";

    public String getImgFrontCam() {
        return imgFrontCam;
    }

    public void setImgFrontCam(String imgFrontCam) {
        this.imgFrontCam = imgFrontCam;
    }

    private String imgFrontCam = "";
    private String imgLogo = "";
    private String imgOrientation = "";
    private String imgLat = "";
    private String imgLong = "";
    private String imgTime = "";
    private String imgFlag = "0";
    private String imgPath = "";

    public String getImgOverlayLogo() {
        return imgOverlayLogo;
    }

    public void setImgOverlayLogo(String imgOverlayLogo) {
        this.imgOverlayLogo = imgOverlayLogo;
    }

    private String imgOverlayLogo = "";

    public ImageTags() {
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgSno() {
        return imgSno;
    }

    public void setImgSno(String imgSno) {
        this.imgSno = imgSno;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgMand() {
        return imgMand;
    }

    public void setImgMand(String imgMand) {
        this.imgMand = imgMand;
    }

    public String getImgLogo() {
        return imgLogo;
    }

    public void setImgLogo(String imgLogo) {
        this.imgLogo = imgLogo;
    }

    public String getImgOrientation() {
        return imgOrientation;
    }

    public void setImgOrientation(String imgOrientation) {
        this.imgOrientation = imgOrientation;
    }

    public String getImgLat() {
        return imgLat;
    }

    public void setImgLat(String imgLat) {
        this.imgLat = imgLat;
    }

    public String getImgLong() {
        return imgLong;
    }

    public void setImgLong(String imgLong) {
        this.imgLong = imgLong;
    }

    public String getImgTime() {
        return imgTime;
    }

    public void setImgTime(String imgTime) {
        this.imgTime = imgTime;
    }

    public String getImgFlag() {
        return imgFlag;
    }

    public void setImgFlag(String imgFlag) {
        this.imgFlag = imgFlag;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
