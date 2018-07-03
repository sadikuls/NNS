package com.sadikul.nns.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sadikul.nns.Model.Notice;


public class PreferenceManager {
    private static  PreferenceManager preferenceManager=null;
    SharedPreferences mSharedPreferences=null;
    private Context context=null;
    private String noticeTag="files";
    private String log_in_glag="com.sadikul.nns.Utils.login";
    private String image_link_tag="com.sadikul.nns.Utils.imagelink";
    private String pdf_link_tag="com.sadikul.nns.Utils.pdf";

    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constant.ApplicationId, Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceManager getInstance(Context context){
        if(preferenceManager==null){
            preferenceManager=new PreferenceManager(context);

        }
        return preferenceManager;
    }



    public boolean setNotices(Notice noticesDetails){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noticesDetails);
        editor.putString(noticeTag, json);

        return editor.commit();
    }

    public Notice getNotices (){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(noticeTag, "");
        return gson.fromJson(json, Notice.class);
    }
    public boolean setLoginStatus(boolean loginStatus){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(log_in_glag, loginStatus);

        return editor.commit();
    }


    public boolean setImageLink(String link){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(image_link_tag, link);

        return editor.commit();
    }


    public boolean setPdfLink(String pdfLink){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(pdf_link_tag, pdfLink);

        return editor.commit();
    }
    public String getPdfLink(){
        return mSharedPreferences.getString(pdf_link_tag,null);
    }
    public String getImageLink(){
        return mSharedPreferences.getString(image_link_tag,null);
    }

    public boolean getLoginStatus(){
        return mSharedPreferences.getBoolean(log_in_glag,false);
    }


    public boolean reset(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
        return editor.commit();
    }

}
