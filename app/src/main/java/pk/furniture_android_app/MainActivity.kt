package pk.furniture_android_app

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var renderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child("football_table.glb")

        val arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        findViewById<Button>(R.id.downloadBtn).setOnClickListener {
            try {
                findViewById<TextView>(R.id.requiredDownloadMessage).visibility = View.GONE
                val file = File.createTempFile("football_table", "glb")
                modelRef.getFile(file).addOnSuccessListener {
                    buildModel(file)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            if (renderable == null) {
                findViewById<TextView>(R.id.requiredDownloadMessage).visibility = View.VISIBLE
            } else {
                val anchorNode = AnchorNode(hitResult.createAnchor())
                anchorNode.renderable = renderable
                arFragment.arSceneView.scene.addChild(anchorNode)
            }
        }

        arFragment.arSceneView.scene.addOnPeekTouchListener { hitTestResult, motionEvent ->
            if(hitTestResult.node != null) {
                val hitNode = hitTestResult.node
                hitNode?.setParent(null)
            }
        }
    }

    private fun buildModel(file: File) {
        val renderableSource = RenderableSource
            .builder()
            .setSource(this, Uri.parse(file.path), RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
            .build()
        ModelRenderable
            .builder()
            .setSource(this, renderableSource)
            .setRegistryId(file.path)
            .build()
            .thenAccept { modelRenderable ->
                Toast.makeText(this, "Model built", Toast.LENGTH_SHORT).show()
                renderable = modelRenderable
            }
    }
}