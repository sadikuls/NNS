package com.sadikul.nns.Retrofit;


import com.sadikul.nns.Utils.Constant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    public static Retrofit getRetrofitClient(){
        return new Retrofit.Builder().
                baseUrl(Constant.baseURL).
                addConverterFactory(GsonConverterFactory.create()).build();
        }

    public static ApiInterface getApiInterface(){
        return getRetrofitClient().create(ApiInterface.class);
    }
}
