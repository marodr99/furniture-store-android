package pk.furniture_android_app

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.*
import net.openid.appauth.browser.VersionedBrowserMatcher
import pk.furniture_android_app.keycloak.AuthStateManager
import pk.furniture_android_app.keycloak.ConnectionBuilderForTesting


class RegisterAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("http://192.168.0.11:8090/auth/realms/furniture-app/protocol/openid-connect/auth"),
            Uri.parse("http://192.168.0.11:8090/auth/realms/furniture-app/protocol/openid-connect/token"),
            Uri.parse("http://192.168.0.11:8090/auth/realms/furniture-app/clients-registrations/openid-connect")
        )

        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setConnectionBuilder(ConnectionBuilderForTesting.INSTANCE)
            .setBrowserMatcher(VersionedBrowserMatcher.CHROME_BROWSER)
            .build()

        val authService = AuthorizationService(this, appAuthConfiguration)
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            "furniture-client",
            ResponseTypeValues.CODE,
            Uri.parse("my.scheme://my.host")
        )
            .setScope("openid")
            .build()

        authService.performAuthorizationRequest(
            authRequest,
            PendingIntent.getActivity(this, 0, Intent(this, WelcomePageActivity::class.java), 0)
        )
        AuthStateManager.getInstance(this).replace(AuthState(serviceConfig))
        finish()
    }
}