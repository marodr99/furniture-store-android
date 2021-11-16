package pk.furniture_android_app.models.wardrobes

import pk.furniture_android_app.models.SortOption

class WardrobesSearchOptions {
    var title: String? = ""
    var color: String? = null
    var wardrobeMaterial: WardrobeMaterial? = null
    var startPrice: Double? = 0.0
    var endPrice: Double? = 0.0
    var sortOption: SortOption? = null

    constructor(map: MutableMap<String, String?>) {
        this.title = map.getOrDefault("title", "")
        this.color = if (map["color"].isNullOrBlank()) null else map["color"]
        this.wardrobeMaterial =
            if (map["material"].isNullOrBlank()) null else WardrobeMaterial.valueOf(map["material"]!!)
        this.startPrice = map.getOrDefault("startPrice", "0.0")?.toDouble()
        this.endPrice = map.getOrDefault("endPrice", "0.0")?.toDouble()
        this.sortOption =
            if (map["sortOption"].isNullOrBlank()) null else SortOption.valueOf(map["sortOption"]!!)
    }
}