package com.ct.mycameralibray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;


public class Pref {
    private static Pref uniqInstance;
    private static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public static Pref getIn(Context context) {
        if (uniqInstance == null) {
            uniqInstance = new Pref();
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        editor = pref.edit();
        return uniqInstance;
    }

    public void saveCameraFlash(String flash) {
        editor.putString("flash", flash);
        editor.apply();
    }

    public void setCamShowWaterMark(boolean value) {
        editor.putBoolean("camShowWaterMark", value);
        editor.apply();
    }

    public boolean getCamShowWaterMark() {
        return pref.getBoolean("camShowWaterMark", false);
    }

    public void setCamShowAddress(boolean value) {
        editor.putBoolean("camShowAddress", value);
        editor.apply();
    }

    public boolean getCamShowAddress() {
        return pref.getBoolean("camShowAddress", false);
    }

    public void setCamShowLatLng(boolean value) {
        editor.putBoolean("camShowLatLong", value);
        editor.apply();
    }

    public boolean getCamShowLatLng() {
        return pref.getBoolean("camShowLatLong", false);
    }

    public void setCamShowTime(boolean value) {
        editor.putBoolean("camShowTime", value);
        editor.apply();
    }

    public boolean getCamShowTime() {
        return pref.getBoolean("camShowTime", false);
    }

    public void setCamShowLabelName(boolean value) {
        editor.putBoolean("camShowLabelName", value);
        editor.apply();
    }

    public boolean getCamShowLabelName() {
        return pref.getBoolean("camShowLabelName", false);
    }

    public void setCamShowOverlayImg(boolean value) {
        editor.putBoolean("camShowOverlayImg", value);
        editor.apply();
    }

    public boolean getCamShowOverlayImg() {
        return pref.getBoolean("camShowOverlayImg", false);
    }

    public void setCamShowGuideBox(boolean value) {
        editor.putBoolean("guideBox", value);
        editor.apply();
    }

    public boolean getCamShowGuideBox() {
        return pref.getBoolean("guideBox", false);
    }

    public void setCamShowImageLabel(boolean value) {
        editor.putBoolean("imageLabel", value);
        editor.apply();
    }

    public boolean getCamShowImageLabel() {
        return pref.getBoolean("imageLabel", false);
    }

    public void setCamShowHelperImage(boolean value) {
        editor.putBoolean("helperImage", value);
        editor.apply();
    }

    public boolean getCamShowHelperImage() {
        return pref.getBoolean("helperImage", false);
    }

    public void setCamShowAt(String value) {
        editor.putString("at", value);
        editor.apply();
    }

    public String getCamShowAt() {
        return pref.getString("at", "");
    }

    public void setCamAspectRatio(String value) {
        editor.putString("camAspectRatio", value);
        editor.apply();
    }

    public String getCamAspectRatio() {
        return pref.getString("camAspectRatio", "Full");
    }

    public void setCamType(String value) {
        editor.putString("type", value);
        editor.apply();
    }

    public String getCamType() {
        return pref.getString("type", "");
    }

    public void setCamOriControl(String value) {
        editor.putString("oriControl", value);
        editor.apply();
    }

    public String getCamOriControl() {
        return pref.getString("oriControl", "");
    }

    public void setCamShowWaterMarkAt(Integer value) {
        editor.putInt("camShowWaterMarkAt", value);
        editor.apply();
    }

    public int getCamShowWaterMarkAt() {
        return pref.getInt("camShowWaterMarkAt", Gravity.BOTTOM | Gravity.LEFT);
    }

    public void setCamWatermarkValue(String value) {
        editor.putString("watermarkValue", value);
        editor.apply();
    }

    public String getCamWatermarkValue() {
        return pref.getString("watermarkValue", "");
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

    public void setCamShowTextAt(String value) {
        editor.putString("camShowTextAt", value);
        editor.apply();
    }

    public String getCamShowTextAt() {
        return pref.getString("camShowTextAt", "");
    }


    public void clearPref() {
        editor.clear().apply();
    }

    public boolean getOrientationFlag() {
        return pref.getBoolean("orientation_flag", false);
    }

    public void setOrientationFlag(boolean flag) {
        editor.putBoolean("orientation_flag", flag);
        editor.apply();
    }


    public void setCameraFlash(String flash) {
        editor.putString("flash", flash);
        editor.apply();
    }

    public String getCameraFlash() {
        return pref.getString("flash", "");
    }
}
