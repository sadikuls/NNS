package com.sadikul.nns.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.sadikul.nns.R;
import com.sadikul.nns.Utils.Constant;
import com.sadikul.nns.Utils.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadForm extends AppCompatActivity {

    ProgressDialog progresDialog;
    PreferenceManager mPreferenceManager = null;
    private static final int READ = 123;
    private static final int PICK_IMAGE = 111;
    String imageBase64 = null;
    Toolbar toolbar;
    boolean file_send_tag=false;
    boolean result_tag = false;
    LinearLayout chooserOptions;
    Uri selectedImage = null;
    Bitmap cameraPhoto;
    String pathofBmp;
    @BindView(R.id.noticeSubject)
    EditText noticeSubject;
    @BindView(R.id.notice_description)
    EditText descriptionOfNotice;
    @BindView(R.id.NoticeImageview)
    ImageView NoticeImageview;

    String notice_subject = "", notice_description = "";
    private final int file_pick_request = 10;
    Intent file_data = null;
    @BindView(R.id.attatchedFileName)
    TextView attatchedFileName;

    //File f = new File(file_data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
    String content_type = null;
    String filePath = null;
    File f=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice_from);
        ButterKnife.bind(this);
        mPreferenceManager = PreferenceManager.getInstance(this);


        progresDialog = new ProgressDialog(UploadForm.this);

        progresDialog.setMessage("Please Wait..");
        progresDialog.setTitle("Sending message..");
        progresDialog.setCancelable(false);

        chooserOptions = (LinearLayout) findViewById(R.id.choogeImageOptions);

        toolbar = (Toolbar) findViewById(R.id.noticeformtoolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Compose");


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UploadForm.this,MainActivity.class));
        finish();
    }


    private void chooseImageFromGalery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constant.selectimagecode);
    }

    private void captureimage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.from_galey:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constant.storagePermissonCode);
                    break;
                } else {
                    chooseImageFromGalery();
                }
                break;
            case R.id.takePhotoWcamera:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constant.captureimagePermissionCode);
                } else {

                    captureimage();
                }
                break;
            case R.id.menuid_file:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constant.file_pick_request);
                } else {
                    pick_file();
                }

                break;
            case R.id.send: {
                notice_subject = noticeSubject.getText().toString();
                notice_description = descriptionOfNotice.getText().toString();

                if (notice_subject.equals("")) {
                    Toast.makeText(UploadForm.this, "Subject can not be empty", Toast.LENGTH_SHORT).show();
                    //progresDialog.dismiss();
                    break;
                }
                if (notice_description.equals("")) {
                    Toast.makeText(UploadForm.this, "Message body can not be empty", Toast.LENGTH_SHORT).show();
                    //progresDialog.dismiss();
                    break;
                }

                /*if (!file_flag) {
                    Toast.makeText(UploadForm.this, "Please attatch a file", Toast.LENGTH_SHORT).show();
                   // progresDialog.dismiss();
                    break;
                }*/

                upload();

                break;
            }
            case android.R.id.home: {
                startActivity(new Intent(UploadForm.this,MainActivity.class));
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.storagePermissonCode: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takeImageFromGalery();
                } else {
                    Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case Constant.captureimagePermissionCode: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    captureimage();
                } else {
                    Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case Constant.checkpermissionForMediastore: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    managingCameraRequest();
                } else {
                    Toast.makeText(getApplication(), "You Denied permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case Constant.file_pick_request: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pick_file();
                } else {
                    Toast.makeText(getApplication(), "You Denied permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void pick_file() {
        new MaterialFilePicker()
                .withActivity(this)
                //.withFilter(Pattern.compile(".*\\.progresDialogf$"))
                .withRequestCode(file_pick_request)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case Constant.selectimagecode: {
                if (resultCode == RESULT_OK) {

                    selectedImage = imageReturnedIntent.getData();
                    //String path = selectedImage.getPath();
                    decodeUri(selectedImage);


                }
                break;
            }
            case Constant.CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    cameraPhoto = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    //selectedImage= (Uri) imageReturnedIntent.getExtras().get("data");

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.checkpermissionForMediastore);
                        return;
                    }
                    managingCameraRequest();

                }
                break;
            }
            case file_pick_request: {
                if (requestCode == file_pick_request && resultCode == RESULT_OK) {
                    file_data = imageReturnedIntent;
                    attatchedFileName.setVisibility(View.VISIBLE);
                    f = new File(file_data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    //String filePath = f.getAbsolutePath();
                    //File f = new File(file_data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));

                    content_type = getMimeType(f.getPath());
                    filePath = f.getAbsolutePath();
                    attatchedFileName.setText(filePath.replace(filePath.substring(0,filePath.lastIndexOf("/")+1),""));
                    //file_flag = true;
                }
                break;
            }


        }
    }

    public void managingCameraRequest() {
        Random r = new Random();
        int number = r.nextInt();
        pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), cameraPhoto, "sendImage" + number, null);
        selectedImage = Uri.parse(pathofBmp);
        imageBase64 = toBitmapString(cameraPhoto);
        Log.e("CameraUri", selectedImage + " " + imageBase64);
        NoticeImageview.setImageBitmap(cameraPhoto);
    }


    private void takeImageFromGalery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }


    public void decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            imageBase64 = toBitmapString(bitmap);

            NoticeImageview.setVisibility(View.VISIBLE);
            Log.e("imageBase64", imageBase64);
            //imageView.setVisibility(View.VISIBLE);
            NoticeImageview.setImageBitmap(bitmap);
            Log.e("FNF", imageBase64 + "");
        } catch (IOException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
    }

    public String toBitmapString(Bitmap copressimage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copressimage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imagebyte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagebyte, Base64.DEFAULT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progresDialog.dismiss();
    }

    public void upload() {
        // final Intent myData=file_data;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progresDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                /*File f = new File(file_data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                String content_type = getMimeType(f.getPath());
                String filePath = f.getAbsolutePath();*/

                OkHttpClient client = new OkHttpClient();

                if (imageBase64 == null)
                    imageBase64 = "faka";

                RequestBody request_body=null;
                if(filePath!=null){

                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);
                    request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("upload_file", filePath.substring(filePath.lastIndexOf("/") + 1), file_body)
                            .addFormDataPart("message_title", notice_subject)
                            .addFormDataPart("avater", imageBase64 + "")
                            .addFormDataPart("message_body", notice_description)
                            .build();
                    Log.e("upload1", notice_subject + " des" + notice_description + " " + imageBase64 + " file" + f.toString());

                }else{
                    request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("message_title", notice_subject)
                            .addFormDataPart("avater", imageBase64 + "")
                            .addFormDataPart("message_body", notice_description)
                            .build();
                }




                Request request = new Request.Builder()
                        .url(Constant.baseURL + Constant.image_with_file)
                        .post(request_body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {

                        Log.e("res", response.body().string().toString()+"");
                        throw new IOException("Error :" + response);

                    } else {
                        result_tag = true;
                        Log.e("res", response.body().string());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                progresDialog.dismiss();
                if (result_tag) {
                    Toast.makeText(UploadForm.this, "Successfully send message...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UploadForm.this,MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(UploadForm.this,"Something went wrong... Please try again", Toast.LENGTH_LONG).show();

                }
            }
        }.execute();
    }
/*
    private void send_push() {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progresDialog.setMessage("Sending push..");
            }

            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client=new OkHttpClient();


                RequestBody request_body=new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("message",notice_subject)
                        .addFormDataPart("title","PTI Gaibandha")
                        .build();

                Request request=new Request.Builder()
                        .url(Constant.baseURL+Constant.send_push)
                        .post(request_body)
                        .build();

                try {
                    Response response=client.newCall(request).execute();
                    if(!response.isSuccessful()){

                        progresDialog.dismiss();
                        //Toast.makeText(UploadForm.this,"Something went wrong...",Toast.LENGTH_LONG).show();
                        throw new IOException("Error :"+response);

                    }else{
                        Log.e("respos",response.body().string());
                        //startActivity(new Intent(UploadForm.this,DashBoard.class));

                    }

                } catch (IOException e) {
                    e.printStackTrace();

                    //Toast.makeText(UploadForm.this,"Something went wrong..."+e,Toast.LENGTH_LONG).show();
                    Log.e("respos",e+"");
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progresDialog.dismiss();
              */
/*  try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*//*

                Toast.makeText(UploadForm.this, "Successfully send message...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UploadForm.this,MainActivity.class));
                finish();
            }
        }.execute();
    }
*/

    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}

