package com.example.pethub.retrofit

import retrofit2.Call
import retrofit2.http.*

interface ApiDataInterface {
    @POST("/auth/login")
    fun login(@Body loginInfo: LoginInfo) : Call<TokenResponse>

    @GET("/user/auth")
    fun getUserInfo(@Header("Authorization") auth: String) : Call<UserInfo>

    @GET("/ad")
    fun getAds() : Call<MutableList<Ad>>

    @POST("/ad/favorite/{id}")
    fun postFavAd(@Header("Authorization") auth: String, @Path("id") id: Int) : Call<Void>

    @GET("/ad/favorite")
    fun getFavAds(@Header("Authorization") auth: String) : Call<MutableList<Ad>>

    @POST("/ad")
    fun postAd(@Header("Authorization") auth: String, @Body adData : AdPost) : Call<Void>

    @DELETE("/ad/favorite/{id}")
    fun delFavAd(@Header("Authorization") auth: String, @Path("id") id: Int) : Call<Void>

    @POST("/user")
    fun signUp(@Body signUpInfo: SignUpInfo) : Call<Void>

 }