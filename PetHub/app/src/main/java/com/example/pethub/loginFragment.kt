package com.example.pethub

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.pethub.R
import com.example.pethub.retrofit.LoginRequest
import com.example.pethub.retrofit.LoginResponse
import com.example.pethub.retrofit.RetrofitClient
import com.example.pethub.retrofit.TokenResponse
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var username: EditText
lateinit var password: EditText
lateinit var btnLogin: Button

/**
 * A simple [Fragment] subclass.
 * Use the [loginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class loginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)
        username = v.findViewById(R.id.edUserLogin)
        password = v.findViewById(R.id.editUserPassword)
        btnLogin = v.findViewById(R.id.button)
        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                login()
            }

        })
        return v
    }

    private fun getUsers() {
        RetrofitClient.apiDataInterface.getUsers().enqueue(object : retrofit2.Callback<MutableList<LoginResponse>> {
            override fun onResponse(
                call: Call<MutableList<LoginResponse>>,
                response: Response<MutableList<LoginResponse>>
            ) {
                val resp = response.body()
                Toast.makeText(activity!!.applicationContext, "Success", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<MutableList<LoginResponse>>, t: Throwable) {
                Toast.makeText(activity!!.applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun login() {
        val loginRequest = LoginRequest(username.text.toString().trim(), password.text.toString().trim())
        val loginResponseCall = RetrofitClient.apiDataInterface.login(loginRequest)
        loginResponseCall.enqueue(object : retrofit2.Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                try {
                    val token = response.body()
                    val status = response.code()
                    if (status != 401) {
                        val sharedPrefs = activity!!.getSharedPreferences(
                            "SharedPrefs",
                            AppCompatActivity.MODE_PRIVATE
                        )
                        sharedPrefs.edit().putString("token", token!!.access_token).apply()
                        Toast.makeText(activity!!.applicationContext, "Success", Toast.LENGTH_LONG)
                            .show()
                        val manager = fragmentManager
                        val transaction = manager!!.beginTransaction()
                        transaction.replace(R.id.fragment_container, profileFragment())
                        transaction.commit()
                    } else {
                        Toast.makeText(
                            activity!!.applicationContext,
                            "Invalid login or password",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (ex: Exception) {
                    Toast.makeText(activity?.applicationContext, "Error", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Toast.makeText(activity!!.applicationContext, "Failed", Toast.LENGTH_LONG).show()
                val manager = fragmentManager
                val transaction = manager!!.beginTransaction()
                transaction.replace(R.id.fragment_container, profileFragment())
                transaction.commit()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment loginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            loginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}