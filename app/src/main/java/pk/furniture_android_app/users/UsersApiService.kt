package pk.furniture_android_app.users

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersApiService {
    @POST("/users/register")
    fun createUser(@Body user: User): Call<String>
}