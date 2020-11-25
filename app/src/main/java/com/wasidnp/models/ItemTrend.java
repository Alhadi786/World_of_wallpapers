package com.wasidnp.models;

public class ItemTrend {

	private String ImageUrl;

	public ItemTrend(String limage) {
		// TODO Auto-generated constructor stub
		this.ImageUrl=limage;
	}

	public ItemTrend() {
		// TODO Auto-generated constructor stub
	}
	 
	public String getImageurl()
	{
		return ImageUrl;
		
	}
	
	public void setImageurl(String imageurl)
	{
		this.ImageUrl=imageurl;
	}

}
