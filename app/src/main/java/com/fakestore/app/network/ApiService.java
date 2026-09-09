package com.fakestore.app.network;

import com.fakestore.app.model.LoginRequest;
import com.fakestore.app.model.LoginResponse;
import com.fakestore.app.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("users")
    Call<List<User>> getUsers();
}
