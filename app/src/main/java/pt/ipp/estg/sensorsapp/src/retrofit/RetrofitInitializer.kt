package pt.ipp.estg.sensorsapp.src.retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {
    companion object{
        val baseUrlGoogleMaps = "https://www.googleapis.com/geolocation/v1/"
        val baseUrlGeoapify = "https://api.geoapify.com/v2/"
    }

    fun init(url:String):Retrofit{
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun geoapifyService():GeoapifyService = init(baseUrlGeoapify).create(GeoapifyService::class.java)
    fun googleMapsService():GoogleMapsService = init(baseUrlGoogleMaps).create(GoogleMapsService::class.java)
}