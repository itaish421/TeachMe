package com.example.teachme

import com.example.teachme.models.Weather
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class WeatherService {

    companion object {
        const val REQUEST_URL = "https://www.meteosource.com/api/v1/free/point?lat=32.0853&lon=34.7818&sections=current%2Chourly&language=en&units=auto&key=gk11otn3dntgwyxoo3dcq1cvc98docu1eolrmtzv"
    }

    suspend fun openRequest(): Weather = withContext(Dispatchers.IO) {
        val weather = CompletableDeferred<Weather>()
        try {
            val request = URL(REQUEST_URL).readText()
            weather.complete(Weather.fromJson(request))
        }
        catch (e: Exception) {
            weather.completeExceptionally(e)
        }
        weather.await()
    }
}