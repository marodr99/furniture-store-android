package pk.furniture_android_app.furniture

import pk.furniture_android_app.models.chairs.ChairsSearchOptions
import pk.furniture_android_app.models.furniture.FurnitureResponse
import pk.furniture_android_app.models.wardrobes.WardrobesSearchOptions
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FurnitureApiService {
    @GET("/furniture/{furnitureType}/{page}")
    fun getAllFurniture(
        @Path("furnitureType") furnitureType: String,
        @Path("page") page: Int
    ): Call<FurnitureResponse>

    @POST("/furniture/chairs/specific/{page}")
    fun getSpecificChairs(
        @Path("page") page: Int,
        @Body chairsSearchOptions: ChairsSearchOptions
    ): Call<FurnitureResponse>

    @POST("/furniture/wardrobes/specific/{page}")
    fun getSpecificWardrobes(
        @Path("page") page: Int,
        @Body wardrobesSearchOptions: WardrobesSearchOptions
    ): Call<FurnitureResponse>
}