package com.example.pethub.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethub.R
import com.example.pethub.favoriteAdapter.FavoriteAdapter
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.fragment_favorite.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [favoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class favoriteFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress?.visibility = ProgressBar.VISIBLE
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")
        val adapter = FavoriteAdapter(mutableListOf())
        rvFavorite.adapter = adapter
        rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        if (token!!.isNotEmpty()) {
            viewModel.getFavAds("Bearer " + token)
            viewModel.favAdList.observe(viewLifecycleOwner, Observer {
                adapter.updateList(it)
                progress?.visibility = ProgressBar.INVISIBLE
            })
        } else {
            findNavController().navigate(R.id.loginFragment)
            Toast.makeText(requireContext(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
        }
        adapter.setOnImageViewClickListener(object : FavoriteAdapter.OnClickListener {
            override fun onImageViewClick(position: Int) {
                viewModel.getFavAds("Bearer " + token)
                viewModel.favAdList.observe(viewLifecycleOwner, Observer {
                    viewModel.delFavAd("Bearer " + token, it[position].id!!)
                })
                adapter.list.removeAt(position)
                adapter.notifyItemRemoved(position)
                findNavController().navigate(R.id.favoriteFragment)
            }

            override fun onItemClick(position: Int) {
                viewModel.favAdList.observe(viewLifecycleOwner, Observer {
                    sharedPrefs.edit().apply {
                        putString("title", it[position].title)
                        putString("price", it[position].price)
                        putString("city", it[position].x_coord)
                    }.apply()
                    //descriptionFragment.newInstance(it[position].title, it[position].price, it[position].x_coord)
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
         * @return A new instance of fragment favoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            favoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}