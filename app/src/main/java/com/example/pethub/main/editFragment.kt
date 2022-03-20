package com.example.pethub.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.example.pethub.main.ChoosePhoto.Companion.createBitmapFromResult
import com.example.pethub.retrofit.AdPost
import com.example.pethub.retrofit.Kind
import com.example.pethub.viewmodel.ViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.feed_item.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class editFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val viewModel by activityViewModels<ViewModel>()
    val kindList = mutableListOf<String>("Выберите вид")
    val fullKindList = mutableListOf<Kind>()
    var fileUri: Uri? = null
    var id: Int? = null

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
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageViewEditAd.setOnClickListener { startActivityForResult(ChoosePhoto.loadPhotoFromDevice(), 111) }

        viewModel.ad.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                id = it.id
                editTitleTv.setText(it.title)
                editPriceTv.setText(it.price)
                editTvLocation.setText(it.city)
                if (!it.url.isNullOrEmpty()) {
                    Picasso.get().load(it.url).into(imageViewEditAd)
                }
            }
            viewModel._ad.postValue(null)
        })
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        spinnerEditType.post {
            run {
                spinnerEditType.setSelection( sharedPrefs?.getString("edit_type", "")!!.toInt())
            }
        }

        val token = sharedPrefs?.getString("token", "")
        val items = arrayOf("Выберите тип","Продажа", "Покупка", "Утерян", "Найден")
        val spinnerEditTypeAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
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
        spinnerEditTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEditType.adapter = spinnerEditTypeAdapter
        var type : Int? = null
        spinnerEditType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
            if (!it.isNullOrEmpty()) {
                for (item in it) {
                    if (!kindList.contains(item.title)) {
                        item.title?.let { title -> kindList.add(title) }
                    }
                }
            }
        })
        val spinnerEditKindAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, kindList) {
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
        viewModel.kindList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                fullKindList.addAll(it)
                for (item in fullKindList) {
                    if (item.id == sharedPrefs?.getString("edit_kind", "")!!.toInt()) {
                        spinnerEditKind.post {
                            spinnerEditKind.setSelection(spinnerEditKindAdapter.getPosition(item.title))
                        }
                    }
                }
            }
        })
        spinnerEditKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEditKind.adapter = spinnerEditKindAdapter
        var kind : Int? = null
        spinnerEditKind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
            viewModel._userAdsList.postValue(null)
            bthEdit.setOnClickListener {
                val title = editTitleTv.text.toString().trim()
                val location = editTvLocation.text.toString().trim()
                val price = editPriceTv.text.toString().trim()
                fileUri?.let {
                    val originalFile = com.example.pethub.main.FileUtils.getFile(requireContext(), fileUri)
                    val requestBody = originalFile.asRequestBody(
                        requireContext().contentResolver.getType(it)
                            .toString().toMediaTypeOrNull()
                    )
                    val photo = MultipartBody.Part.createFormData("image", originalFile.name, requestBody)
                    id?.let { it1 ->
                        viewModel.updateUsersAd("Bearer " + token,
                            it1,
                            title.toRequestBody(MultipartBody.FORM),
                            type.toString().toRequestBody(MultipartBody.FORM),
                            kind.toString().toRequestBody(MultipartBody.FORM),
                            location.toRequestBody(MultipartBody.FORM), photo,
                            "Пушкина 33".toRequestBody(MultipartBody.FORM),
                            "Пушкина 33".toRequestBody(MultipartBody.FORM),
                            price.toRequestBody(MultipartBody.FORM),
                            "".toRequestBody(MultipartBody.FORM)
                        )
                    }
                } ?: id?.let { it1 ->
                    viewModel.updateUsersAd("Bearer " + token,
                        it1,
                        title.toRequestBody(MultipartBody.FORM),
                        type.toString().toRequestBody(MultipartBody.FORM),
                        kind.toString().toRequestBody(MultipartBody.FORM),
                        location.toRequestBody(MultipartBody.FORM), null,
                        "Пушкина 33".toRequestBody(MultipartBody.FORM),
                        "Пушкина 33".toRequestBody(MultipartBody.FORM),
                        price.toRequestBody(MultipartBody.FORM),
                        "".toRequestBody(MultipartBody.FORM)
                    )
                }
                viewModel._userAdsList.postValue(null)
                findNavController().navigate(R.id.userAdsFragment)
            }
        } else {
            findNavController().navigate(R.id.loginFragment)
            Toast.makeText(requireContext(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.createBitmapFromResult(requireActivity())
            fileUri = data.data
            imageViewEditAd.setImageBitmap(imageBitmap)
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        sharedPrefs?.edit()?.putString("url","")?.apply()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}