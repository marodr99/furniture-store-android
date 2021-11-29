package pk.furniture_android_app.orders

import retrofit2.Call
import retrofit2.http.*

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

    @GET("/orders/{id}")
    fun getOrder(
        @Header("Authorization") accessToken: String,
        @Path("id") orderId: Int
    ): Call<SingleOrder>
}