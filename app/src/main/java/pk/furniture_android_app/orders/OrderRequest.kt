package pk.furniture_android_app.orders

import com.google.gson.annotations.SerializedName

class OrderRequest {
    @SerializedName("furnitureId")
    var furnitureId: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("surname")
    var surname: String? = null

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null

    @SerializedName("houseNumber")
    var houseNumber: String? = null

    @SerializedName("flatNumber")
    var flatNumber: String? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("postalCode")
    var postalCode: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("orderEmail")
    var orderEmail: String? = null
}