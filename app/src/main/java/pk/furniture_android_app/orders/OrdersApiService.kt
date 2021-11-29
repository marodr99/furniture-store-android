package pk.furniture_android_app.orders

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OrdersApiService {
    @POST("/orders")
    fun createOrder(
        @Header("Authorization") accessToken: String,
        @Body orderRequest: OrderRequest
    ): Call<Void>

    @GET("/orders")
    fun getOrders(
        @Header("Authorization") accessToken: String
    ): Call<List<Order>>
}