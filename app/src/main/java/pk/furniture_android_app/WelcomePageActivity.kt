package pk.furniture_android_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
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
                .setConnectionBuilder(ConnectionBuilderForTesting.INSTANCE).build()
            val authService = AuthorizationService(this, appAuthConfiguration)
            authService.performTokenRequest(
                resp.createTokenExchangeRequest()
            ) { response, tokenEx -> authStateManager.updateAfterTokenResponse(response, tokenEx)
            }
        }
    }
}