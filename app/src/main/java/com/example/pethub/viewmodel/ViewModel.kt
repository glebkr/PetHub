package com.example.pethub.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pethub.repository.Repository
import com.example.pethub.retrofit.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class ViewModel(application: Application): AndroidViewModel(application) {
    val rep = Repository()
    val context = getApplication<Application>().applicationContext
    val sharedPrefs = getApplication<Application>().getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
    val _userInfo = MutableLiveData<UserInfo>()
    val userInfo : LiveData<UserInfo> = _userInfo
    val _token = MutableLiveData<String>()
    val token : LiveData<String> = _token
    val _adList = MutableLiveData<MutableList<Ad>>()
    val adList = _adList
    val _favAdList = MutableLiveData<MutableList<Ad>>()
    val favAdList = _favAdList
    val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean> = _isLoading

    fun login(loginInfo: LoginInfo) {
        viewModelScope.launch {
            try {
                rep.login(loginInfo).enqueue(object : retrofit2.Callback<TokenResponse> {
                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        val resp = response.body()
                        if (response.code() != 401) {
                            sharedPrefs!!.edit().putString("token", resp?.access_token).apply()
                            _token.postValue(resp?.access_token)
                        } else {
                            Toast.makeText(context, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                            _isLoading.postValue(false)
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch(ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getUserInfo(auth: String) {
        viewModelScope.launch {
            try {
                rep.getUserInfo(auth).enqueue(object : retrofit2.Callback<UserInfo> {
                    override fun onResponse(
                        call: Call<UserInfo>,
                        response: Response<UserInfo>
                    ) {
                        val resp = response.body()
                        _userInfo.postValue(resp)
                    }

                    override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAds() {
        viewModelScope.launch {
            try {
                rep.getAds().enqueue(object : retrofit2.Callback<MutableList<Ad>> {
                    override fun onResponse(
                        call: Call<MutableList<Ad>>,
                        response: Response<MutableList<Ad>>
                    ) {
                        val resp = response.body()
                        _adList.postValue(resp)
                    }

                    override fun onFailure(call: Call<MutableList<Ad>>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch(ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun postFavAd(auth: String, id: Int) {
        viewModelScope.launch {
            try {
                rep.postFavAd(auth, id).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getFavAds(auth: String) {
        viewModelScope.launch {
            try {
                rep.getFavAd(auth).enqueue(object : retrofit2.Callback<MutableList<Ad>> {
                    override fun onResponse(
                        call: Call<MutableList<Ad>>,
                        response: Response<MutableList<Ad>>
                    ) {
                        if (response.code() != 401) {
                            val resp = response.body()
                            _favAdList.postValue(resp)
                        } else {
                            Toast.makeText(context, "Вы не авторизованы", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Ad>>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun delFavAd(auth: String, id: Int) {
        viewModelScope.launch {
            try {
                rep.delFavAd(auth, id).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun postAd(auth: String, adData: AdPost) {
        viewModelScope.launch {
            try {
                rep.postAd(auth, adData).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(context, "Объявление создано!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT).show()

                    }

                })
            } catch(ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signUp(signUpInfo: SignUpInfo) {
        viewModelScope.launch {
            try {
                rep.signUp(signUpInfo).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Неудача, попробуйте еще раз", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            } catch (ex: Exception) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }
 }
