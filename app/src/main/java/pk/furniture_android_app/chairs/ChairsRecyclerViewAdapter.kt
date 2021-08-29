package pk.furniture_android_app.chairs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.furniture_android_app.OnRecyclerViewClickListener
import pk.furniture_android_app.R
import pk.furniture_android_app.models.chairs.Chair

class ChairsRecyclerViewAdapter() :
    RecyclerView.Adapter<ChairsRecyclerViewAdapter.ViewHolder>() {
    private var chairs = emptyList<Chair>()
    var onItemClickListener: OnRecyclerViewClickListener? = null

    class ViewHolder(view: View, onItemClickListener: OnRecyclerViewClickListener) :
        RecyclerView.ViewHolder(view) {
        val chairCardImage: ImageView = view.findViewById(R.id.chairCardImageView)
        val chairCardTitle: TextView = view.findViewById(R.id.chairCardTitle)
        val chairCardPrice: TextView = view.findViewById(R.id.chairCardPrice)

        init {
            view.setOnClickListener {
                if (onItemClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.chair_card, viewGroup, false)
        return ViewHolder(inflate, onItemClickListener!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chair = chairs[position]
        holder.chairCardTitle.text = chair.title
        holder.chairCardPrice.text = chair.price.toString() + " zł"
        if (chair.imageUrl != null && chair.imageUrl.isNotBlank())
            Glide.with(holder.itemView.context).load(chair.imageUrl)
                .placeholder(R.drawable.no_image_available).into(holder.chairCardImage)
        else
            Glide.with(holder.itemView.context).load(R.drawable.no_image_available)
                .into(holder.chairCardImage)
    }

    override fun getItemCount(): Int {
        return chairs.size
    }

    fun setData(chairs: List<Chair>) {
        this.chairs = chairs
        notifyDataSetChanged()
    }
}