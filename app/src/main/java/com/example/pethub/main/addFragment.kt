package com.example.pethub.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.os.Parcelable
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.main.ChoosePhoto.Companion.createBitmapFromResult
import com.example.pethub.retrofit.Kind
import com.example.pethub.viewmodel.ViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.user_feed_item.view.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

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
    var type : Int? = null
    var kind : Int? = null
    var fileUri: Uri? = null

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

    private fun getSpinnerData() {
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val items = arrayOf("Выберите тип","Продажа", "Покупка", "Утерян", "Найден")
        val spinnerAddTypeAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
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
        spinnerAddTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddType.adapter = spinnerAddTypeAdapter

        spinnerAddType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                when (position) {
                    1 -> type = 1
                    2 -> type = 2
                    3 -> type = 3
                    4 -> {
                        type = 4
                        if (sharedPrefs?.getString("x_coord", "").isNullOrEmpty() && sharedPrefs?.getString("y_coord", "").isNullOrEmpty()) {
                            sharedPrefs?.edit()?.putBoolean("add", true)?.apply()
                            findNavController().navigate(R.id.mapsFragment)
                        }
                    }
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
                fullKindList.addAll(it)
            }
        })
        val spinnerAddKindAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, kindList) {
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
        spinnerAddKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddKind.adapter = spinnerAddKindAdapter
        spinnerAddKind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    }

    private fun addButtonPress() {
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPrefs?.getString("token", "")

        if (token!!.isNotEmpty()) {
            val title = titleTv.text.toString().trim()
            val location = tvLocation.text.toString().trim()
            val price = priceTv.text.toString().trim()
            if (title.isNotEmpty() && location.isNotEmpty() && price.isNotEmpty() && type != null && kind != null) {
                /*
                val bitmapByteArray = ByteArrayOutputStream()
                bitmapImg?.compress(Bitmap.CompressFormat.JPEG, 80, bitmapByteArray)
                //val adData = AdPost(title, type, kind, price,"Пушкина 33", "Пушкина 33", location, bitmapByteArray)

                var map = HashMap<String, MultipartBody.Part>()


                map["title"] = MultipartBody .Part.createFormData("gwerg") RequestBody.create(MultipartBody.FORM, title)
                map["type_id"] = RequestBody.create(MultipartBody.FORM, type.toString())
                map["animal_kind"] = RequestBody.create(MultipartBody.FORM, kind.toString())
                map["city"] = RequestBody.create(MultipartBody.FORM, location)
                map["x_coord"] = RequestBody.create(MultipartBody.FORM, "Пушкина 33")
                map["y_coord"] = RequestBody.create(MultipartBody.FORM, "Пушкина 33")
                map["price"] = RequestBody.create(MultipartBody.FORM, price)
                map["description"] = RequestBody.create(MultipartBody.FORM, "")
                 */
                //map["image"] = requestBody

                fileUri?.let {
                val originalFile = com.example.pethub.main.FileUtils.getFile(requireContext(), fileUri)
                val requestBody = originalFile.asRequestBody(
                    requireContext().contentResolver.getType(it)
                        .toString().toMediaTypeOrNull()
                )
                val photo = MultipartBody.Part.createFormData("image", originalFile.name, requestBody)
                viewModel.postAd("Bearer " + token,
                    title.toRequestBody(MultipartBody.FORM),
                    type.toString().toRequestBody(MultipartBody.FORM),
                    kind.toString().toRequestBody(MultipartBody.FORM),
                    location.toRequestBody(MultipartBody.FORM), photo,
                    (if (type == 4) sharedPrefs.getString("x_coord", "") else "")!!.toRequestBody(MultipartBody.FORM),
                    (if (type == 4) sharedPrefs.getString("y_coord", "") else "")!!.toRequestBody(MultipartBody.FORM),
                    price.toRequestBody(MultipartBody.FORM),
                    "".toRequestBody(MultipartBody.FORM)
                )
                    } ?: viewModel.postAd("Bearer " + token,
                    title.toRequestBody(MultipartBody.FORM),
                    type.toString().toRequestBody(MultipartBody.FORM),
                    kind.toString().toRequestBody(MultipartBody.FORM),
                    location.toRequestBody(MultipartBody.FORM), null,
                    (if (type == 4) sharedPrefs.getString("x_coord", "") else "")!!.toRequestBody(MultipartBody.FORM),
                    (if (type == 4) sharedPrefs.getString("y_coord", "") else "")!!.toRequestBody(MultipartBody.FORM),
                    price.toRequestBody(MultipartBody.FORM),
                    "".toRequestBody(MultipartBody.FORM)
                )
                /*
                viewModel.postAd("Bearer " + token, MultipartBody.Part.createFormData("title", title), MultipartBody.Part.createFormData("type_id",
                    type.toString()), MultipartBody.Part.createFormData("animal_kind", kind.toString()), MultipartBody.Part.createFormData("city", location), photo,
                    MultipartBody.Part.createFormData("x_coord","Пушкина 33"), MultipartBody.Part.createFormData("y_coord","Пушкина 33"), MultipartBody.Part.createFormData("price",price),  MultipartBody.Part.createFormData("description",""))
                 */
                viewModel._adList.postValue(null)
                titleTv.text = null
                tvLocation.text = null
                priceTv.text = null
                ivAdPhoto.setImageResource(R.drawable.empty_photo)
                spinnerAddType.post {
                    run {
                        spinnerAddType.setSelection(0)
                    }
                }
                spinnerAddKind.post {
                    run {
                        spinnerAddKind.setSelection(0)
                    }
                }
                sharedPrefs.edit().apply {
                    putString("x_coord", "")
                    putString("y_coord", "")
                    putBoolean("add", false)
                }.apply()
            } else if (type == null && kind == null) {
                Toast.makeText(
                    requireContext(),
                    "Выберите тип объявления и вид питомца",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (type == null && kind != null) {
                Toast.makeText(requireContext(), "Выберите тип объявлеия", Toast.LENGTH_SHORT)
                    .show()
            } else if (type != null && kind == null) {
                Toast.makeText(requireContext(), "Выберите вид питомца", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Заполните остальные поля", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            findNavController().navigate(R.id.loginFragment)
            Toast.makeText(requireContext(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = sharedPrefs?.getString("x_coord", "")?.let {
                sharedPrefs.getString("y_coord", "")?.let { it1 ->
                    data.createBitmapFromResult(requireActivity(),
                        it, it1
                    )
                }
            }
            fileUri = data.data
            ivAdPhoto.setImageBitmap(imageBitmap)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSpinnerData()
        bthAdd.setOnClickListener { addButtonPress() }
        ivAdPhoto.setOnClickListener { startActivityForResult(ChoosePhoto.loadPhotoFromDevice(), 111) }
    }

    override fun onStop() {
        super.onStop()

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