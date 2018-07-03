package com.sadikul.nns.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sadikul.nns.Adapter.DashboardAdapter;
import com.sadikul.nns.Model.Notice;
import com.sadikul.nns.Model.NoticeItem;
import com.sadikul.nns.R;
import com.sadikul.nns.Retrofit.ApiInterface;
import com.sadikul.nns.Retrofit.Connectivity;
import com.sadikul.nns.Retrofit.RetrofitClient;
import com.sadikul.nns.Utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoard extends AppCompatActivity {

    PreferenceManager preferenceManager;
    ProgressDialog pd;
    @BindView(R.id.noticeRecyclerview)
    RecyclerView noticeRecyclerview;

    List<NoticeItem> notices;

    @BindView(R.id.noticeboardToolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("All Uploaded files");
        notices=new ArrayList<>();
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        getfiles();
        preferenceManager= PreferenceManager.getInstance(this);

        noticeRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        if(!Connectivity.getInstance(this).isNetworkConnected()){
            get_saved_noticeBoard();
            Toast.makeText(this, "Check your network connections", Toast.LENGTH_SHORT).show();
        }else{

            getfiles();
        }



    }





    public void get_saved_noticeBoard() {
        if(preferenceManager.getNotices()!=null){

            notices=preferenceManager.getNotices().getNotice();
            DashboardAdapter adapter=new DashboardAdapter(DashBoard.this,notices);
            noticeRecyclerview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void getfiles(){
        pd.setMessage("Loading...");
        pd.setTitle("Please wait...");
        pd.show();
        ApiInterface apiInterface = RetrofitClient.getApiInterface();
        final Call<Notice> profileBasic = apiInterface.getNotices();
        profileBasic.enqueue(new Callback<Notice>() {
            @Override
            public void onResponse(Call<Notice> call, Response<Notice> response) {
                if (response.isSuccessful()) {
                    pd.dismiss();
                    try {

                        notices=response.body().getNotice();
                        if(notices!=null){
                            DashboardAdapter adapter=new DashboardAdapter(DashBoard.this,notices);
                            noticeRecyclerview.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        //Notice noticess =response.body();
                        //preferenceManager.setNotices(noticess);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DashBoard.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Notice> call, Throwable t) {

                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Please check your internet connection...", Toast.LENGTH_SHORT).show();
                Log.e("log_in", t + "");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_board_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:{
                if(!Connectivity.getInstance(this).isNetworkConnected()){
                    get_saved_noticeBoard();
                    Toast.makeText(this, "Check your network connections", Toast.LENGTH_SHORT).show();
                }else{

                    getfiles();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
