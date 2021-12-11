package com.example.pethub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pethub.retrofit.AdPost
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.feed_item.*
import kotlinx.android.synthetic.main.fragment_add.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class addFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")
        val items = arrayOf("Продажа", "Покупка", "Утерян", "Найден")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = spinnerAdapter
        var type : Int? = null
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                when (position) {
                    0 -> type = 1
                    1 -> type = 2
                    2 -> type = 3
                    3 -> type = 4
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        if (token!!.isNotEmpty()) {
            bthAdd.setOnClickListener {
                val title = titleTv.text.toString().trim()
                val location = tvLocation.text.toString().trim()
                val price = priceTv.text.toString().trim()
                if (title.isNotEmpty() && location.isNotEmpty() && price.isNotEmpty()) {
                    val adData = AdPost(title, type!!, 1, price, location, "Пушкина 33")
                    viewModel.postAd("Bearer " + token, adData)
                    titleTv.text = null
                    tvLocation.text = null
                    priceTv.text = null
                } else {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            findNavController().navigate(R.id.loginFragment)
            Toast.makeText(requireContext(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment addFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            addFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}