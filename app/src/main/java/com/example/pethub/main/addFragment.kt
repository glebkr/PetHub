package com.example.pethub.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.retrofit.AdPost
import com.example.pethub.retrofit.Kind
import com.example.pethub.viewmodel.ViewModel
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
    val kindList = mutableListOf<String>("Выберите вид")
    val fullKindList = mutableListOf<Kind>()

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
        val items = arrayOf("Выберите тип","Продажа", "Покупка", "Утерян", "Найден")
        val spinnerAdTypeAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        spinnerAdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAdType.adapter = spinnerAdTypeAdapter
        var type : Int? = null
        spinnerAdType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                when (position) {
                    1 -> type = 1
                    2 -> type = 2
                    3 -> type = 3
                    4 -> type = 4
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        viewModel.getKinds()
        viewModel.kindList.observe(viewLifecycleOwner, Observer {
            for (item in it) {
                if (!kindList.contains(item.title)) {
                    item.title?.let { title -> kindList.add(title) }
                }
            }
            fullKindList.addAll(it)
        })
        val spinnerAdKindAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, kindList) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        spinnerAdKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAdKind.adapter = spinnerAdKindAdapter
        var kind : Int? = null
        spinnerAdKind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                for (item in fullKindList) {
                    if (item.title == kindList[position]) {
                        kind = item.id
                    }
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
                if (title.isNotEmpty() && location.isNotEmpty() && price.isNotEmpty() && type != null && kind != null) {
                    val adData = AdPost(title, type!!, kind!!, price, location, "Пушкина 33")
                    viewModel.postAd("Bearer " + token, adData)
                    titleTv.text = null
                    tvLocation.text = null
                    priceTv.text = null
                } else if (type == null) {
                    Toast.makeText(requireContext(), "Выберите тип объявления", Toast.LENGTH_SHORT).show()
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