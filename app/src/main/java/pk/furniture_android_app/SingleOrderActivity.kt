package pk.furniture_android_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService
import pk.furniture_android_app.keycloak.AuthStateManager
import pk.furniture_android_app.keycloak.ConnectionBuilderForTesting
import pk.furniture_android_app.orders.OrdersApiService
import pk.furniture_android_app.orders.SingleOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_order)

        val authStageManager = AuthStateManager.getInstance(this)
        val authState = authStageManager.current

        val ordersApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(OrdersApiService::class.java)

        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setConnectionBuilder(ConnectionBuilderForTesting.INSTANCE).build()
        val authorizationService = AuthorizationService(this, appAuthConfiguration)
        authState.performActionWithFreshTokens(
            authorizationService
        ) { accessToken, _, ex ->
            if (ex != null) {
                Toast.makeText(this, "Can not connect to KeyCloak", Toast.LENGTH_SHORT).show()
                return@performActionWithFreshTokens
            }
            val orderId = intent.getIntExtra("orderId", 0)
            if (accessToken != null) {
                ordersApiService?.getOrder("Bearer $accessToken", orderId)
                    ?.enqueue(object : Callback<SingleOrder> {
                        override fun onResponse(
                            call: Call<SingleOrder>,
                            response: Response<SingleOrder>
                        ) {
                            setUpOrderInfo(response.body()!!)
                        }

                        override fun onFailure(call: Call<SingleOrder>, t: Throwable) {
                            Toast.makeText(
                                this@SingleOrderActivity,
                                "Can not connect to Api",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }
            authStageManager.replace(authState)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpOrderInfo(body: SingleOrder) {
        val title: TextView = findViewById(R.id.singleOrderTitle)
        val furnitureType: TextView = findViewById(R.id.singleOrderFurnitureType)
        val date: TextView = findViewById(R.id.singleOrderDate)
        val name: TextView = findViewById(R.id.singleOrderName)
        val surname: TextView = findViewById(R.id.singleOrderSurname)
        val email: TextView = findViewById(R.id.singleOrderEmail)
        val phoneNr: TextView = findViewById(R.id.singleOrderPhoneNumber)
        val houseNr: TextView = findViewById(R.id.singleOrderHouseNumber)
        val flatNr: TextView = findViewById(R.id.singleOrderFlatNumber)
        val street: TextView = findViewById(R.id.singleOrderStreet)
        val postalCode: TextView = findViewById(R.id.singleOrderPostalCode)
        val city: TextView = findViewById(R.id.singleOrderCity)

        if (body.flatNumber == null || body.flatNumber.isNullOrBlank())
            flatNr.visibility = View.GONE
        else
            flatNr.text = "Flat nr: $body.flatNumber"
        if (body.street == null || body.street.isNullOrBlank())
            street.visibility = View.GONE
        else
            street.text = "Street: $body.street"

        title.text = "Title: " + body.title
        furnitureType.text = "Type: " + body.furnitureType?.name
        date.text = "Date: " + body.date
        name.text = "Name: " + body.name
        surname.text = "Surname: " + body.surname
        email.text = "Email: " + body.email
        phoneNr.text = "Phone: " + body.phoneNumber
        houseNr.text = "House nr: " + body.houseNumber
        postalCode.text = "Postal Code: " + body.postalCode
        city.text = "City: " + body.city
    }
}