package pk.furniture_android_app.furniture.wardrobes

import pk.furniture_android_app.furniture.FurnitureSearchOptions
import pk.furniture_android_app.models.wardrobes.Wardrobe
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WardrobesApiService : FurnitureSearchOptions {
    @GET("/wardrobes/{id}")
    fun getWardrobe(@Path("id") id: Int): Call<Wardrobe>

    @GET("/wardrobes/search/options")
    override fun getSearchOptions(): Call<Map<String, List<String>>>
}