package com.example.pethub.repository

import android.app.Application
import android.content.Context
import com.example.pethub.retrofit.LoginRequest
import com.example.pethub.retrofit.RetrofitClient

class Repository {
    val api = RetrofitClient.apiDataInterface
    suspend fun login(loginRequest: LoginRequest) = api.login(loginRequest)
    suspend fun getUser(auth: String) = api.getUser(auth)
    suspend fun getAds() = api.getAds()
    suspend fun postFavAd(auth: String, id: Int) = api.postFavAd(auth, id)
}