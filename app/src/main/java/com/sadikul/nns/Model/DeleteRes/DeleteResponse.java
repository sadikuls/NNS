package com.sadikul.nns.Model.DeleteRes;

import com.google.gson.annotations.SerializedName;
import com.sadikul.nns.Model.NoticeItem;

import java.util.List;

public class DeleteResponse{

	@SerializedName("msg")
	private String msg;

	@SerializedName("data")
	private List<NoticeItem> data;

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setData(List<NoticeItem> data){
		this.data = data;
	}

	public List<NoticeItem> getData(){
		return data;
	}
}