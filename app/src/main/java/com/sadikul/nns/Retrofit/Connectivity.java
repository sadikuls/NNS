package com.sadikul.nns.Retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Connectivity {
    private static Connectivity connectivity=null;
    private static Context context=null;

    private Connectivity(Context context) {
        this.context=context;
    }

    public static synchronized Connectivity getInstance(Context context){
        if(connectivity==null){
            connectivity=new Connectivity(context);

        }
        return connectivity;
    }

    public static boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo() ;

        if(networkInfo != null&& networkInfo.isConnected()){
            return true;
        }
        return false;
    }
}