package pk.furniture_android_app.models.furniture

import com.google.gson.annotations.SerializedName
import java.util.*

class FurnitureResponse {
    @SerializedName("totalNumberOfPages")
    val totalNumberOfPages: Int = 1

    @SerializedName("furnitureFromPage")
    val furnitureFromPage: List<Furniture> = Collections.emptyList()
}