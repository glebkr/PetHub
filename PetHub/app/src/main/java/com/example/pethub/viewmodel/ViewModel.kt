package com.example.pethub.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pethub.repository.Repository
import com.example.pethub.retrofit.LoginRequest
import com.example.pethub.retrofit.LoginResponse
import com.example.pethub.retrofit.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import javax.security.auth.callback.Callback
import androidx.lifecycle.ViewModel
import com.example.pethub.retrofit.Feed

class ViewModel(application: Application): AndroidViewModel(application) {
    val rep = Repository()
    val context = getApplication<Application>().applicationContext
    val sharedPrefs = getApplication<Application>().getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
    val _progress = MutableLiveData<Boolean>(false)
    val progress : LiveData<Boolean> = _progress
    val _name = MutableLiveData<String>()
    val name : LiveData<String> = _name
    val _login = MutableLiveData<String>()
    val login : LiveData<String> = _login
    val _token = MutableLiveData<String>()
    val token : LiveData<String> = _token
    val _feedList = MutableLiveData<MutableList<Feed>>()
    val feedList = _feedList

    fun login(loginRequest: LoginRequest) {
        _progress.postValue(true)
        viewModelScope.launch {
            try {
                rep.login(loginRequest).enqueue(object : retrofit2.Callback<TokenResponse> {
                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        val resp = response.body()
                        if (response.code() != 401) {
                            sharedPrefs!!.edit().putString("token", resp?.access_token).apply()
                            _token.postValue(resp?.access_token)
                        } else {
                            Toast.makeText(context, "Invalid login or password", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(context, "Failed, try again", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch(ex: Exception) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            } finally {
                _progress.postValue(false)
            }
        }
    }

    fun getUser(auth: String) {
        _progress.postValue(true)
        viewModelScope.launch {
            try {
                rep.getUser(auth).enqueue(object : retrofit2.Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val resp = response.body()
                        _name.postValue(resp?.name)
                        _login.postValue(resp?.login)
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(context, "Failed, try again", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            } finally {
                _progress.postValue(false)
            }
        }
    }

    fun getAds() {
        _progress.postValue(true)
        viewModelScope.launch {
            try {
                rep.getAds().enqueue(object : retrofit2.Callback<MutableList<Feed>> {
                    override fun onResponse(
                        call: Call<MutableList<Feed>>,
                        response: Response<MutableList<Feed>>
                    ) {
                        val resp = response.body()
                        _feedList.postValue(resp)
                    }

                    override fun onFailure(call: Call<MutableList<Feed>>, t: Throwable) {
                        Toast.makeText(context, "Failed, try again", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch(ex: Exception) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            } finally {
                _progress.postValue(false)
            }
        }
    }

    fun postFavAd(auth: String, id: Int) {
        _progress.postValue(true)
        viewModelScope.launch {
            try {
                rep.postFavAd(auth, id).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        val resp = response.body()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Failed, try again", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            } finally {
                _progress.postValue(false)
            }
        }
    }
}
