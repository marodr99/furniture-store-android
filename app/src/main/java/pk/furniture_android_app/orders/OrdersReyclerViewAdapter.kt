package pk.furniture_android_app.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pk.furniture_android_app.OnRecyclerViewClickListener
import pk.furniture_android_app.R

class OrdersReyclerViewAdapter() : RecyclerView.Adapter<OrdersReyclerViewAdapter.ViewHolder>() {
    private var orders = emptyList<Order>()
    var onItemClickListener: OnRecyclerViewClickListener? = null

    class ViewHolder(view: View, onItemClickListener: OnRecyclerViewClickListener) :
        RecyclerView.ViewHolder(view) {
        val orderCardTitle: TextView = view.findViewById(R.id.myOrdersCardTitle)
        val orderCardFurnitureType: TextView = view.findViewById(R.id.myOrdersListFurnitureType)
        val orderCardDate: TextView = view.findViewById(R.id.myOrdersListDate)

        init {
            view.setOnClickListener {
                if (onItemClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.order_card, parent, false)
        return ViewHolder(inflate, onItemClickListener!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedOrder = orders[position]
        holder.orderCardTitle.text = selectedOrder.title
        holder.orderCardFurnitureType.text = selectedOrder.furnitureType?.name
        holder.orderCardDate.text = selectedOrder.date
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun setData(orders: List<Order>) {
        this.orders = orders
        notifyDataSetChanged()
    }
}