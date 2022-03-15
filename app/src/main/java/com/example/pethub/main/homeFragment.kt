package com.example.pethub.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethub.R
import com.example.pethub.feedAdapter.FeedAdapter
import com.example.pethub.retrofit.Ad
import com.example.pethub.viewmodel.ViewModel
import com.kotlinpermissions.ifNotNullOrElse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel by activityViewModels<ViewModel>()
    private val secondAdList = mutableListOf<Ad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        menu.findItem(R.id.clear_filter_home).isVisible = !sharedPrefs?.getString("query", "").isNullOrEmpty() || !sharedPrefs?.getString("type", "").isNullOrEmpty() ||
                !sharedPrefs?.getString("kind", "").isNullOrEmpty() || !sharedPrefs?.getString("cityFilter", "").isNullOrEmpty()
        menu.findItem(R.id.filter).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissionWrite = ActivityCompat.checkSelfPermission(requireContext(),  Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                10);
        }
        val permissionRead = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                10);
        }
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val adapter = FeedAdapter(mutableListOf(), mutableListOf())
        val token = sharedPrefs?.getString("token", "")
        if (token!!.isNotEmpty()) {
            viewModel.getFavAds("Bearer " + token)
            viewModel.favAdList.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    adapter.updateFavList(it)
                }
                viewModel._favAdList.postValue(null)
            })
        }
        if (viewModel._adList.value == null) {
            if (sharedPrefs.getString("query", "").isNullOrEmpty() && sharedPrefs.getString("type", "").isNullOrEmpty() &&
                sharedPrefs.getString("kind", "").isNullOrEmpty() && sharedPrefs.getString("cityFilter", "").isNullOrEmpty()) {
                progress?.visibility = ProgressBar.VISIBLE
                viewModel.getAds()
            }
        }
        viewModel.adList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.updateList(it)
                progress?.visibility = ProgressBar.INVISIBLE
            }
        })
        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        rvFeed.adapter = adapter

        /*
        fun filter(text: String?) {
            viewModel.adList.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    adapter.updateList(it)
                }
            })
            val filteredList = mutableListOf<Ad>()
            for (item in adapter.list) {
                if (text?.let { item.title!!.lowercase().contains(it.lowercase()) } == true ) {
                    filteredList.add(item)
                }
            }
            if (filteredList.isNotEmpty()) {
                adapter.updateList(filteredList)
            }
        }

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }

        })
         */

        adapter.setOnItemClickListener(object : FeedAdapter.OnClickListener {
            override fun onImageViewClick(position: Int) {
                    if (token.isNotEmpty()) {
                        val adId = adapter.list[position].id
                        val idList = mutableListOf<Int>()
                        for (i in 0 until adapter.favList.size) {
                            idList.add(adapter.favList[i].id!!)
                        }
                        if (adId != null) {
                            if (idList.contains(adId)) {
                                viewModel.delFavAd("Bearer " + token, adId)
                                adapter.favList.remove(adapter.list[position])
                            } else {
                                viewModel.postFavAd("Bearer " + token, adId)
                                adapter.favList.add(adapter.list[position])
                            }
                        }
                    } else {
                        findNavController().navigate(R.id.loginFragment)
                        Toast.makeText(requireContext(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onItemClick(position: Int) {
                sharedPrefs.edit().apply {
                    putString("title", adapter.list[position].title)
                    putString("price", adapter.list[position].price)
                    putString("city", adapter.list[position].city)
                    if (!adapter.list[position].url.isNullOrEmpty()) {
                        putString("url", adapter.list[position].url)
                    }

                }.apply()
                // argument.newInstance(it[position].title, it[position].price, it[position].x_coord)
               findNavController().navigate(R.id.descriptionFragment)
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
         * @return A new instance of fragment homeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}