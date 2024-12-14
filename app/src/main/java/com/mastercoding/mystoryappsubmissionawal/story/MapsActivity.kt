package com.mastercoding.mystoryappsubmissionawal.story

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mastercoding.mystoryappsubmissionawal.R
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.model.StoryResponse
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        ApiService.create().getAllStories(token, null, null, 1).enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {

                    response.body()?.listStory?.forEach { story ->

                        story.lat?.let { lat ->
                            story.lon?.let { lon ->
                                val latLng = LatLng(lat.toDouble(), lon.toDouble())
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet(story.description)
                                )
                            }
                        }
                    }

                    if (response.body()?.listStory?.isNotEmpty() == true) {
                        val firstStory = response.body()?.listStory?.get(0)
                        firstStory?.let {
                            val firstLatLng = LatLng(it.lat!!.toDouble(), it.lon!!.toDouble())
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10f))
                        }
                    }
                } else {
                    Log.e("MapsActivity", "Failed to fetch stories: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e("MapsActivity", "API call failed: ${t.message}")
            }
        })
    }
}
