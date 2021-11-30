package com.example.pethub

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.retrofit.LoginResponse
import com.example.pethub.retrofit.RetrofitClient
import com.example.pethub.viewmodel.ViewModel
import okhttp3.internal.waitMillis
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var progress: ProgressBar? = null
    private var tvName: TextView? = null
    private var tvLogin: TextView? = null
    private var name = ""
    private var login = ""
    private val viewModel by activityViewModels<ViewModel>()
    //private val viewModel = ViewModelProvider(this).get(ViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvName = view.findViewById(R.id.tvUserName)
        tvLogin = view.findViewById(R.id.tvLogin)
        progress = view.findViewById(R.id.progress)
        viewModel.name.observe(viewLifecycleOwner, Observer {
            tvName?.text = it
        })
        viewModel.login.observe(viewLifecycleOwner, Observer {
            tvLogin?.text = it
        })
        viewModel.progress.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                progress?.visibility = ProgressBar.VISIBLE
            } else {
                progress?.visibility = ProgressBar.INVISIBLE
            }
        })
        val sharedPrefs =
            activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs!!.getString("token", "")
        Log.e("TOKEN", token!!)
        if (token.isEmpty()) {
            findNavController().navigate(R.id.loginFragment)
        } else {
            viewModel.getUser("Bearer " + token)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.logOut).isVisible = true;
        super.onPrepareOptionsMenu(menu)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}