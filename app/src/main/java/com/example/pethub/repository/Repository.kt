package com.example.pethub.repository

import com.example.pethub.retrofit.*

class Repository {
    val api = RetrofitClient.apiDataInterface
    suspend fun login(loginInfo: LoginInfo) = api.login(loginInfo)
    suspend fun getUserInfo(auth: String) = api.getUserInfo(auth)
    suspend fun getAds() = api.getAds()
    suspend fun postFavAd(auth: String, id: Int) = api.postFavAd(auth, id)
    suspend fun getFavAd(auth: String) = api.getFavAds(auth)
    suspend fun delFavAd(auth: String, id: Int) = api.delFavAd(auth, id)
    suspend fun postAd(auth: String, adData: AdPost) = api.postAd(auth, adData)
    suspend fun signUp(signUpInfo: SignUpInfo) = api.signUp(signUpInfo)
}