package com.example.pethub

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.retrofit.LoginRequest
import com.example.pethub.retrofit.LoginResponse
import com.example.pethub.retrofit.RetrofitClient
import com.example.pethub.retrofit.TokenResponse
import com.example.pethub.viewmodel.ViewModel
import okhttp3.internal.waitMillis
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [loginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class loginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var btnLogin: Button
    private var progress : ProgressBar? = null
    private val viewModel by activityViewModels<ViewModel>()
    //private val viewModel = ViewModelProvider(this).get(ViewModel::class.java)

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username = view.findViewById(R.id.edUserLogin)
        password = view.findViewById(R.id.editUserPassword)
        btnLogin = view.findViewById(R.id.button)
        progress = view.findViewById(R.id.progress)
        val sharedPrefs =
            activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        viewModel.token.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                findNavController().navigate(R.id.profileFragment)
                viewModel._token.postValue("")
            }
        })
        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val token = sharedPrefs?.getString("token", "")
                val loginRequest =
                    LoginRequest(username.text.toString().trim(), password.text.toString().trim())
                if (token!!.isNotEmpty()) {
                    findNavController().navigate(R.id.profileFragment)
                } else {
                    viewModel.login(loginRequest)
                }

            }
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                progress?.visibility = ProgressBar.VISIBLE
            } else {
                progress?.visibility = ProgressBar.INVISIBLE
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