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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.main.ChoosePhoto.Companion.createBitmapFromResult
import com.example.pethub.retrofit.SignUpInfo
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [signUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class signUpFragment : Fragment() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.createBitmapFromResult(requireActivity())
            fileUri = data.data
            ivSignUser.setImageBitmap(imageBitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivSignUser.setOnClickListener { startActivityForResult(ChoosePhoto.loadPhotoFromDevice(), 111) }

        signUpBtn.setOnClickListener {
            val name = etName.text.toString().trim()
            val login = etLogin.text.toString().trim()
            val city = etCity.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            if (name.isNotEmpty() && login.isNotEmpty() && city.isNotEmpty() && phone.isNotEmpty()
                && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (password.equals(confirmPassword)) {
                        fileUri?.let { it1 ->
                            val originalFile = FileUtils.getFile(requireContext(), it1)
                            val requestBody = RequestBody.create(
                                requireContext().contentResolver.getType(it1)
                                    .toString().toMediaTypeOrNull(), originalFile
                            )
                            val photo =
                                MultipartBody.Part.createFormData("image", originalFile.name, requestBody)
                            viewModel.signUp(
                                name.toRequestBody(MultipartBody.FORM),
                                password
                                    .toRequestBody(MultipartBody.FORM),
                                phone
                                    .toRequestBody(MultipartBody.FORM),
                                login.toRequestBody(MultipartBody.FORM),
                                login
                                    .toRequestBody(MultipartBody.FORM),
                                city.toRequestBody(MultipartBody.FORM),
                                photo
                            )
                        } ?: viewModel.signUp(
                            name.toRequestBody(MultipartBody.FORM),
                            password
                                .toRequestBody(MultipartBody.FORM),
                            phone
                                .toRequestBody(MultipartBody.FORM),
                            login.toRequestBody(MultipartBody.FORM),
                            login
                                .toRequestBody(MultipartBody.FORM),
                            city.toRequestBody(MultipartBody.FORM),
                            null
                        )
                        findNavController().navigate(R.id.loginFragment)
                    } else {
                        Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment signUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            signUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}