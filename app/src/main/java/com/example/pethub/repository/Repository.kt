package com.example.pethub.repository

import com.example.pethub.retrofit.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

class Repository {
    val api = RetrofitClient.apiDataInterface
    suspend fun login(loginInfo: LoginInfo) = api.login(loginInfo)
    suspend fun getUserInfo(auth: String) = api.getUserInfo(auth)
    suspend fun getAds() = api.getAds()
    suspend fun postFavAd(auth: String, id: Int) = api.postFavAd(auth, id)
    suspend fun getFavAd(auth: String) = api.getFavAds(auth)
    suspend fun delFavAd(auth: String, id: Int) = api.delFavAd(auth, id)
    suspend fun postAd(
        auth: String, title: RequestBody, typeId:RequestBody, animalId: RequestBody, city: RequestBody,
        image: MultipartBody.Part?, x_coord: RequestBody, y_coord: RequestBody, price: RequestBody, description: RequestBody) = api.postAd(auth, title, typeId, animalId, city,
    image, x_coord, y_coord, price, description)
    suspend fun signUp(name: RequestBody, password: RequestBody, phone: RequestBody,
                       login: RequestBody, email: RequestBody, city: RequestBody, image: MultipartBody.Part?) = api.signUp(name, password, phone, login, email, city, image)
    suspend fun getKinds() = api.getKinds()
    suspend fun fullFilter(type: Int? = null, kind: Int? = null, city: String? = null) = api.fullFilter(type, kind,city)
    suspend fun getUsersAds(auth: String) = api.getUsersAds(auth)
    suspend fun deleteUsersAd(auth: String, id: Int) = api.deleteUsersAd(auth, id)
    suspend fun updateUsersAd(auth: String, id: Int, title: RequestBody, typeId: RequestBody, animalId: RequestBody, city: RequestBody,
                              image: MultipartBody.Part?, x_coord: RequestBody, y_coord: RequestBody, price: RequestBody, description: RequestBody) = api.updateUsersAd(auth, id, title, typeId, animalId, city, image, x_coord, y_coord, price, description)
    suspend fun updateUser(auth: String,  name: RequestBody, phone: RequestBody, login: RequestBody, email: RequestBody,
                           city: RequestBody, image: MultipartBody.Part?) = api.updateUser(auth, name, phone, login, email, city, image)
}