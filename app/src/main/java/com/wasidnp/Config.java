package com.wasidnp;

import android.databinding.BaseObservable;

import java.io.Serializable;

public class Config extends BaseObservable implements Serializable {

    //your admin panel url
    public static final String ADMIN_PANEL_URL = "http://dronewallpaper.com/walladmin";

    //number of grid column of wallpaper images
    public static final int NUM_OF_COLUMNS = 2;

    //set true to enable ads or set false to disable ads

    /*public  String admob_app_id = "ca-app-pub-9165362605707343~1844293230";
    public  String admob_banner_unit_id = "ca-app-pub-9165362605707343/7643414821";
    public  String admob_interstitial_unit_id = "ca-app-pub-9165362605707343/2554739677";
    public  String admob_reward_video_unit_id = "ca-app-pub-9165362605707343/2758229085";*/

    public  String admob_app_id = "ca-app-pub-3940256099942544~3347511713";
    public  String admob_banner_unit_id = "ca-app-pub-3940256099942544/6300978111";
    public  String admob_interstitial_unit_id = "ca-app-pub-3940256099942544/1033173712";
    public  String admob_reward_video_unit_id = "ca-app-pub-3940256099942544/5224354917";

    public  String ENABLE_ADMOB_BANNER_ADS = "true";
    public  String ENABLE_ADMOB_VIDEO_ADS = "true";
    public  String ENABLE_ADMOB_INTERSTITIAL_ADS = "true";

    public  final int INTERSTITIAL_ADS_INTERVAL = 2;

    //set true to enable exit dialog or set false to disable exit dialog
    public static final boolean ENABLE_EXIT_DIALOG = true;

    //set true to enable tab layout
    public static final boolean ENABLE_TAB_LAYOUT = true;

    public static final boolean ENABLE_CENTER_CROP_IN_DETAIL_WALLPAPER = true;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

//    public Config(String admob_app_id, String admob_banner_unit_id, String admob_interstitial_unit_id,
//                  String admob_reward_video_unit_id, boolean enableAdmobBannerAds,
//                  boolean enableAdmobVideoAds, boolean enableAdmobInterstitialAds) {
//          this.admob_app_id = admob_app_id;
//          this.admob_banner_unit_id = admob_banner_unit_id;
//          this.admob_interstitial_unit_id = admob_interstitial_unit_id;
//          this.admob_reward_video_unit_id = admob_reward_video_unit_id;
//          this.ENABLE_ADMOB_BANNER_ADS = enableAdmobBannerAds;
//          this.ENABLE_ADMOB_VIDEO_ADS = enableAdmobVideoAds;
//          this.ENABLE_ADMOB_INTERSTITIAL_ADS = enableAdmobInterstitialAds;
//    }

    public Config() {

    }

    public  String getAdminPanelUrl() {
        return ADMIN_PANEL_URL;
    }

    public  int getNumOfColumns() {
        return NUM_OF_COLUMNS;
    }

    public  String isEnableAdmobBannerAds() {
        return ENABLE_ADMOB_BANNER_ADS;
    }

    public  String getAdmob_app_id() {
        return admob_app_id;
    }

    public  String getAdmob_banner_unit_id() {
        return admob_banner_unit_id;
    }

    public  String getAdmob_interstitial_unit_id() {
        return admob_interstitial_unit_id;
    }

    public  String isEnableAdmobVideoAds() {
        return ENABLE_ADMOB_VIDEO_ADS;
    }

    public  String isEnableAdmobInterstitialAds() {
        return ENABLE_ADMOB_INTERSTITIAL_ADS;
    }

    public  int getInterstitialAdsInterval() {
        return INTERSTITIAL_ADS_INTERVAL;
    }

    public  boolean isEnableExitDialog() {
        return ENABLE_EXIT_DIALOG;
    }

    public  boolean isEnableTabLayout() {
        return ENABLE_TAB_LAYOUT;
    }

    public  boolean isEnableCenterCropInDetailWallpaper() {
        return ENABLE_CENTER_CROP_IN_DETAIL_WALLPAPER;
    }

    public  boolean isEnableRtlMode() {
        return ENABLE_RTL_MODE;
    }

    public  void setAdmob_app_id(String admob_app_id) {
        this.admob_app_id = admob_app_id;
    }

    public  void setAdmob_banner_unit_id(String admob_banner_unit_id) {
        this.admob_banner_unit_id = admob_banner_unit_id;
    }

    public  void setAdmob_interstitial_unit_id(String admob_interstitial_unit_id) {
        this.admob_interstitial_unit_id = admob_interstitial_unit_id;
    }

    public  String getAdmob_reward_video_unit_id() {
        return admob_reward_video_unit_id;
    }

    public  void setAdmob_reward_video_unit_id(String admob_reward_video_unit_id) {
        this.admob_reward_video_unit_id = admob_reward_video_unit_id;
    }

    public  void setEnableAdmobBannerAds(String enableAdmobBannerAds) {
        ENABLE_ADMOB_BANNER_ADS = enableAdmobBannerAds;
    }

    public  void setEnableAdmobVideoAds(String enableAdmobVideoAds) {
        ENABLE_ADMOB_VIDEO_ADS = enableAdmobVideoAds;
    }

    public  void setEnableAdmobInterstitialAds(String enableAdmobInterstitialAds) {
        ENABLE_ADMOB_INTERSTITIAL_ADS = enableAdmobInterstitialAds;
    }

    public String isENABLE_ADMOB_BANNER_ADS() {
        return ENABLE_ADMOB_BANNER_ADS;
    }

    public void setENABLE_ADMOB_BANNER_ADS(String ENABLE_ADMOB_BANNER_ADS) {
        this.ENABLE_ADMOB_BANNER_ADS = ENABLE_ADMOB_BANNER_ADS;
    }

    public String isENABLE_ADMOB_VIDEO_ADS() {
        return ENABLE_ADMOB_VIDEO_ADS;
    }

    public void setENABLE_ADMOB_VIDEO_ADS(String ENABLE_ADMOB_VIDEO_ADS) {
        this.ENABLE_ADMOB_VIDEO_ADS = ENABLE_ADMOB_VIDEO_ADS;
    }

    public String isENABLE_ADMOB_INTERSTITIAL_ADS() {
        return ENABLE_ADMOB_INTERSTITIAL_ADS;
    }

    public void setENABLE_ADMOB_INTERSTITIAL_ADS(String ENABLE_ADMOB_INTERSTITIAL_ADS) {
        this.ENABLE_ADMOB_INTERSTITIAL_ADS = ENABLE_ADMOB_INTERSTITIAL_ADS;
    }

    public int getINTERSTITIAL_ADS_INTERVAL() {
        return INTERSTITIAL_ADS_INTERVAL;
    }

}