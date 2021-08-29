package pk.furniture_android_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import me.relex.circleindicator.CircleIndicator3
import pk.furniture_android_app.models.chairs.Chair
import pk.furniture_android_app.shared.ViewPagerFurnitureAdapter

class SingleChairActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chair)

        val gson = Gson()
        val viewPager: ViewPager2 = findViewById(R.id.singleChairViewPager)
        val chair: Chair = gson.fromJson(intent.getStringExtra("chair"), Chair::class.java)
        viewPager.adapter = chair.imageUrl?.let { ViewPagerFurnitureAdapter(it.split("^")) }
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator: CircleIndicator3 = findViewById(R.id.singleChairCircleIndicator)
        indicator.setViewPager(viewPager)
    }
}