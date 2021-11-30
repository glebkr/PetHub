package com.example.pethub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethub.favoriteAdapter.FavoriteAdapter
import com.example.pethub.feedAdapter.FeedAdapter
import com.example.pethub.retrofit.Feed
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.feed_item.*
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
        val adapter = FeedAdapter(mutableListOf())
        viewModel.getAds()
        val sharedPrefs =
            activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")
        feed_rv.layoutManager = LinearLayoutManager(requireContext())
        feed_rv.adapter = adapter
        viewModel.feedList.observe(viewLifecycleOwner, Observer {
            adapter.updateList(it)
        })
        adapter.setOnImageViewClickListener(object : FeedAdapter.OnClickListener {
            override fun onImageViewClick(position: Int) {
                viewModel.feedList.observe(viewLifecycleOwner, Observer {
                    val id = it[position].id
                    viewModel.postFavAd(token!!, id!!)
                })
                IVfavorite.setImageResource(R.drawable.ic_favorite_pressed)
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