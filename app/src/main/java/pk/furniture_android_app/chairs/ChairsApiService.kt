package pk.furniture_android_app.chairs

import pk.furniture_android_app.models.Chair
import retrofit2.Call
import retrofit2.http.GET

interface ChairsApiService {
    @GET("/furniture/chairs")
    fun getAllChairs(): Call<List<Chair>>
}