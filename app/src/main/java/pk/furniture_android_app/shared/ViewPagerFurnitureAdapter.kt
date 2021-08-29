package pk.furniture_android_app.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.furniture_android_app.R

class ViewPagerFurnitureAdapter(private var images: List<String>) :
    RecyclerView.Adapter<ViewPagerFurnitureAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.viewPagerFurnitureImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerFurnitureAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_viewpager_furniture, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewPagerFurnitureAdapter.ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(images[position]).into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}