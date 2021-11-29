package pk.furniture_android_app.orders

import pk.furniture_android_app.models.furniture.FurnitureType

class Order {
    var orderId: Int? = null
    var title: String? = null
    var furnitureType: FurnitureType? = null
    var date: String? = null
}