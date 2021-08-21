package pk.furniture_android_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import pk.furniture_android_app.chairs.ChairsApiService
import pk.furniture_android_app.chairs.ChairsListViewAdapter
import pk.furniture_android_app.models.Chair
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChairsListActivity : AppCompatActivity() {
    private var adapter: ChairsListViewAdapter? = null
    private var listview: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chairs_list)

        val progressBar = findViewById<ProgressBar>(R.id.chairsProgressBar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        val chairsApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(ChairsApiService::class.java)

        val allChairsCall = chairsApiService?.getAllChairs()
        allChairsCall?.enqueue(object : Callback<List<Chair>> {
            override fun onResponse(call: Call<List<Chair>>, response: Response<List<Chair>>) {
                progressBar.visibility = View.GONE
                response.body()?.let { populateListView(it) }
            }

            override fun onFailure(call: Call<List<Chair>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ChairsListActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun populateListView(chairs: List<Chair>) {
        listview = findViewById(R.id.chairsListView)
        adapter = ChairsListViewAdapter(chairs, this)
        if (listview != null) {
            listview!!.adapter = adapter
        }
    }
}