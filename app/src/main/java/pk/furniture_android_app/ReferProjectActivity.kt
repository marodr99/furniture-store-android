package pk.furniture_android_app

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import net.openid.appauth.AuthState
import pk.furniture_android_app.keycloak.AuthStateManager
import java.io.File
import java.util.*

class ReferProjectActivity : AppCompatActivity() {
    var file: File? = null
    val ZIP_EXTENSION = ".zip"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_project)

        val zipImage: ImageView = findViewById(R.id.zip_image)
        zipImage.setOnClickListener {
            getContent.launch("application/zip")
        }

        val authState = AuthStateManager.getInstance(this).current
        val userDetailsFromToken = getUserDetailsFromToken(authState)
        val email = userDetailsFromToken?.get("email")


        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child("${email}/" + UUID.randomUUID() + ZIP_EXTENSION)
        val uploadButton: Button = findViewById(R.id.button_upload)
        uploadButton.setOnClickListener {
            val stream = modelRef.putBytes(file?.readBytes()!!)
            stream.addOnSuccessListener {
                Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
            stream.addOnFailureListener {
                Toast.makeText(this, "File uploaded failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val file = File.createTempFile("temp", ".zip")
        val inputStream = contentResolver.openInputStream(uri!!)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        this.file = file
        inputStream?.close()
        val progressBar: ProgressBar = findViewById(R.id.zip_upload_progress)
        progressBar.progress = 100
    }

    private fun getUserDetailsFromToken(authState: AuthState): Map<*, *>? {
        val tokenParts = authState.accessToken?.split(".")
        val decoded = String(Base64.decode(tokenParts?.get(1), Base64.DEFAULT))
        val gson = Gson()
        return gson.fromJson(decoded, Map::class.java)
    }
}