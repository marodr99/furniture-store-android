package pk.furniture_android_app

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
                authStateManager.updateAfterTokenResponse(response, tokenEx)
            }

            val authState = authStateManager.current
            setUpLogoutButton(authState, authService)
        }
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