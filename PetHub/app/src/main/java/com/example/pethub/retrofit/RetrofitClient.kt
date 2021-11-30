package com.example.pethub.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val base_url = "https://pethub-project.herokuapp.com"
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String?) : Retrofit {
        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                //.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    val apiDataInterface : ApiDataInterface
    get() = getClient(base_url).create(ApiDataInterface::class.java)
}