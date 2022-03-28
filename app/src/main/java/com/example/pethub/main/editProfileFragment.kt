package com.example.pethub.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.feedAdapter.FeedAdapter
import com.example.pethub.main.ChoosePhoto.Companion.createBitmapFromResult
import com.example.pethub.retrofit.SignUpInfo
import com.example.pethub.retrofit.UserUpdateInfo
import com.example.pethub.viewmodel.ViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class editProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var fileUri: Uri? = null
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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.createBitmapFromResult(requireActivity(), "", "")
            fileUri = data.data
            ivUserEdit.setImageBitmap(imageBitmap)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                etUpdateName.setText(it.name)
                etUpdateLogin.setText(it.login)
                etUpdateCity.setText(it.city)
                etUpdatePhone.setText(it.phone)
                if (!it.url.isNullOrEmpty()) {
                    Picasso.get().load(it.url).into(ivUserEdit)
                }
            }
            viewModel._userInfo.postValue(null)
        })
        ivUserEdit.setOnClickListener { startActivityForResult(ChoosePhoto.loadPhotoFromDevice(), 111) }

        editProfileBtn.setOnClickListener {
            val name = etUpdateName.text.toString().trim()
            val login = etUpdateLogin.text.toString().trim()
            val city = etUpdateCity.text.toString().trim()
            val phone = etUpdatePhone.text.toString().trim()
            fileUri?.let { it1 ->
                val originalFile = FileUtils.getFile(requireContext(), it1)
                val requestBody = originalFile
                    .asRequestBody(
                        requireContext().contentResolver.getType(it1)
                            .toString().toMediaTypeOrNull()
                    )
                val photo =
                    MultipartBody.Part.createFormData("avatar", originalFile.name, requestBody)
                viewModel.updateUser(
                    "Bearer " + token,
                    name.toRequestBody(MultipartBody.FORM),
                    phone
                        .toRequestBody(MultipartBody.FORM),
                    login.toRequestBody(MultipartBody.FORM),
                    login
                        .toRequestBody(MultipartBody.FORM),
                    city.toRequestBody(MultipartBody.FORM),
                    photo
                )
            } ?: viewModel.updateUser(
                "Bearer " + token,
                name.toRequestBody(MultipartBody.FORM),
                phone
                    .toRequestBody(MultipartBody.FORM),
                login.toRequestBody(MultipartBody.FORM),
                login
                    .toRequestBody(MultipartBody.FORM),
                city.toRequestBody(MultipartBody.FORM),
                null
            )
            viewModel._userInfo.postValue(null)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}