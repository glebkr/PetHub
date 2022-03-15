package com.example.pethub.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.pethub.R
import com.example.pethub.viewmodel.ViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_description.*
import kotlinx.android.synthetic.main.fragment_profile.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [descriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class descriptionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val viewModel by activityViewModels<ViewModel>()

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
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        tvAdTitle.text = sharedPrefs?.getString("title", "")
        tvAdPrice.text = "Цена: " + sharedPrefs?.getString("price", "")
        tvAdCity.text = "Город: " + sharedPrefs?.getString("city", "")
        if (!sharedPrefs?.getString("url","").isNullOrEmpty()) {
            Picasso.get().load(sharedPrefs?.getString("url","")).into(ivAd)
        }
        //Toast.makeText(requireContext(), "${arguments?.getString("title")}", Toast.LENGTH_SHORT).show()
        /*
        arguments?.let {
            tvAdTitle.text = it.getString("title", "lul")
            tvAdPrice.text = it.getString("price", "lul")
            tvAdCity.text = it.getString("city", "lul")
        }
         */

    }

    override fun onStop() {
        super.onStop()
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        sharedPrefs?.edit()?.putString("url","")?.apply()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment descriptionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(title: String?, price: String?, city: String?) =
            descriptionFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("price", price)
                    putString("city", city)
                }
            }
    }
}