package com.example.pethub.main

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pethub.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val minsk = LatLng(53.9, 27.5590)
        val m = googleMap.addMarker(MarkerOptions().position(minsk).title("Marker in Minsk"))
        googleMap.addMarker(MarkerOptions().position(LatLng(53.87, 27.5)).title("Marker"))
        googleMap.addMarker(MarkerOptions().position(LatLng(53.93, 27.6)).title("Marker"))
        googleMap.addMarker(MarkerOptions().position(LatLng(53.885, 27.650)).title("Marker"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minsk, 11.0F))
        /*
        googleMap.setOnMarkerClickListener ( object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(p0: Marker): Boolean {
                Toast.makeText(context, m?.title, Toast.LENGTH_SHORT).show()
                return true
            }
        })
         */

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}