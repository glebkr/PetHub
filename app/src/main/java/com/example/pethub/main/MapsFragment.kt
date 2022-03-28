package com.example.pethub.main

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pethub.R
import com.example.pethub.viewmodel.ViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_maps.*

class MapsFragment : Fragment() {
    val viewModel by activityViewModels<ViewModel>()

    private val mapCallback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        viewModel.getAds()
        viewModel.adList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                for (item in it) {
                    if (item.type_id == 4) {
                        if (!item.x_coord.isNullOrEmpty() && !item.y_coord.isNullOrEmpty()) {
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(
                                        LatLng(
                                            item.x_coord!!.toDouble(),
                                            item.y_coord!!.toDouble()
                                        )
                                    )
                                    .title(item.title)
                            )
                                ?.apply {
                                    //this.showInfoWindow()
                                    this.tag = item.id
                                }

                        }
                    }
                }
            }
        })

        val minsk = LatLng(53.9, 27.5590)
        /*
        val m = googleMap.addMarker(MarkerOptions().position(minsk).title("Marker in Minsk"))
        googleMap.addMarker(MarkerOptions().position(LatLng(53.87, 27.5)).title("Marker"))
        googleMap.addMarker(MarkerOptions().position(LatLng(53.93, 27.6)).title("Marker"))
        googleMap.addMarker(MarkerOptions().position(LatLng(53.885, 27.650)).title("Marker"))
        */
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minsk, 11.0F))

        googleMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                viewModel.adList.observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        for (item in it) {
                            if (item.id == marker.tag) {
                                sharedPrefs?.edit()?.apply {
                                    putString("title", item.title)
                                    putString("price", item.price)
                                    putString("city", item.city)
                                    putString("userName", item.userName)
                                    putString("phone", item.phone)
                                    if (!item.url.isNullOrEmpty()) {
                                        putString("url", item.url)
                                    }
                                }?.apply()
                                findNavController().navigate(R.id.descriptionFragment)
                            }
                        }
                    }
                })
                return true
            }
        })

        /*
        googleMap.setOnMarkerClickListener ( object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(p0: Marker): Boolean {
                Toast.makeText(context, m?.title, Toast.LENGTH_SHORT).show()
                return true
            }
        })
         */
    }

    private val addMarkerCallback = OnMapReadyCallback { googleMap ->
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val minsk = LatLng(53.9, 27.5590)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minsk, 11.0F))
        googleMap.addMarker(MarkerOptions().position(minsk).title("Переместите маркер на место, где находится найденный питомец").draggable(true))
        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
            }

            override fun onMarkerDragEnd(marker: Marker) {
                confirmButton.visibility = View.VISIBLE
                confirmButton.setOnClickListener {
                    sharedPrefs?.edit()?.apply {
                        putString("x_coord", marker.position.latitude.toString())
                        putString("y_coord", marker.position.longitude.toString())
                        putBoolean("add", false)
                    }?.apply()
                    findNavController().popBackStack()
                }
            }

            override fun onMarkerDragStart(marker: Marker) {
                confirmButton.visibility = View.INVISIBLE
            }

        })
    }

    override fun onStop() {
        super.onStop()
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        viewModel._adList.postValue(null)
        sharedPrefs?.edit()?.apply {
            putBoolean("add", false)?.apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPrefs = activity?.getSharedPreferences("SharedPrefs", AppCompatActivity.MODE_PRIVATE)
        sharedPrefs?.edit()?.apply {
            putString("x_coord", "")
            putString("y_coord", "")
        }?.apply()
        confirmButton.visibility = View.INVISIBLE
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (sharedPrefs?.getBoolean("add", false) == false) {
            mapFragment?.getMapAsync(mapCallback)
        } else {
            mapFragment?.getMapAsync(addMarkerCallback)
        }
    }
}