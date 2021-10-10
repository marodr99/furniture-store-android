package pk.furniture_android_app.models.furniture

import com.google.gson.annotations.SerializedName

class Furniture {
    @SerializedName("id")
    val id = 0

    @SerializedName("title")
    val title: String? = null

    @SerializedName("price")
    val price = 0.0

    @SerializedName("imgUrl")
    val imgUrl: String? = null

    @SerializedName("furnitureType")
    val furnitureType: FurnitureType? = null
}