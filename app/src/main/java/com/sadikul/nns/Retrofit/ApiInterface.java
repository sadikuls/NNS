package com.sadikul.nns.Retrofit;


import com.sadikul.nns.Model.DeleteRes.DeleteResponse;
import com.sadikul.nns.Model.Login_model;
import com.sadikul.nns.Model.Notice;
import com.sadikul.nns.Model.NoticesDetails;
import com.sadikul.nns.Utils.Constant;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {
    //public static final String url=Constants.medicineListURL;

    @POST(Constant.files)
    Call<Notice> getNotices();

    @POST(Constant.files_by_id)
    Call<NoticesDetails> getNoticesDetails(@Query("id") String id);

    @POST(Constant.login)
    Call<Login_model> log_in(@Query("username") String username,
                             @Query("password") String password);


    @POST(Constant.DeleteByID)
    Call<DeleteResponse> delete_by_ID(@Query("id") String id);

}
