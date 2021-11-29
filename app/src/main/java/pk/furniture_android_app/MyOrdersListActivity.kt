package pk.furniture_android_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService
import pk.furniture_android_app.keycloak.AuthStateManager
import pk.furniture_android_app.keycloak.ConnectionBuilderForTesting
import pk.furniture_android_app.orders.Order
import pk.furniture_android_app.orders.OrdersApiService
import pk.furniture_android_app.orders.OrdersReyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrdersListActivity : AppCompatActivity() {
    private val adapter by lazy { OrdersReyclerViewAdapter() }
    private var orders: List<Order> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders_list)
        val authStateManager = AuthStateManager.getInstance(this)
        val authState = authStateManager.current

        val ordersApiCall =
            RetrofitClientInstance.getRetrofitInstance()?.create(OrdersApiService::class.java)

        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setConnectionBuilder(ConnectionBuilderForTesting.INSTANCE).build()
        val authorizationService = AuthorizationService(this, appAuthConfiguration)
        authState.performActionWithFreshTokens(
            authorizationService
        ) { accessToken, _, ex ->
            if (ex != null) {
                Toast.makeText(this, "Failed to connect to KeyCloak", Toast.LENGTH_SHORT).show()
                return@performActionWithFreshTokens
            }
            if (accessToken != null) {
                ordersApiCall?.getOrders("Bearer $accessToken")
                    ?.enqueue(object : Callback<List<Order>> {
                        override fun onResponse(
                            call: Call<List<Order>>,
                            response: Response<List<Order>>
                        ) {
                            if (response.body() != null) {
                                response.body()?.let {
                                    adapter.setData(it)
                                    orders = it
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                            Toast.makeText(
                                this@MyOrdersListActivity,
                                "Failed to connect to Api",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
            authStateManager.replace(authState)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.myOrdersListRecyclerView)
        adapter.onItemClickListener = object : OnRecyclerViewClickListener {
            override fun onItemClick(position: Int) {
                val clickedOrder = orders[position]
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}