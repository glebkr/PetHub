package com.example.pethub.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiDataInterface {
    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest) : Call<TokenResponse>

    @GET("/user")
    fun getUsers() : Call<MutableList<LoginResponse>>
}