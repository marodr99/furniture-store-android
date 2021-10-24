package pk.furniture_android_app.furniture

import retrofit2.Call

interface FurnitureSearchOptions {
    fun getSearchOptions(): Call<Map<String, List<String>>>
}