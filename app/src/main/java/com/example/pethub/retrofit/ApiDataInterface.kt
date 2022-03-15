package com.example.pethub.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @POST("/ad")
    fun postAd(@Header("Authorization") auth: String, @Part("title") title: RequestBody, @Part("type_id") typeId: RequestBody, @Part("animal_kind") animalId: RequestBody, @Part("city") city: RequestBody,
               @Part image: MultipartBody.Part, @Part("x_coord") x_coord: RequestBody, @Part("y_coord") y_coord: RequestBody, @Part("price") price: RequestBody, @Part("description") description: RequestBody) : Call<Void>

    @DELETE("/ad/favorite/{id}")
    fun delFavAd(@Header("Authorization") auth: String, @Path("id") id: Int) : Call<Void>

    @POST("/user")
    fun signUp(@Body signUpInfo: SignUpInfo) : Call<Void>

    @GET("/kind")
    fun getKinds() : Call<MutableList<Kind>>

    @GET("/ad")
    fun fullFilter(@Query("type") type: Int? = null, @Query("kind") kind: Int? = null, @Query("city") city: String? = null) : Call<MutableList<Ad>>

    @GET("/ad/user")
    fun getUsersAds(@Header("Authorization") auth: String) : Call<MutableList<Ad>>

    @DELETE("/ad/{id}")
    fun deleteUsersAd(@Header("Authorization") auth: String, @Path("id") id: Int) : Call<Void>

    @PATCH("/ad/{id}")
    fun updateUsersAd(@Header("Authorization") auth: String, @Path("id") id: Int, @Body adData: AdPost) : Call<Void>

    @PATCH("/user")
    fun updateUser(@Header("Authorization") auth: String, @Body userUpdateInfo: UserUpdateInfo) : Call<Void>
 }