package com.sadikul.nns.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sadikul.nns.Model.NoticeItemDetails;
import com.sadikul.nns.Model.NoticesDetails;
import com.sadikul.nns.R;
import com.sadikul.nns.Retrofit.ApiInterface;
import com.sadikul.nns.Retrofit.RetrofitClient;
import com.sadikul.nns.Utils.Constant;
import com.sadikul.nns.Utils.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileDetails extends AppCompatActivity {
    @BindView(R.id.attatched)
    TextView attatched;
    // Progress Dialog
    private ProgressDialog pDialog;
    File file;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    PreferenceManager preferenceManager = null;
    ProgressDialog mProgressDialog;
    @BindView(R.id.messageTitle)
    TextView messageTitle;
    @BindView(R.id.messageBody)
    TextView messageBody;
    NoticeItemDetails noticeItemDetails = null;
    String id = null;
    @BindView(R.id.file_download)
    TextView fileDownload;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.image_container)
    RelativeLayout imageContainer;
    @BindView(R.id.noticeDetailsToolbar)
    Toolbar noticeDetailsToolbar;
    @BindView(R.id.imageview_downloader)
    FloatingActionButton imageviewDownloader;
    String imageDownloadUrl = "", filedownloadUrl = "";
    boolean result = false;

    SharedPreferences mSharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);
        ButterKnife.bind(this);
        setSupportActionBar(noticeDetailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details");

        preferenceManager = PreferenceManager.getInstance(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        id = getIntent().getStringExtra("id");
        Log.e("id+++++++", id + "+++");
        innitializeAll();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_board_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh: {
                innitializeAll();
                break;
            }
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Please Check internet connection");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



    public void innitializeAll() {
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getApiInterface();
        final Call<NoticesDetails> profileBasic = apiInterface.getNoticesDetails(id);
        profileBasic.enqueue(new Callback<NoticesDetails>() {
            @Override
            public void onResponse(Call<NoticesDetails> call, Response<NoticesDetails> response) {
                if (response.isSuccessful()) {
                    Log.e("success+++++++", response.body().getNotice().get(0).getImageLink() + " details=" + response.body().getNotice().get(0).getPdfLink());
                    mProgressDialog.dismiss();
                    try {

                        List<NoticeItemDetails> notice = response.body().getNotice();

                        if (notice != null) {
                            NoticeItemDetails noticeItemDetails = notice.get(0);
                            messageTitle.setText(noticeItemDetails.getTitle());
                            messageBody.setText(noticeItemDetails.getDescript());

                            preferenceManager.setImageLink(noticeItemDetails.getImageLink());
                            preferenceManager.setPdfLink(noticeItemDetails.getPdfLink());

                            Log.e("array", "image=" + noticeItemDetails.getImageLink() + " file" + noticeItemDetails.getPdfLink());
                            if (noticeItemDetails.getPdfLink().equals("")) {
                                fileDownload.setVisibility(View.GONE);
                                attatched.setVisibility(View.GONE);
                            } else {
                                fileDownload.setVisibility(View.VISIBLE);
                                attatched.setVisibility(View.VISIBLE);
                            }


                            fileDownload.setPaintFlags(fileDownload.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            fileDownload.setText(noticeItemDetails.getPdfLink().replace("files/", ""));

                            if (noticeItemDetails.getImageLink().equals("")) {
                                imageContainer.setVisibility(View.GONE);
                            } else {
                                imageContainer.setVisibility(View.VISIBLE);
                            }


                            Glide.with(FileDetails.this).load(Constant.baseURL + noticeItemDetails.getImageLink()).placeholder(R.drawable.ic_file_gray_116dp).fitCenter().dontAnimate().into(image);

                        }

                        //NoticesDetails noticess =response.body();
                        //preferenceManager.setNotices(noticess);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FileDetails.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<NoticesDetails> call, Throwable t) {

                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Please check your internet connection...", Toast.LENGTH_SHORT).show();
                Log.e("log_in", t + "");

            }
        });
    }


    @OnClick({R.id.imageview_downloader, R.id.file_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageview_downloader:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK}, Constant.notice_detailsstoragePermissonCode);
                    break;
                } else {
                    /*final DownloadTask downloadTask = new DownloadTask(this);

                    downloadTask.execute(preferenceManager.getImageLink());
                    Log.e("down1+++",preferenceManager.getImageLink());*/
                    new DownloadFileFromURL().execute(preferenceManager.getImageLink());
                }
                break;
            case R.id.file_download:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK}, Constant.notice_detailsstoragePermissonCode);
                    break;
                } else {
                   /* final DownloadTask downloadTask = new DownloadTask(this);
                    downloadTask.execute(preferenceManager.getPdfLink());

                    Log.e("down2+++",preferenceManager.getPdfLink());*/

                    new DownloadFileFromURL().execute(preferenceManager.getPdfLink());
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.notice_detailsstoragePermissonCode: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //takeImageFromGalery();
                } else {
                    Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Download on progress...");
                pDialog.setTitle(" Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
/*
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(Constant.baseURL+sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                Log.e("url",sUrl[0]);
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                File root = Environment.getExternalStorageDirectory();
                File myDir = new File(root.getAbsolutePath() + "/gainbandhaPTI");
                if(!myDir.exists())
                    myDir.mkdirs();
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                String filename = sUrl[0];
                filename=filename.replace(filename.substring(0,filename.lastIndexOf("/")+1),"");

                Random random=new Random();
                int v=random.nextInt(100);

                Log.e("filename",filename+" ");
                String filenameArray[] = filename.split("\\.");
                String extension = filenameArray[filenameArray.length-1];
                String main_name=filename.replace("."+extension,"");
                Log.e("filename2",filename+" name="+main_name+" =exten- "+extension);

                String fname = "/"+main_name+v+"."+extension;

                Log.e("originalfname",fname);
                File file = new File (myDir, fname);



                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
*/

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(Constant.baseURL + f_url[0]);

                Log.e("url", url.toString());
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                Random random = new Random();
                int v = random.nextInt(100);

                String filename = url.toString();
                filename = filename.replace(filename.substring(0, filename.lastIndexOf("/") + 1), "");

                File root = Environment.getExternalStorageDirectory();
                File myDir = new File(root.getAbsolutePath() + "/gainbandhaPTI");
                if (!myDir.exists())
                    myDir.mkdirs();

                Log.e("filename", filename + " ");
                String filenameArray[] = filename.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                String main_name = filename.replace("." + extension, "");
                Log.e("filename2", filename + " name=" + main_name + " =exten- " + extension);

                String fname = "/" + main_name + v + "." + extension;

                Log.e("originalfname", fname);
                file = new File(myDir, fname);

                // Output stream to write file
                //OutputStream output = new FileOutputStream("/sdcard/"+fname);
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                result = false;
            }

            return null;
        }

        /**
         * Updating progress bar
         */

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            if (!result) {
                Toast.makeText(FileDetails.this, "Image saved into" + file, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FileDetails.this, "Error in downloading image", Toast.LENGTH_SHORT).show();
            }

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
            //my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }

    }
}