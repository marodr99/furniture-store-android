package pk.furniture_android_app.furniture

import pk.furniture_android_app.models.furniture.FurnitureResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FurnitureApiService {
    @GET("/furniture/{furnitureType}/{page}")
    fun getAllFurniture(
        @Path("furnitureType") furnitureType: String,
        @Path("page") page: Int
    ): Call<FurnitureResponse>
}