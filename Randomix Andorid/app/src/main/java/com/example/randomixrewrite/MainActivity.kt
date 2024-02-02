package com.example.randomixrewrite

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await



import okhttp3.Dispatcher

class MainActivity : AppCompatActivity() {
    val apiKey: String = "6c33fe627076da12bfe3a1f9760e2bfa"

    private lateinit var shareLocationButton: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private lateinit var temperatureTextView: TextView
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shareLocationButton = findViewById(R.id.share_location_button)
        val simplifiedButton: Button = findViewById(R.id.Simplified)

        checkLocation()




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        simplifiedButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val weatherManager = WeatherManager(this@MainActivity)


                    val weatherData = weatherManager.getCurrentWeather(currentLatitude,currentLongitude)
                    Log.d("Weather", "Temperature: ${weatherData.main.temp}")
                    val intent = Intent(this@MainActivity, ActivityNew::class.java)
                    intent.putExtra("latitude",currentLatitude)
                    intent.putExtra("longitude",currentLongitude)
                    startActivity(intent)

                } catch (e: Exception) {
                    Log.d("Weather", "Error processing simplified JSON")
                    e.printStackTrace()
                }
            }
        }

        shareLocationButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    requestLocation()
                    val weatherManager = WeatherManager(this@MainActivity)

                    val location = getLastLocation()
                    if (location != null){
                        val latitude = location.latitude
                        val longitude = location.longitude

                        currentLatitude = latitude
                        currentLongitude = longitude

                        val weatherData = weatherManager.getCurrentWeather(latitude, longitude)
                        Log.d("Weather", "Temperature: ${weatherData.main.temp}")

                        val intent = Intent(this@MainActivity, ActivityNew::class.java)
                        intent.putExtra("latitude",latitude)
                        intent.putExtra("longitude",longitude)
                        startActivity(intent)

                    }
                } catch (e: Exception) {
                    Log.d("Weather", "Error")
                    e.printStackTrace()
                }
            }
        }
    }


    private suspend fun requestLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                while (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    delay(100)
                }
                getLastLocation()
            }
        } else {
            getLastLocation()
        }
    }


    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? = withContext(Dispatchers.IO) {
        return@withContext try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e("Weather", "Error getting location", e)
            null
        }
    }

    private fun checkLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            showAlert()
        }
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    lifecycleScope.launch {
                        getLastLocation()
                    }
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}