package pk.furniture_android_app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pk.furniture_android_app.chairs.ChairsApiService
import pk.furniture_android_app.chairs.ChairsRecyclerViewAdapter
import pk.furniture_android_app.models.chairs.ChairResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChairsListActivity : AppCompatActivity() {
    private val adapter by lazy { ChairsRecyclerViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chairs_list)

        val progressBar = findViewById<ProgressBar>(R.id.chairsProgressBar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        setupRecyclerView()

        val chairsApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(ChairsApiService::class.java)

        val allChairsCall = chairsApiService?.getAllChairs(0)
        allChairsCall?.enqueue(object : Callback<ChairResponse> {
            override fun onResponse(call: Call<ChairResponse>, response: Response<ChairResponse>) {
                progressBar.visibility = View.GONE
                response.body()?.let { adapter.setData(it.chairsFromSelectedPage) }
            }

            override fun onFailure(call: Call<ChairResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ChairsListActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.chairsRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}