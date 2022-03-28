package com.example.pethub.main

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethub.R
import com.example.pethub.retrofit.AdEdit
import com.example.pethub.retrofit.AdPost
import com.example.pethub.userAdsAdapter.UserAdsAdapter
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.fragment_user_ads.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [userAdsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class userAdsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_user_ads, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel._userAdsList.postValue(null)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel._userAdsList.postValue(null)
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")
        val adapter = UserAdsAdapter(mutableListOf())
        rvUserAds.layoutManager = LinearLayoutManager(requireContext())
        rvUserAds.adapter = adapter
        viewModel.getUsersAds("Bearer " + token)
        viewModel.userAdsList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.updateList(it)
                viewModel._userAdsList.postValue(null)
            } else {
                //viewModel.getUsersAds("Bearer " + token)
            }
        })
        adapter.setOnItemClickListener(object : UserAdsAdapter.OnClickListener {
            override fun onEditImageViewClick(position: Int) {
                if (adapter.list.size != 0) {
                    val item = adapter.list[position]
                    val ad = AdEdit(
                        item.id,
                        item.title,
                        item.type_id,
                        item.animal_kind,
                        item.price,
                        item.x_coord,
                        item.y_coord,
                        item.city,
                        if (!item.url.isNullOrEmpty()) item.url else ""
                    )
                    viewModel._ad.postValue(ad)
                    sharedPrefs?.edit()?.apply {
                        putString("edit_type", item.type_id.toString())
                        putString("edit_kind", item.animal_kind.toString())
                    }?.apply()
                    findNavController().navigate(R.id.editFragment)
                }
            }

            override fun onDeleteImageViewClick(position: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Подтверждение удаления")
                builder.setMessage("Вы уверены, что хотите удалить данное объявление?")
                builder.setPositiveButton("Да") { dialog, which ->
                    viewModel.deleteUsersAd("Bearer " + token, adapter.list[position].id!!)
                    adapter.list.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    viewModel._adList.postValue(null)
                }
                builder.setNegativeButton("Нет") { dialog, which ->
                    dialog.cancel()
                }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()
            }


            override fun onItemClick(position: Int) {
                sharedPrefs!!.edit().apply {
                    putString("title", adapter.list[position].title)
                    putString("price", adapter.list[position].price)
                    putString("city", adapter.list[position].city)
                    putString("userName", adapter.list[position].userName)
                    putString("phone", adapter.list[position].phone)
                    if (!adapter.list[position].url.isNullOrEmpty()) {
                        putString("url", adapter.list[position].url)
                    }
                }.apply()
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
         * @return A new instance of fragment userAdsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            userAdsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}