package com.example.pethub.main

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.retrofit.LoginInfo
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.fragment_login.*

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
    private val viewModel by activityViewModels<ViewModel>()

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
        viewModel.token.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                findNavController().navigate(R.id.profileFragment)
                viewModel._token.postValue("")
                progress?.visibility = ProgressBar.INVISIBLE
            }
        })
        btnSignIn.setOnClickListener {
            progress?.visibility = ProgressBar.VISIBLE
            val loginInfo =
                LoginInfo(edUserLogin.text.toString().trim(), etUserPassword.text.toString().trim())
            viewModel.login(loginInfo)
            viewModel.isLoading.observe(viewLifecycleOwner, Observer {
                progress?.visibility = ProgressBar.INVISIBLE
            })
        }
        btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
        /*
        viewModel.progress.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                progress?.visibility = ProgressBar.VISIBLE
            } else {
                progress?.visibility = ProgressBar.INVISIBLE
            }
        })

         */
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