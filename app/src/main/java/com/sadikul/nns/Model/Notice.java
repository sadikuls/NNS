package com.sadikul.nns.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Notice {
    @SerializedName("notice")
    private List<NoticeItem> notice;

    public void setNotice(List<NoticeItem> notice){
        this.notice = notice;
    }

    public List<NoticeItem> getNotice(){
        return notice;
    }
}
