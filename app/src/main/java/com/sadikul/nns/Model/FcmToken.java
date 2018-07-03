package com.sadikul.nns.Model;

import com.google.gson.annotations.SerializedName;

public class FcmToken{

	@SerializedName("status")
	private String status;

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}