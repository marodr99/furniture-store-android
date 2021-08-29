package pk.furniture_android_app.chairs

import pk.furniture_android_app.models.chairs.ChairResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChairsApiService {
    @GET("/furniture/chairs/{page}")
    fun getAllChairs(@Path("page") page: Int): Call<ChairResponse>
}