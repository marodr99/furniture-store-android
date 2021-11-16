package pk.furniture_android_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pk.furniture_android_app.models.furniture.FurnitureType

class StoreOfferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_offer)
        setUpFurnitureButton(FurnitureType.CHAIRS)
        setUpFurnitureButton(FurnitureType.WARDROBES)
    }

    private fun setUpFurnitureButton(furnitureType: FurnitureType) {
        val button = getProperButton(furnitureType)
        button.setOnClickListener {
            val furnitureListIntent = Intent(this, FurnitureListActivity::class.java).apply {
                putExtra("furnitureType", furnitureType)
            }
            startActivity(furnitureListIntent)
        }
    }

    private fun getProperButton(furnitureType: FurnitureType): Button {
        when (furnitureType) {
            FurnitureType.CHAIRS -> return findViewById(R.id.chairsButton) as Button
            FurnitureType.WARDROBES -> return findViewById(R.id.wardrobesButton) as Button
        }
        throw RuntimeException("No button matched")
    }
}