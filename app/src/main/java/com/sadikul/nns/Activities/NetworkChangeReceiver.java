package com.sadikul.nns.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sadikul.nns.Retrofit.Connectivity;
import com.sadikul.nns.Utils.ObservableObject;


public class NetworkChangeReceiver extends BroadcastReceiver {

    Context context=null;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context=context;
        if(Connectivity.getInstance(context).isNetworkConnected()){
            ObservableObject.getInstance().updateValue(intent);
        }
    }


}

