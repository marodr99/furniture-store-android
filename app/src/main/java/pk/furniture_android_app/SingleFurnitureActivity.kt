package pk.furniture_android_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3
import pk.furniture_android_app.furniture.chairs.ChairsApiService
import pk.furniture_android_app.models.chairs.Chair
import pk.furniture_android_app.models.furniture.FurnitureType
import pk.furniture_android_app.shared.ViewPagerFurnitureAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.ceil

class SingleFurnitureActivity : AppCompatActivity() {

    companion object {
        val MAX_WEIGHT = "Max weight: "
        val WIDTH = "Width: "
        val HEIGHT = "Height: "
        val DEPTH = "Depth: "
        val COLOR = "Color: "
        val MATERIAL = "Material: "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_furniture)

        val furnitureType = intent.getSerializableExtra("furnitureType") as FurnitureType
        val furnitureId = intent.getIntExtra("furnitureId", 0)
        val viewPager: ViewPager2 = findViewById(R.id.singleFurnitureViewPager)

        if (furnitureType == FurnitureType.CHAIRS) {
            val chairsApiService =
                RetrofitClientInstance.getRetrofitInstance()?.create(ChairsApiService::class.java)
            val chairCall = chairsApiService?.getChair(furnitureId)
            chairCall?.enqueue(object : Callback<Chair> {
                override fun onResponse(
                    call: Call<Chair>,
                    response: Response<Chair>
                ) {
                    response.body()?.let {
                        val chair = it
                        setupSharedInfo(chair.title, chair.additionalInformation, chair.price)
                        setup3DButton(chair.fileName)
                        setupViewPager(viewPager, chair.imgUrl, chair.imagesUrl)
                        val chairProperties = arrayOf(
                            MAX_WEIGHT + chair.maxWeight.toString() + "kg",
                            WIDTH + chair.width.toString() + "cm",
                            HEIGHT + chair.height.toString() + "cm",
                            DEPTH + chair.depth.toString() + "cm",
                            COLOR + chair.color,
                            MATERIAL + chair.material,
                        )
                        setupProps(chairProperties)
                    }
                }

                override fun onFailure(call: Call<Chair>, t: Throwable) {
                    Toast.makeText(
                        this@SingleFurnitureActivity,
                        "Connection error. Please try later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun setup3DButton(glbFileName: String?) {
        val button: Button = findViewById(R.id.singleFurnitureCheckIn3d)
        button.setOnClickListener {
            val intent =
                Intent(this@SingleFurnitureActivity, AugmentedRealityActivity::class.java).apply {
                    putExtra("glbFileName", glbFileName)
                }
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSharedInfo(
        furnitureTitle: String?,
        furnitureAdditionalInfo: String?,
        furniturePrice: Double
    ) {
        val title: TextView = findViewById(R.id.singleFurnitureElementTitle)
        val additionalInfo: TextView = findViewById(R.id.singleFurnitureElementDesc)
        val buyNowButton: Button = findViewById(R.id.singleFurnitureBuyNow)

        title.text = furnitureTitle
        additionalInfo.text = furnitureAdditionalInfo
        buyNowButton.text = "Buy now for $furniturePrice"
    }

    private fun setupViewPager(
        viewPager: ViewPager2,
        frontImage: String?,
        additionalImages: String?
    ) {
        val allImages = "$frontImage^$additionalImages"
        viewPager.adapter = ViewPagerFurnitureAdapter(allImages.split("^"))
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator: CircleIndicator3 = findViewById(R.id.singleFurnitureCircleIndicator)
        indicator.setViewPager(viewPager)
    }

    private fun setupProps(props: Array<String>) {
        val leftPropsConstraint = findViewById<ConstraintLayout>(R.id.singleFurnitureLeftProps)
        val rightPropsConstraint = findViewById<ConstraintLayout>(R.id.singleFurnitureRightProps)
        val numberOfLeftSideProps = ceil(props.size.toDouble() / 2).toInt()

        val textViewList: MutableList<TextView> = mutableListOf()

        for (i in props.indices) {
            val textView = TextView(this)
            textView.text = props[i]
            textView.id = View.generateViewId()
            textViewList.add(textView)

            if (i < numberOfLeftSideProps)
                leftPropsConstraint.addView(textView)
            else
                rightPropsConstraint.addView(textView)
        }

        val constraintSetLeft = ConstraintSet()
        constraintSetLeft.clone(leftPropsConstraint)
        val constraintSetRight = ConstraintSet()
        constraintSetRight.clone(rightPropsConstraint)

        chainTextViews(
            0,
            numberOfLeftSideProps,
            constraintSetLeft,
            textViewList,
            leftPropsConstraint,
            ConstraintSet.LEFT
        )
        chainTextViews(
            numberOfLeftSideProps,
            props.size,
            constraintSetRight,
            textViewList,
            rightPropsConstraint,
            ConstraintSet.RIGHT
        )

    }

    private fun chainTextViews(
        startIndex: Int,
        edgeIndex: Int,
        constraintSet: ConstraintSet,
        textViewList: MutableList<TextView>,
        constraintLayout: ConstraintLayout?,
        constraintConnection: Int
    ) {
        var previousItem: View? = null
        val textViewIds: MutableList<Int> = mutableListOf()
        for (i in startIndex until edgeIndex) {
            val isLastItem = i == edgeIndex - 1
            constraintSet.connect(
                textViewList[i].id,
                constraintConnection,
                ConstraintSet.PARENT_ID,
                constraintConnection
            )
            if (previousItem == null) {
                constraintSet.connect(
                    textViewList[i].id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
            } else {
                constraintSet.connect(
                    textViewList[i].id,
                    ConstraintSet.TOP,
                    previousItem.id,
                    ConstraintSet.BOTTOM
                )
                if (isLastItem)
                    constraintSet.connect(
                        textViewList[i].id,
                        ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM
                    )
            }
            textViewIds.add(textViewList[i].id)
            previousItem = textViewList[i]
        }
        constraintSet.createVerticalChain(
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            textViewIds.toIntArray(),
            null,
            ConstraintSet.CHAIN_SPREAD_INSIDE
        )
        constraintSet.applyTo(constraintLayout)
    }
}