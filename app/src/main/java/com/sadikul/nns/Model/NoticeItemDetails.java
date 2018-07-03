package com.sadikul.nns.Model;

import com.google.gson.annotations.SerializedName;

public class NoticeItemDetails {

	@SerializedName("image_link")
	private String imageLink;

	@SerializedName("pdf_link")
	private String pdfLink;

	@SerializedName("id")
	private String id;

	@SerializedName("time")
	private String time;

	@SerializedName("title")
	private String title;

	@SerializedName("descript")
	private String descript;

	public void setImageLink(String imageLink){
		this.imageLink = imageLink;
	}

	public String getImageLink(){
		return imageLink;
	}

	public void setPdfLink(String pdfLink){
		this.pdfLink = pdfLink;
	}

	public String getPdfLink(){
		return pdfLink;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTime(String time){
		this.time = time;
	}

	public String getTime(){
		return time;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setDescript(String descript){
		this.descript = descript;
	}

	public String getDescript(){
		return descript;
	}
}