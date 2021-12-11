package pk.furniture_android_app.furniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.furniture_android_app.OnRecyclerViewClickListener
import pk.furniture_android_app.R
import pk.furniture_android_app.models.furniture.Furniture

class FurnitureRecyclerViewAdapter() :
    RecyclerView.Adapter<FurnitureRecyclerViewAdapter.ViewHolder>() {
    private var furniture = emptyList<Furniture>()
    var onItemClickListener: OnRecyclerViewClickListener? = null

    class ViewHolder(view: View, onItemClickListener: OnRecyclerViewClickListener) :
        RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImageView)
        val cardTitle: TextView = view.findViewById(R.id.cardTitle)
        val cardPrice: TextView = view.findViewById(R.id.cardPrice)

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
            LayoutInflater.from(viewGroup.context).inflate(R.layout.furniture_card, viewGroup, false)
        return ViewHolder(inflate, onItemClickListener!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFurniture = furniture[position]
        holder.cardTitle.text = selectedFurniture.title
        holder.cardPrice.text = String.format("%.2f", selectedFurniture.price) + " zł"
        if (selectedFurniture.imgUrl != null && selectedFurniture.imgUrl.isNotBlank())
            Glide.with(holder.itemView.context).load(selectedFurniture.imgUrl)
                .placeholder(R.drawable.no_image_available).into(holder.cardImage)
        else
            Glide.with(holder.itemView.context).load(R.drawable.no_image_available)
                .into(holder.cardImage)
    }

    override fun getItemCount(): Int {
        return furniture.size
    }

    fun setData(furniture: List<Furniture>) {
        this.furniture = furniture
        notifyDataSetChanged()
    }
}