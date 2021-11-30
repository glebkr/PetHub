package com.example.pethub.retrofit

import retrofit2.Call
import retrofit2.http.*

interface ApiDataInterface {
    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest) : Call<TokenResponse>

    @GET("/user/auth")
    fun getUser(@Header("Authorization") auth: String) : Call<LoginResponse>

    @GET("/ad")
    fun getAds() : Call<MutableList<Feed>>

    @POST("/ad/favorite/{id}")
    fun postFavAd(@Header("Authorization") auth: String, @Path("id") id: Int) : Call<Void>

}