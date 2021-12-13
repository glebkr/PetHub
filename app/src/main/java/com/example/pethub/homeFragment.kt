package com.example.pethub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethub.feedAdapter.FeedAdapter
import com.example.pethub.retrofit.Ad
import com.example.pethub.viewmodel.ViewModel
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress?.visibility = ProgressBar.VISIBLE
        val adapter = FeedAdapter(mutableListOf())
        viewModel.getAds()
        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        rvFeed.adapter = adapter
        viewModel.adList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.updateList(it)
                progress?.visibility = ProgressBar.INVISIBLE
            }
        })

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

        val sharedPrefs =
            activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")
        if (token!!.isNotEmpty()) {
            viewModel.getFavAds("Bearer " + token)
        }
        adapter.setOnItemClickListener(object : FeedAdapter.OnClickListener {
            override fun onImageViewClick(position: Int) {
                    if (token.isNotEmpty()) {
                        viewModel.adList.observe(viewLifecycleOwner, Observer { adList ->
                            val adId = adList[position].id
                            viewModel.favAdList.observe(viewLifecycleOwner, Observer { favAdList ->
                                val idList = mutableListOf<Int>()
                                for (i in 0 until favAdList.size) {
                                    idList.add(favAdList[i].id!!)
                                }
                                if (idList.contains(adId)) {
                                    viewModel.delFavAd("Bearer " + token, adId!!)
                                } else {
                                    viewModel.postFavAd("Bearer " + token, adId!!)
                                }
                            })
                        })
                    } else {
                        findNavController().navigate(R.id.loginFragment)
                        Toast.makeText(requireContext(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onItemClick(position: Int) {
                viewModel.adList.observe(viewLifecycleOwner, Observer {
                    sharedPrefs.edit().apply {
                        putString("title", it[position].title)
                        putString("price", it[position].price)
                        putString("city", it[position].x_coord)
                    }.apply()
                        // ragment.newInstance(it[position].title, it[position].price, it[position].x_coord)
                })
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