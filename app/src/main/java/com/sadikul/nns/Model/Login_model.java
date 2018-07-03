package com.sadikul.nns.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Login_model {

	@SerializedName("msg")
	private String msg;

	@SerializedName("user")
	private List<UserItem> user;

	@SerializedName("status")
	private String status;

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setUser(List<UserItem> user){
		this.user = user;
	}

	public List<UserItem> getUser(){
		return user;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

}