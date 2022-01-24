package com.example.pethub.main

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.retrofit.Ad
import com.example.pethub.retrofit.Kind
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.fragment_filter.*
import okhttp3.internal.assertThreadDoesntHoldLock

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [filterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class filterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val viewModel by activityViewModels<ViewModel>()
    var kindList = mutableListOf<String>("Выберите вид")

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
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typeItems = arrayOf("Выберите тип","Продажа", "Покупка", "Утерян", "Найден")
        val spinnerTypeAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, typeItems) {
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
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = spinnerTypeAdapter
        var type : Int? = null
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    item.title?.let { title -> kindList.add(title)}
                }
            }
        })
        val spinnerKindAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, kindList) {
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
        spinnerKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKind.adapter = spinnerKindAdapter
        var kind: Int? = null
        spinnerKind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                for (i in 1 until kindList.size) {
                    when (position) {
                        i -> kind = i
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        resultBtn.setOnClickListener {
            if (type != null && kind != null) {
                viewModel.fullFilter(type!!, kind!!)
            } else if (type != null && kind == null) {
                viewModel.filterWithType(type!!)
            } else if (type == null && kind != null) {
                viewModel.filterWithKind(kind!!)
            } else if (type == null && kind == null) {
                viewModel.getAds()
                viewModel.adList.observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        val newList = mutableListOf<Ad>()
                        fun filter(text: String?) {
                            for (item in it) {
                                if (item.title!!.lowercase().contains(text!!.lowercase())) {
                                    newList.add(item)
                                }
                            }
                        }
                        if (searchView.query.isNotEmpty()) {
                            filter(searchView.query.toString())
                        }
                        if (newList.isNotEmpty()) {
                            viewModel._adList.postValue(newList)
                        } else {
                            viewModel._adList.postValue(it)
                        }
                        findNavController().navigate(R.id.homeFragment)
                        //viewModel._adList.postValue(null)
                        //newList.clear()
                    }
                } )
            }

            viewModel.filteredList.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    val newList = mutableListOf<Ad>()
                    fun filter(text: String?) {
                        for (item in it) {
                            if (item.title!!.lowercase().contains(text!!.lowercase())) {
                                newList.add(item)
                            }
                        }
                    }
                    if (searchView.query.isNotEmpty()) {
                        filter(searchView.query.toString())
                    }
                    if (newList.isNotEmpty()) {
                        viewModel._adList.postValue(newList)
                    } else {
                        viewModel._adList.postValue(it)
                    }
                    findNavController().navigate(R.id.homeFragment)
                    viewModel._filteredList.postValue(null)
                    //newList.clear()
                }
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment filterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            filterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}