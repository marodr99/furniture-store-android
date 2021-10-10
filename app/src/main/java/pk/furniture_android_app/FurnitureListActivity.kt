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
import pk.furniture_android_app.furniture.FurnitureApiService
import pk.furniture_android_app.furniture.FurnitureRecyclerViewAdapter
import pk.furniture_android_app.models.furniture.Furniture
import pk.furniture_android_app.models.furniture.FurnitureResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FurnitureListActivity : AppCompatActivity() {
    private val adapter by lazy { FurnitureRecyclerViewAdapter() }
    private var furniture: List<Furniture> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_furniture_list)

        val progressBar = findViewById<ProgressBar>(R.id.listProgressBar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        setupRecyclerView()

        val furnitureApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(FurnitureApiService::class.java)

        val allFurnitureCall = furnitureApiService?.getAllFurniture(
            "chairs",
            intent.getIntExtra("currentPage", 0)
        )
        allFurnitureCall?.enqueue(object : Callback<FurnitureResponse> {
            override fun onResponse(call: Call<FurnitureResponse>, response: Response<FurnitureResponse>) {
                progressBar.visibility = View.GONE

                response.body()?.let {
                    adapter.setData(it.furnitureFromPage)
                    furniture = it.furnitureFromPage
                    setPages(it)
                }
            }

            override fun onFailure(call: Call<FurnitureResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@FurnitureListActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setPages(response: FurnitureResponse) {
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
        val recyclerView: RecyclerView = findViewById(R.id.listRecyclerView)
        adapter.onItemClickListener = object : OnRecyclerViewClickListener {
            override fun onItemClick(position: Int) {
                val clickedFurniture = furniture[position]
                val intent =
                    Intent(this@FurnitureListActivity, SingleFurnitureActivity::class.java).apply {
                        putExtra("furnitureType", clickedFurniture.furnitureType)
                        putExtra("furnitureId", clickedFurniture.id)
                    }
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}