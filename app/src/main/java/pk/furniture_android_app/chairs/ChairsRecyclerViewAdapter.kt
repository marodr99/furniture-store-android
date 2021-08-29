package pk.furniture_android_app.chairs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pk.furniture_android_app.R
import pk.furniture_android_app.models.chairs.Chair

class ChairsRecyclerViewAdapter() :
    RecyclerView.Adapter<ChairsRecyclerViewAdapter.ViewHolder>() {
    private var chairs = emptyList<Chair>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chairCardImage: ImageView = view.findViewById(R.id.chairCardImageView)
        val chairCardTitle: TextView = view.findViewById(R.id.chairCardTitle)
        val chairCardPrice: TextView = view.findViewById(R.id.chairCardPrice)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.chair_card, viewGroup, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chair = chairs[position]
        holder.chairCardTitle.text = chair.title
        holder.chairCardPrice.text = chair.price.toString() + " zł"
        if (chair.imageUrl != null && chair.imageUrl.isNotBlank())
            Picasso.get().load(chair.imageUrl).into(holder.chairCardImage)
        else
            Picasso.get().load(R.drawable.no_image_available).into(holder.chairCardImage)
    }

    override fun getItemCount(): Int {
        return chairs.size
    }

    fun setData(chairs: List<Chair>) {
        this.chairs = chairs
        notifyDataSetChanged()
    }
}