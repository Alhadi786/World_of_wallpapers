package com.wasidnp.json;

import java.io.Serializable;

public class JsonConfig implements Serializable {

    public static final String TREND_ARRAY_NAME = "data";
    private static final long serialVersionUID = 1L;

    public static final String LATEST_ARRAY_NAME = "MaterialWallpaper";
    public static final String LATEST_IMAGE_CATEGORY_NAME = "category_name";
    public static final String LATEST_IMAGE_URL = "image";

    public static final String CATEGORY_ARRAY_NAME = "MaterialWallpaper";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_CID = "cid";
    public static final String CATEGORY_IMAGE_URL = "category_image";
    public static String CATEGORY_ADS_URL = "ads_url";
    public static final String CATEGORY_ads_image = "ads_image";
    public static String CATEGORY_ads_enable = "ads_enable";

    public static final String CATEGORY_ITEM_ARRAY = "MaterialWallpaper";
    public static final String CATEGORY_ITEM_CATNAME = "cat_name";
    public static final String CATEGORY_ITEM_IMAGEURL = "images";
    public static final String CATEGORY_ITEM_CATID = "cid";

    public static String CATEGORY_ITEM_CATIDD;
    public static String CATEGORY_TITLE;
    public static String CATEGORY_ID;

    public static final String admob_app_data = "data";
    public static final String admob_app_id = "admob_app_id";
    public static final String admob_banner_unit_id = "admob_banner_unit_id";
    public static final String admob_interstitial_unit_id = "admob_interstitial_unit_id";
    public static final String admob_reward_video_unit_id = "admob_reward_video_unit_id";

    public static final boolean ENABLE_ADMOB_BANNER_ADS = false;
    public static final boolean ENABLE_ADMOB_VIDEO_ADS = false;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ADS = false;

}
