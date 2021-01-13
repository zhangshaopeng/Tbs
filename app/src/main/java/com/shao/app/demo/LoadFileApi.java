package com.shao.app.demo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface LoadFileApi {
    @GET
    Call<ResponseBody> loadPdfFile(@Url String fileUrl);
}