package pk.furniture_android_app.chairs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import pk.furniture_android_app.R
import pk.furniture_android_app.models.Chair

class ChairsListViewAdapter(private val chairs: List<Chair>, private val context: Context) :
    BaseAdapter() {
    override fun getCount(): Int {
        return chairs.size
    }

    override fun getItem(pos: Int): Any {
        return chairs[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getView(pos: Int, view: View?, viewGroup: ViewGroup?): View {
        val retView: View =
            view ?: LayoutInflater.from(context).inflate(R.layout.chair_card, viewGroup, false)

        val chairCardImage = retView.findViewById<ImageView>(R.id.chairCardImageView)
        val chairCardTitle = retView.findViewById<TextView>(R.id.chairCardTitle)
        val chairCardPrice = retView.findViewById<TextView>(R.id.chairCardPrice)

        val currentChair = chairs[pos]

        chairCardTitle.text = currentChair.title
        chairCardPrice.text = currentChair.price.toString() + " zł"

        if (currentChair.imageUrl != null && currentChair.imageUrl.isNotBlank()) {
            Picasso.get().load(currentChair.imageUrl).into(chairCardImage)
        } else {
            Picasso.get().load(R.drawable.no_image_available).into(chairCardImage)
        }

        retView.setOnClickListener {
            Toast.makeText(context, currentChair.fileName, Toast.LENGTH_SHORT).show()
        }

        return retView
    }
}