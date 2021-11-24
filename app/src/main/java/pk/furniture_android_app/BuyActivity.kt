package pk.furniture_android_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import pk.furniture_android_app.keycloak.AuthStateManager
import pk.furniture_android_app.keycloak.ConnectionBuilderForTesting
import pk.furniture_android_app.orders.OrderRequest
import pk.furniture_android_app.orders.OrdersApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class BuyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        val authStateManager = AuthStateManager.getInstance(this)
        val authState = authStateManager.current
        val userDetails = getUserDetailsFromToken(authState)

        setupBasicUserInfo(userDetails)
        setupConfirmButton(authState, authStateManager)
    }

    @SuppressLint("SetTextI18n")
    private fun setupConfirmButton(authState: AuthState, authStateManager: AuthStateManager) {
        val confirmButton: Button = findViewById(R.id.confirmBuyButton)
        val price: Double = intent.getDoubleExtra("furniturePrice", 0.0)
        confirmButton.text = "Buy now for $price"

        confirmButtonSetOnClickListener(confirmButton, authState, authStateManager)
    }

    private fun setupBasicUserInfo(userDetails: Map<*, *>?) {
        val nameTextView: TextInputEditText = findViewById(R.id.buyNowNameEditText)
        val surnameTextView: TextInputEditText = findViewById(R.id.buyNowSurnameEditText)
        val emailTextView: TextInputEditText = findViewById(R.id.buyNowEmailEditText)
        nameTextView.setText(userDetails?.get("given_name").toString())
        surnameTextView.setText(userDetails?.get("family_name").toString())
        emailTextView.setText(userDetails?.get("email").toString())
    }

    private fun getUserDetailsFromToken(authState: AuthState): Map<*, *>? {
        val tokenParts = authState.accessToken?.split(".")
        val decoded = String(Base64.decode(tokenParts?.get(1), Base64.DEFAULT))
        val gson = Gson()
        return gson.fromJson(decoded, Map::class.java)
    }

    private fun confirmButtonSetOnClickListener(
        confirmButton: Button, authState: AuthState, authStateManager: AuthStateManager
    ) {
        val ordersApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(OrdersApiService::class.java)

        val nameTextView: TextInputEditText = findViewById(R.id.buyNowNameEditText)
        val surnameTextView: TextInputEditText = findViewById(R.id.buyNowSurnameEditText)
        val phoneTextView: TextInputEditText = findViewById(R.id.buyNowPhoneEditText)
        val houseTextView: TextInputEditText = findViewById(R.id.buyNowHouseEditText)
        val flatTextView: TextInputEditText = findViewById(R.id.buyNowFlatEditText)
        val streetTextView: TextInputEditText = findViewById(R.id.buyNowStreetEditText)
        val postalCodeTextView: TextInputEditText = findViewById(R.id.buyNowPostalCodeEditText)
        val cityTextView: TextInputEditText = findViewById(R.id.buyNowCity)
        val emailTextView: TextInputEditText = findViewById(R.id.buyNowEmailEditText)

        confirmButton.setOnClickListener {
            if (!areRequiredFieldsCorrect(
                    nameTextView, surnameTextView, phoneTextView,
                    houseTextView, postalCodeTextView, cityTextView, emailTextView
                )
            )
                return@setOnClickListener
            val orderRequest = createOrderRequest(
                nameTextView,
                surnameTextView,
                phoneTextView,
                houseTextView,
                flatTextView,
                streetTextView,
                postalCodeTextView,
                cityTextView,
                emailTextView
            )
            val appAuthConfiguration = AppAuthConfiguration.Builder()
                .setConnectionBuilder(ConnectionBuilderForTesting.INSTANCE).build()
            val authorizationService = AuthorizationService(this, appAuthConfiguration)
            authState.performActionWithFreshTokens(authorizationService) { accessToken, _, ex ->
                createOrder(
                    ex,
                    accessToken,
                    ordersApiService,
                    authStateManager,
                    authState,
                    orderRequest
                )
            }
        }
    }

    private fun createOrderRequest(
        nameTextView: TextInputEditText,
        surnameTextView: TextInputEditText,
        phoneTextView: TextInputEditText,
        houseTextView: TextInputEditText,
        flatTextView: TextInputEditText,
        streetTextView: TextInputEditText,
        postalCodeTextView: TextInputEditText,
        cityTextView: TextInputEditText,
        emailTextView: TextInputEditText
    ): OrderRequest {
        val orderRequest = OrderRequest()
        orderRequest.furnitureId = intent.getIntExtra("furnitureId", 0)
        orderRequest.name = nameTextView.text.toString()
        orderRequest.surname = surnameTextView.text.toString()
        orderRequest.phoneNumber = phoneTextView.text.toString()
        orderRequest.houseNumber = houseTextView.text.toString()
        orderRequest.flatNumber = flatTextView.text.toString()
        orderRequest.street = streetTextView.text.toString()
        orderRequest.postalCode = postalCodeTextView.text.toString()
        orderRequest.city = cityTextView.text.toString()
        orderRequest.orderEmail = emailTextView.text.toString()
        return orderRequest
    }

    private fun areRequiredFieldsCorrect(
        nameTextView: TextInputEditText,
        surnameTextView: TextInputEditText,
        phoneTextView: TextInputEditText,
        houseTextView: TextInputEditText,
        postalCodeTextView: TextInputEditText,
        cityTextView: TextInputEditText,
        emailTextView: TextInputEditText
    ): Boolean {
        val phonePattern = Pattern.compile("^(\\+[0-9]{1,2})?[0-9]{9}\$")
        val postalCodePattern = Pattern.compile("^[0-9]{5}$")
        if (nameTextView.text == null || nameTextView.text.toString().isBlank())
            nameTextView.error = "Name is required"
        else
            nameTextView.error = null
        if (surnameTextView.text == null || surnameTextView.text.toString().isBlank())
            surnameTextView.error = "Surname is required"
        else
            surnameTextView.error = null
        if (phoneTextView.text == null || !phonePattern.matcher(phoneTextView.text.toString())
                .matches()
        )
            phoneTextView.error = "Phone number is required"
        else
            phoneTextView.error = null
        if (houseTextView.text == null || houseTextView.text.toString().isBlank())
            houseTextView.error = "House number is required"
        else
            houseTextView.error = null
        if (postalCodeTextView.text == null || !postalCodePattern.matcher(postalCodeTextView.text.toString())
                .matches()
        )
            postalCodeTextView.error = "Postal code is required"
        else
            postalCodeTextView.error = null
        if (cityTextView.text == null || cityTextView.text.toString().isBlank())
            cityTextView.error = "City is required"
        else
            cityTextView.error = null
        if (emailTextView.text == null || !Patterns.EMAIL_ADDRESS.matcher(emailTextView.text.toString())
                .matches()
        )
            emailTextView.error = "Email address is required"
        else
            emailTextView.error = null

        return !(nameTextView.error != null || surnameTextView.error != null ||
                phoneTextView.error != null || houseTextView.error != null ||
                postalCodeTextView.error != null || cityTextView.error != null || emailTextView.error != null)


    }


    private fun createOrder(
        ex: AuthorizationException?,
        accessToken: String?,
        ordersApiService: OrdersApiService?,
        authStateManager: AuthStateManager,
        authState: AuthState,
        orderRequest: OrderRequest
    ) {
        if (ex != null) {
            Toast.makeText(
                this@BuyActivity,
                "Keycloak connection error",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (accessToken != null) {
            ordersApiService?.createOrder("Bearer $accessToken", orderRequest)
                ?.enqueue(object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        val textViewMessage: TextView = findViewById(R.id.buyNowErrorMessage)
                        if (response.code() == 201) {
                            textViewMessage.text = "Order created successfully"
                            Thread.sleep(2_000)
                            finish()
                        } else
                            textViewMessage.text = response.message()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@BuyActivity,
                            "Api connection error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }
        authStateManager.replace(authState)
    }
}