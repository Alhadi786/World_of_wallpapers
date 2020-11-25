package com.wasidnp.models;

public class ItemCategory {

    private String CategoryId;
    private String CategoryName;
    private String CategoryImage;
    private String CategoryAds_url;
    private String CategoryAds_enable;


    public ItemCategory(String categoryid, String categoryname, String categoryimage) {
        // TODO Auto-generated constructor stub
        this.CategoryId = categoryid;
        this.CategoryName = categoryname;
        this.CategoryImage = categoryimage;
    }

    public ItemCategory() {
        // TODO Auto-generated constructor stub
    }

    public String getCategoryAds_url() {
        return CategoryAds_url;
    }

    public void setCategoryAds_url(String categoryAds_url) {
        CategoryAds_url = categoryAds_url;
    }



    public String getCategoryAds_enable() {
        return CategoryAds_enable;
    }

    public void setCategoryAds_enable(String categoryAds_enable) {
        CategoryAds_enable = categoryAds_enable;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryImage(String categoryimage) {
        this.CategoryImage = categoryimage;
    }

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryName(String categoryname) {
        this.CategoryName = categoryname;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryid) {
        this.CategoryId = categoryid;
    }

}
