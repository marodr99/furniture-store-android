package pk.furniture_android_app.models.wardrobes

import com.google.gson.annotations.SerializedName
import pk.furniture_android_app.models.furniture.FurnitureType

class Wardrobe {
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

    @SerializedName("width")
    val width = 0

    @SerializedName("height")
    val height = 0

    @SerializedName("depth")
    val depth = 0

    @SerializedName("color")
    val color: String? = null

    @SerializedName("wardrobeMaterial")
    val material: String? = null

    @SerializedName("additionalInformation")
    val additionalInformation: String? = null

    @SerializedName("stock")
    val stock = 0

    @SerializedName("fileName")
    val fileName: String? = null

    @SerializedName("imagesUrl")
    val imagesUrl: String? = null

}