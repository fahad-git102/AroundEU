package com.fahadandroid.groupchat.ApiCall;

import com.fahadandroid.groupchat.models.CompanyApiModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIMethodInterface {
    @GET("v1/3rl7kpffachn3")
    Call<List<CompanyApiModel>> getCompanies();
}
