package pk.furniture_android_app.models

import com.google.gson.annotations.SerializedName

class Chair {
    @SerializedName("id")
    val id = 0

    @SerializedName("title")
    val title: String? = null

    @SerializedName("maxWeight")
    val maxWeight = 0

    @SerializedName("width")
    val width = 0

    @SerializedName("height")
    val height = 0

    @SerializedName("depth")
    val depth = 0

    @SerializedName("color")
    val color: String? = null

    @SerializedName("price")
    val price = 0.0

    @SerializedName("material")
    val material: String? = null

    @SerializedName("additionalInformation")
    val additionalInformation: String? = null

    @SerializedName("stock")
    val stock = 0

    @SerializedName("imageUrl")
    val imageUrl: String? = null

    @SerializedName("fileName")
    val fileName: String? = null

}