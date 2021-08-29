package pk.furniture_android_app.models.chairs

import com.google.gson.annotations.SerializedName
import java.util.*

class ChairResponse {
    @SerializedName("totalNumberOfPages")
    val totalNumberOfPages: Int = 1

    @SerializedName("chairsFromSelectedPage")
    val chairsFromSelectedPage: List<Chair> = Collections.emptyList()
}