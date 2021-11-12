package pk.furniture_android_app

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pk.furniture_android_app.users.User
import pk.furniture_android_app.users.UsersApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class RegisterAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_account)

        val registerNowButton: Button = findViewById(R.id.registerNowButton)

        registerNowButton.setOnClickListener {
            val emailInput: TextInputEditText = findViewById(R.id.emailInput)
            val passwordInput: TextInputEditText = findViewById(R.id.passwordInput)
            val retypePasswordInput: TextInputEditText = findViewById(R.id.retypePasswordInput)

            val emailTextLayout: TextInputLayout = findViewById(R.id.emailInputLayout)
            val passwordTextLayout: TextInputLayout = findViewById(R.id.passwordInputLayout)
            val retypePasswordTextLayout: TextInputLayout =
                findViewById(R.id.retypePasswordInputLayout)

            setErrorMessages(
                emailInput, emailTextLayout, passwordInput, passwordTextLayout,
                retypePasswordInput, retypePasswordTextLayout
            )
            val emailText = emailInput.text
            val passwordText = passwordInput.text
            if (isValidEmail(emailText) && isValidPassword(passwordText) &&
                retypePasswordInput.text != null && retypePasswordInput.text.toString() == passwordText.toString()
            ) {
                val usersApiService =
                    RetrofitClientInstance.getRetrofitInstance()
                        ?.create(UsersApiService::class.java)
                usersApiService?.createUser(User(emailText.toString(), passwordText.toString()))
                    ?.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            when (response.code()) {
                                201 -> println("Stworzono")
                                409 -> emailTextLayout.error = "Email already exist"
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(
                                this@RegisterAccountActivity,
                                "Connectivity error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }
        }
    }

    private fun setErrorMessages(
        emailInput: TextInputEditText,
        emailTextLayout: TextInputLayout,
        passwordInput: TextInputEditText,
        passwordTextLayout: TextInputLayout,
        retypePasswordInput: TextInputEditText,
        retypePasswordTextLayout: TextInputLayout
    ) {
        if (!isValidEmail(emailInput.text))
            emailTextLayout.error = "Wrong email structure"
        else
            emailTextLayout.error = null
        if (!isValidPassword(passwordInput.text))
            passwordTextLayout.error = "Incorrect password structure"
        else
            passwordTextLayout.error = null
        if (retypePasswordInput.text == null || retypePasswordInput.text.toString() != passwordInput.text.toString())
            retypePasswordTextLayout.error = "Passwords does not match"
        else
            retypePasswordTextLayout.error = null
    }

    private fun isValidEmail(email: Editable?): Boolean {
        if (email == null) return false
        return Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches();
    }

    private fun isValidPassword(password: Editable?): Boolean {
        if (password == null) return false
        val PASSWORD_PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\!\\@\\#\\$]{4,24}"
        )
        return !TextUtils.isEmpty(password.toString()) && PASSWORD_PATTERN.matcher(password.toString())
            .matches()
    }
}