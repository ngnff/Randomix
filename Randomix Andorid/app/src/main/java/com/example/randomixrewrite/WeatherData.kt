package com.example.randomixrewrite

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import android.content.Context


data class WeatherData(
    val coord: Coord,
    val main: Main,
    val clouds: Clouds
) {
    data class Coord(
        val lon: Double,
        val lat: Double
    )

    data class Main(
        val temp: Double,
        val feelsLike: Double,
        val tempMin: Double?,
        val tempMax: Double,
        val pressure: Int,
        val humidity: Int,
        val seaLevel: Int?
    )

    data class Clouds(
        val all: Int
    )
}

fun parseWeatherData(jsonData: String): WeatherData {
    val jsonObject = JSONObject(jsonData)

    val coordObject = jsonObject.getJSONObject("coord")
    val coord = WeatherData.Coord(
        lon = coordObject.getDouble("lon"),
        lat = coordObject.getDouble("lat")
    )

    val mainObject = jsonObject.getJSONObject("main")
    val main = WeatherData.Main(
        temp = mainObject.getDouble("temp"),
        feelsLike = mainObject.optDouble("feels_like", 0.0),
        tempMin = mainObject.optDouble("temp_min", 0.0), // Используйте null в качестве значения по умолчанию
        tempMax = mainObject.getDouble("temp_max"),
        pressure = mainObject.getInt("pressure"),
        humidity = mainObject.getInt("humidity"),
        seaLevel = mainObject.optInt("sea_level", 0)
    )

    val cloudsObject = jsonObject.getJSONObject("clouds")
    val clouds = WeatherData.Clouds(
        all = cloudsObject.getInt("all")
    )

    return WeatherData(coord, main, clouds)
}

class WeatherManager(private val context: Context) {

    fun getJsonDataFromAssets(fileName: String): String {
        val json: String
        try {
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            bufferedReader.close()
            json = stringBuilder.toString()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }

        return json
    }



    fun getJsonDataFromUrl(url: String): String {
        val connection = URL(url).openConnection()
        val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
        val jsonData = StringBuilder()

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            jsonData.append(line)
        }
        reader.close()

        return jsonData.toString()
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherData {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=6c33fe627076da12bfe3a1f9760e2bfa"
                val jsonData = getJsonDataFromUrl(url)


                return@withContext parseWeatherData(jsonData)
            } catch (e: Exception) {
                Log.d("Weather", "Error fetching weather data")
                val customJsonData = getJsonDataFromAssets("custom_data")
                Log.d("Weather",customJsonData)
                parseWeatherData(customJsonData)
            }
        }
    }

}