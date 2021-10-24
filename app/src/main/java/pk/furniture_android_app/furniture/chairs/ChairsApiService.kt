package pk.furniture_android_app.furniture.chairs

import pk.furniture_android_app.furniture.FurnitureSearchOptions
import pk.furniture_android_app.models.chairs.Chair
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChairsApiService : FurnitureSearchOptions {
    @GET("/chairs/{id}")
    fun getChair(@Path("id") id: Int): Call<Chair>

    @GET("/chairs/search/options")
    override fun getSearchOptions(): Call<Map<String, List<String>>>
}