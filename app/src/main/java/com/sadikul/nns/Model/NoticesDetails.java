package com.sadikul.nns.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoticesDetails {

	@SerializedName("notice")
	private List<NoticeItemDetails> notice;

	public void setNotice(List<NoticeItemDetails> notice){
		this.notice = notice;
	}

	public List<NoticeItemDetails> getNotice(){
		return notice;
	}
}