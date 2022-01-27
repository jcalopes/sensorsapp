package pt.ipp.estg.sensorsapp.src.retrofit

import retrofit2.Call
import retrofit2.http.POST

interface GoogleMapsService {
    @POST("geolocate?key=")
    fun getLocation(): Call<Location>

}