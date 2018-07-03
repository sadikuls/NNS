package com.sadikul.nns.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sadikul.nns.Adapter.NoticeboardAdminAdapter;
import com.sadikul.nns.Model.DeleteRes.DeleteResponse;
import com.sadikul.nns.Model.Notice;
import com.sadikul.nns.Model.NoticeItem;
import com.sadikul.nns.R;
import com.sadikul.nns.Retrofit.ApiInterface;
import com.sadikul.nns.Retrofit.Connectivity;
import com.sadikul.nns.Retrofit.RetrofitClient;
import com.sadikul.nns.Utils.ObservableObject;
import com.sadikul.nns.Utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NoticeboardAdminAdapter.DeleteHandler,NavigationView.OnNavigationItemSelectedListener,Observer,View.OnClickListener {

    ProgressDialog pd=null;
    PreferenceManager preferenceManager=null;
    LinearLayout l_relative=null;
    Snackbar snakbar=null;
    NoticeboardAdminAdapter adapter;
    List<NoticeItem> notices;
    FloatingActionButton fab=null;
    RecyclerView noticeListRecyclerView=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        preferenceManager= PreferenceManager.getInstance(this);
        notices=new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("PTI Gaibandha");
        ObservableObject.getInstance().addObserver(this);
        l_relative= (LinearLayout) findViewById(R.id.content_main);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        noticeListRecyclerView= (RecyclerView) findViewById(R.id.noticeListRecyclerView);
        noticeListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab.setOnClickListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView= navigationView.getHeaderView(0);
        TextView name= (TextView) navHeaderView.findViewById(R.id.nav_name);
        TextView email= (TextView) navHeaderView.findViewById(R.id.nav_email);
        ImageView imageView= (ImageView) navHeaderView.findViewById(R.id.profileImage);




        if(!Connectivity.getInstance(this).isNetworkConnected()){
            get_saved_noticeBoard();
            showSnackbar("Please check your internet connection..");
        }else{

            get_noticeBoard();
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(Connectivity.getInstance(this).isNetworkConnected()){
            get_noticeBoard();
            //Toast.makeText(MainActivity.this, "Stablished connection succesfully...", Toast.LENGTH_SHORT).show();
        }
    }

    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(l_relative, message, Snackbar.LENGTH_LONG);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#5F9F4D"));
        snackbar.show();
    }

    public void get_noticeBoard(){
        pd.setMessage("Please Wait...");
        pd.show();
        ApiInterface apiInterface = RetrofitClient.getApiInterface();
        final Call<Notice> profileBasic = apiInterface.getNotices();
        profileBasic.enqueue(new Callback<Notice>() {
            @Override
            public void onResponse(Call<Notice> call, Response<Notice> response) {
                if (response.isSuccessful()) {
                    pd.dismiss();
                    try {

                        preferenceManager.setNotices(response.body());
                        notices=response.body().getNotice();
                        if(notices!=null){

                            adapter=new NoticeboardAdminAdapter(MainActivity.this,notices);
                            noticeListRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        //Notice noticess =response.body();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Notice> call, Throwable t) {

                pd.dismiss();
                get_saved_noticeBoard();
                Toast.makeText(getApplicationContext(), "Please check your internet connection...", Toast.LENGTH_SHORT).show();
                Log.e("log_in", t + "");

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                //Toast.makeText(MainActivity.this, "fab button clicked...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,UploadForm.class));
                finish();
                break;
        }
    }

    public void get_saved_noticeBoard() {
        notices=preferenceManager.getNotices().getNotice();
        if(notices!=null){
            adapter=new NoticeboardAdminAdapter(MainActivity.this,notices);
            noticeListRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void delete(String id) {
        pd.setMessage("Please Wait...");
        pd.show();
        ApiInterface apiInterface=RetrofitClient.getApiInterface();
        Call<DeleteResponse> call=apiInterface.delete_by_ID(id);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if(response.isSuccessful()){
                    pd.dismiss();
                    Log.e("Test",response.body().getMsg());
                    DeleteResponse deleteResponse=response.body();
                    if(deleteResponse.getMsg().equals("Success")){
                        notices=response.body().getData();
                        if(notices==null)
                            notices=new ArrayList<NoticeItem>();

                            adapter=new NoticeboardAdminAdapter(MainActivity.this,notices);
                            noticeListRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(MainActivity.this, "Please Try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
        //Toast.makeText(this, "Mainactivity knocked "+id, Toast.LENGTH_SHORT).show();
    }
}

   /*       snakbar=Snackbar.make(l_relative, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Connectivity.getInstance(MainActivity.this).isNetworkConnected()){
                                Toast.makeText(MainActivity.this, "Connected...", Toast.LENGTH_SHORT).show();
                                if(snakbar.isShown()){
                                    snakbar.dismiss();
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                                if(snakbar.isShown()){
                                    snakbar.setDuration(10000);
                                }
                            }
                        }
                    });
            snakbar.setActionTextColor(Color.YELLOW).show();*/

