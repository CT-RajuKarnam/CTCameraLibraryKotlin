package com.ct.mycameralibray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;


public class CamPref {
    private static CamPref uniqInstance;
    private static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    public static CamPref getIn(Context context) {
        if (uniqInstance == null) {
            uniqInstance = new CamPref();
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        editor = pref.edit();
        return uniqInstance;
    }


    public boolean isCamShowWaterMark() {
        return pref.getBoolean("camShowWaterMark", false);
    }

    public void setCamShowWaterMark(boolean camShowWaterMark) {
        editor.putBoolean("camShowWaterMark", camShowWaterMark);
        editor.apply();
    }

    public String getCamWaterMarkUrl() {
        return pref.getString("camWaterMarkUrl", "");
    }

    public void setCamWaterMarkUrl(String camWaterMarkUrl) {
        editor.putString("camWaterMarkUrl", camWaterMarkUrl);
        editor.apply();
    }

    public boolean getOrientationFlag() {
        return pref.getBoolean("orientation_flag", false);
    }

    public void setOrientationFlag(boolean flag) {
        editor.putBoolean("orientation_flag", flag);
        editor.apply();
    }


    public boolean isCamShowAddress() {
        return pref.getBoolean("camShowAddress", false);
    }

    public void setCamShowAddress(boolean camShowAddress) {
        editor.putBoolean("camShowAddress", camShowAddress);
        editor.apply();
    }

    public boolean isCamShowLatLong() {
        return pref.getBoolean("camShowLatLong", false);
    }

    public void setCamShowLatLong(boolean camShowLatLong) {
        editor.putBoolean("camShowLatLong", camShowLatLong);
        editor.apply();
    }

    public boolean isCamShowOverlayImg() {
        return pref.getBoolean("camShowOverlayImg", false);
    }

    public void setCamShowOverlayImg(boolean camShowOverlayImg) {
        editor.putBoolean("camShowOverlayImg", camShowOverlayImg);
        editor.apply();
    }

    public boolean isCamSavePicExternal() {
        return pref.getBoolean("camSavePicExternal", false);
    }

    public void setCamSavePicExternal(boolean camSavePicExternal) {
        editor.putBoolean("camSavePicExternal", camSavePicExternal);
        editor.apply();
    }

    public boolean isCamShowTime() {
        return pref.getBoolean("camShowTime", false);
    }

    public void setCamShowTime(boolean camShowTime) {
        editor.putBoolean("camShowTime", camShowTime);
        editor.apply();
    }

    public boolean isCamShowLabelName() {
        return pref.getBoolean("camShowLabelName", false);
    }

    public void setCamShowLabelName(boolean camShowLabelName) {
        editor.putBoolean("camShowLabelName", camShowLabelName);
        editor.apply();
    }

    public boolean isCamShowGuidBox() {
        return pref.getBoolean("camShowGuidBox", false);
    }

    public void setCamShowGuidBox(boolean camShowGuidBox) {
        editor.putBoolean("camShowGuidBox", camShowGuidBox);
        editor.apply();
    }

    public boolean isCamShowGuidLines() {
        return pref.getBoolean("camShowGuidLines", false);
    }

    public void setCamShowGuidLines(boolean camShowGuidLines) {
        editor.putBoolean("camShowGuidLines", camShowGuidLines);
        editor.apply();
    }

    public String getCamShowTextAt() {
        return pref.getString("camShowTextAt", "");
    }

    public void setCamShowTextAt(String camShowTextAt) {
        editor.putString("camShowTextAt", camShowTextAt);
        editor.apply();
    }

    public String getCamShowWaterMarkAt() {
        return pref.getString("camShowWaterMarkAt", "");
    }

    public void setCamShowWaterMarkAt(String camShowWaterMarkAt) {
        editor.putString("camShowWaterMarkAt", camShowWaterMarkAt);
        editor.apply();
    }

    public void setCamShowWaterMarkAtPos(Integer value) {
        editor.putInt("camShowWaterMarkAtPos", value);
        editor.apply();
    }

    public int getCamShowWaterMarkAtPos() {
        return pref.getInt("camShowWaterMarkAtPos", Gravity.BOTTOM | Gravity.LEFT);
    }

    public String getCamFlashMode() {
        return pref.getString("camFlashMode", "");
    }

    public void setCamFlashMode(String camFlashMode) {
        editor.putString("camFlashMode", camFlashMode);
        editor.apply();
    }

    public String getCamAspectRatio() {
        return pref.getString("camAspectRatio", "");
    }

    public void setCamAspectRatio(String camAspectRatio) {
        editor.putString("camAspectRatio", camAspectRatio);
        editor.apply();
    }

    public String getCamFolderName() {
        return pref.getString("camFolderName", "");
    }

    public void setCamFolderName(String camFolderName) {
        editor.putString("camFolderName", camFolderName);
        editor.apply();
    }

    public String getCamOriRegixL() {
        return pref.getString("camOriRegixL", "");
    }

    public void setCamOriRegixL(String camOriRegixL) {
        editor.putString("camOriRegixL", camOriRegixL);
        editor.apply();
    }

    public String getCamOriRegixP() {
        return pref.getString("camOriRegixP", "");
    }

    public void setCamOriRegixP(String camOriRegixP) {
        editor.putString("camOriRegixP", camOriRegixP);
        editor.apply();
    }

    public void setCamDescPosition(Integer value) {
        editor.putInt("descPosition", value);
        editor.apply();
    }

    public Integer getCamDescPosition() {
        return pref.getInt("descPosition", Gravity.BOTTOM | Gravity.RIGHT);
    }

    public void setCamDescValue(String value) {
        editor.putString("descValue", value);
        editor.apply();
    }

    public String getCamDescValue() {
        return pref.getString("descValue", "");
    }

    public void clearPref() {
        editor.clear().apply();
    }

    public void setCamSwitch(String value){
        editor.putString("switchCamera",value);
        editor.apply();
    }

    public String getCamSwitch(){
        return pref.getString("switchCamera", "back");
    }

}

