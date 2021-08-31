package pk.furniture_android_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

        setupViewPager(viewPager, chair)
        setupChairInfo(chair)
        setup3DButton(chair)
    }

    private fun setup3DButton(chair: Chair) {
        val button: Button = findViewById(R.id.singleChairCheckIn3d)
        button.setOnClickListener {
            val intent =
                Intent(this@SingleChairActivity, AugmentedRealityActivity::class.java).apply {
                    putExtra("glbFileName", chair.fileName)
                }
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupChairInfo(chair: Chair) {
        val title: TextView = findViewById(R.id.singleChairElementTitle)
        val maxWeight: TextView = findViewById(R.id.singleChairElementProp1)
        val width: TextView = findViewById(R.id.singleChairElementProp2)
        val height: TextView = findViewById(R.id.singleChairElementProp3)
        val depth: TextView = findViewById(R.id.singleChairElementProp4)
        val color: TextView = findViewById(R.id.singleChairElementProp5)
        val material: TextView = findViewById(R.id.singleChairElementProp6)
        val additionalInfo: TextView = findViewById(R.id.singleChairElementDesc)
        val buyNowButton: Button = findViewById(R.id.singleChairBuyNow)

        title.text = chair.title
        maxWeight.text = "Max weight: " + chair.maxWeight
        width.text = "Width: " + chair.width
        height.text = "Height: " + chair.height
        depth.text = "Depth: " + chair.depth
        color.text = "Color: " + chair.color
        material.text = "Material: " + chair.material
        additionalInfo.text = chair.additionalInformation
        buyNowButton.text = "Buy now for " + chair.price
    }

    private fun setupViewPager(
        viewPager: ViewPager2,
        chair: Chair
    ) {
        viewPager.adapter = chair.imageUrl?.let { ViewPagerFurnitureAdapter(it.split("^")) }
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator: CircleIndicator3 = findViewById(R.id.singleChairCircleIndicator)
        indicator.setViewPager(viewPager)
    }
}