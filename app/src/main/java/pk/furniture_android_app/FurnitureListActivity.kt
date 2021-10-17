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
import pk.furniture_android_app.models.chairs.ChairsSearchOptions
import pk.furniture_android_app.models.furniture.Furniture
import pk.furniture_android_app.models.furniture.FurnitureResponse
import pk.furniture_android_app.models.furniture.FurnitureType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FurnitureListActivity : AppCompatActivity() {
    private val adapter by lazy { FurnitureRecyclerViewAdapter() }
    private var furniture: List<Furniture> = emptyList()
    private var selectedSearchOptions: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_furniture_list)

        val progressBar = findViewById<ProgressBar>(R.id.listProgressBar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        setupRecyclerView()
        setupSearchView()

        val allFurnitureCall = createProperFurnitureCall(FurnitureType.CHAIRS)
        allFurnitureCall?.enqueue(object : Callback<FurnitureResponse> {
            override fun onResponse(
                call: Call<FurnitureResponse>,
                response: Response<FurnitureResponse>
            ) {
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

    private fun setupSearchView() {
        val mutableMap =
            intent.getSerializableExtra("selectedSearchOptions")
        if (mutableMap != null)
            this.selectedSearchOptions = mutableMap as HashMap<String, String>

        val searchView: SearchView = findViewById(R.id.searchViewFurniture)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(submitedText: String?): Boolean {
                if (submitedText != null) {
                    selectedSearchOptions["title"] = submitedText
                    intent.putExtra("selectedSearchOptions", selectedSearchOptions)
                    intent.putExtra("canRecreate", true)
                    intent.putExtra("currentPage", 0)

                    recreate()
                }
                return true
            }

            override fun onQueryTextChange(submitedText: String?): Boolean {
                if (submitedText.equals("")) {
                    val canRecreate = intent.getBooleanExtra("canRecreate", false)
                    if (canRecreate) {
                        selectedSearchOptions["title"] = ""
                        intent.putExtra("canRecreate", false)
                        recreate()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun createProperFurnitureCall(furnitureType: FurnitureType): Call<FurnitureResponse>? {
        val furnitureApiService =
            RetrofitClientInstance.getRetrofitInstance()?.create(FurnitureApiService::class.java)
        when (furnitureType) {
            FurnitureType.CHAIRS -> return furnitureApiService?.getSpecificChairs(
                intent.getIntExtra(
                    "currentPage",
                    0
                ), ChairsSearchOptions(selectedSearchOptions)
            )
            else -> throw IllegalArgumentException("Furniture of type $furnitureType does not exist")
        }
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