package pk.furniture_android_app

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import net.openid.appauth.*
import net.openid.appauth.browser.VersionedBrowserMatcher
import pk.furniture_android_app.keycloak.AuthStateManager
import pk.furniture_android_app.keycloak.ConnectionBuilderForTesting

class WelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)

        val authStateManager = AuthStateManager.getInstance(this)
        val resp = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)
        authStateManager.updateAfterAuthorization(resp, ex)

        if (resp != null) {
            val appAuthConfiguration = AppAuthConfiguration.Builder()
                .setConnectionBuilder(ConnectionBuilderForTesting.INSTANCE)
                .setBrowserMatcher(VersionedBrowserMatcher.CHROME_BROWSER).build()
            val authService = AuthorizationService(this, appAuthConfiguration)
            authService.performTokenRequest(
                resp.createTokenExchangeRequest()
            ) { response, tokenEx ->
                val accessToken =
                    authStateManager.updateAfterTokenResponse(response, tokenEx).accessToken
                val tokenParts = accessToken?.split(".")
                val decoded = String(Base64.decode(tokenParts?.get(1), Base64.DEFAULT))
                val gson = Gson()
                val userDetails = gson.fromJson(decoded, Map::class.java)

                val authState = authStateManager.current
                setWelcomeText(userDetails["given_name"] as String)
                setUpLogoutButton(authState, authService)
                setUpMyAccountButton(userDetails["sub"] as String)
            }
        }
    }

    private fun setUpMyAccountButton(clientId: String) {
        val myAccountButton: Button = findViewById(R.id.myAccount)
        myAccountButton.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://192.168.0.11:8090/auth/realms/furniture-app/account?referrer=$clientId")
            )
            startActivity(browserIntent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWelcomeText(givenName: String?) {
        val welcomeText: TextView = findViewById(R.id.welcomeText)
        welcomeText.text = "Welcome, $givenName"
    }

    private fun setUpLogoutButton(
        authState: AuthState,
        authService: AuthorizationService
    ) {
        val logOutButton: Button = findViewById(R.id.logOutButton)
        logOutButton.setOnClickListener {

            val endSessionRequest =
                EndSessionRequest.Builder(authState.authorizationServiceConfiguration!!)
                    .setIdTokenHint(authState.idToken)
                    .setPostLogoutRedirectUri(Uri.parse("my.scheme://my.host")).build()
            authService.performEndSessionRequest(
                endSessionRequest,
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, RegisterAccountActivity::class.java),
                    0
                )
            )
            finish()
        }
    }
}