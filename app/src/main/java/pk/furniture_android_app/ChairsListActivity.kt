package pk.furniture_android_app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import pk.furniture_android_app.chairs.ChairsApiService
import pk.furniture_android_app.chairs.ChairsRecyclerViewAdapter
import pk.furniture_android_app.models.chairs.Chair
import pk.furniture_android_app.models.chairs.ChairResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChairsListActivity : AppCompatActivity() {
    private val adapter by lazy { ChairsRecyclerViewAdapter() }
    private var chairs: List<Chair> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chairs_list)

        val progressBar = findViewById<ProgressBar>(R.id.chairsProgressBar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        setupRecyclerView()

        val chairsApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(ChairsApiService::class.java)

        val allChairsCall = chairsApiService?.getAllChairs(intent.getIntExtra("currentPage", 0))
        allChairsCall?.enqueue(object : Callback<ChairResponse> {
            override fun onResponse(call: Call<ChairResponse>, response: Response<ChairResponse>) {
                progressBar.visibility = View.GONE

                response.body()?.let {
                    adapter.setData(it.chairsFromSelectedPage)
                    chairs = it.chairsFromSelectedPage
                    setPages(it)
                }
            }

            override fun onFailure(call: Call<ChairResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ChairsListActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setPages(response: ChairResponse) {
        val previousButton: Button = findViewById(R.id.previousPageButton)
        val nextButton: Button = findViewById(R.id.nextPageButton)
        val allPagesTxt: TextView = findViewById(R.id.allPages)
        val currentPageTxt: TextView = findViewById(R.id.currentPageNumber)

        val currentPage = intent.getIntExtra("currentPage", 0)

        currentPageTxt.text = (currentPage + 1).toString()
        currentPageTxt.isCursorVisible = true
        currentPageTxt.isFocusableInTouchMode = true
        currentPageTxt.inputType = InputType.TYPE_CLASS_TEXT
        currentPageTxt.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val page = Integer.valueOf(textView.text.toString()) - 1
                if (page <= response.totalNumberOfPages - 1 && page >= 0) {
                    intent.putExtra("currentPage", page)
                    recreate()
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }
        allPagesTxt.text = response.totalNumberOfPages.toString()
        previousButton.setOnClickListener {
            if (currentPage > 0) {
                intent.putExtra("currentPage", currentPage - 1)
                recreate()
            }
        }
        nextButton.setOnClickListener {
            if (currentPage < response.totalNumberOfPages - 1) {
                intent.putExtra("currentPage", currentPage + 1)
                recreate()
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.chairsRecyclerView)
        adapter.onItemClickListener = object : OnRecyclerViewClickListener {
            override fun onItemClick(position: Int) {
                val gson = Gson()
                val chairJson = gson.toJson(chairs[position])
                val intent =
                    Intent(this@ChairsListActivity, SingleChairActivity::class.java).apply {
                        putExtra("chair", chairJson)
                    }
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}