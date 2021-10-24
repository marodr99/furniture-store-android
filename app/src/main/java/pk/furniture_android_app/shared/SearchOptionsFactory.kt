package pk.furniture_android_app.shared

import pk.furniture_android_app.RetrofitClientInstance
import pk.furniture_android_app.furniture.FurnitureSearchOptions
import pk.furniture_android_app.furniture.chairs.ChairsApiService
import pk.furniture_android_app.models.furniture.FurnitureType

class SearchOptionsFactory(val furnitureType: FurnitureType) {

    fun getProperApiCall(): FurnitureSearchOptions {
        when (furnitureType) {
            FurnitureType.CHAIRS -> return RetrofitClientInstance.getRetrofitInstance()
                ?.create(ChairsApiService::class.java) as FurnitureSearchOptions
        }
        throw IllegalArgumentException("Not supported furniture type: $furnitureType")
    }
}