package com.mastercoding.mystoryappsubmissionawal.story

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mastercoding.mystoryappsubmissionawal.R
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefManager = PrefManager(this)

        setContentView(R.layout.activity_maps)
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fetchStoriesWithLocation()
    }

    private fun fetchStoriesWithLocation() {
        val token = "Bearer ${prefManager.getToken()}"
        val apiService = ApiService.create()

        lifecycleScope.launch {
            try {
                val response = apiService.getStoriesWithLocation(token)
                val storiesWithLocation = response.listStory.filter { it.lat != null && it.lon != null }

                if (storiesWithLocation.isNotEmpty()) {

                    storiesWithLocation.forEach { story ->
                        val latLng = LatLng(story.lat!!.toDouble(), story.lon!!.toDouble())
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(story.description)
                        )
                    }

                    val firstStoryLatLng = LatLng(
                        storiesWithLocation.first().lat!!.toDouble(),
                        storiesWithLocation.first().lon!!.toDouble()
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstStoryLatLng, 10f))
                } else {
                    Log.e("MapsActivity", "No stories with locations found.")
                }
            } catch (e: Exception) {
                Log.e("MapsActivity", "Failed to fetch stories with location: ${e.message}")
            }
        }
    }
}
