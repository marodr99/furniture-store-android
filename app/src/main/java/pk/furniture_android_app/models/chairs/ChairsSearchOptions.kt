package pk.furniture_android_app.models.chairs

import pk.furniture_android_app.models.SortOption

class ChairsSearchOptions {
    var title: String? = ""
    var color: String? = null
    var chairMaterial: ChairMaterial? = null
    var startPrice: Double? = 0.0
    var endPrice: Double? = 0.0
    var sortOption: SortOption? = null

    constructor(map: MutableMap<String, String?>) {
        this.title = map.getOrDefault("title", "")
        this.color = map["color"]
        this.chairMaterial =
            if (map["chairMaterial"] == null) null else ChairMaterial.valueOf(map["chairMaterial"]!!)
        this.startPrice = map.getOrDefault("startPrice", "0.0")?.toDouble()
        this.endPrice = map.getOrDefault("endPrice", "0.0")?.toDouble()
        this.sortOption =
            if (map["sortOption"] == null) null else SortOption.valueOf(map["sortOption"]!!)
    }
}