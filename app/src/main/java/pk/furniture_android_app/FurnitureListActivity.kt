package pk.furniture_android_app

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pk.furniture_android_app.furniture.FurnitureApiService
import pk.furniture_android_app.furniture.FurnitureRecyclerViewAdapter
import pk.furniture_android_app.models.SortOption
import pk.furniture_android_app.models.chairs.ChairsSearchOptions
import pk.furniture_android_app.models.furniture.Furniture
import pk.furniture_android_app.models.furniture.FurnitureResponse
import pk.furniture_android_app.models.furniture.FurnitureType
import pk.furniture_android_app.shared.SearchOptionsFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FurnitureListActivity : AppCompatActivity() {
    private val adapter by lazy { FurnitureRecyclerViewAdapter() }
    private var furniture: List<Furniture> = emptyList()
    private var selectedSearchOptions: HashMap<String, String?> = HashMap()
    private var searchOptions: Map<String, List<String>> = emptyMap()
    private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_furniture_list)

        val progressBar = findViewById<ProgressBar>(R.id.listProgressBar)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE

        val searchOptionsCall = SearchOptionsFactory(FurnitureType.CHAIRS).getProperApiCall()
        searchOptionsCall.getSearchOptions().enqueue(object : Callback<Map<String, List<String>>> {
            override fun onResponse(
                call: Call<Map<String, List<String>>>, response: Response<Map<String, List<String>>>
            ) {
                val body = response.body()
                if (body != null)
                    searchOptions = body
            }

            override fun onFailure(call: Call<Map<String, List<String>>>, t: Throwable) {
                Toast.makeText(
                    this@FurnitureListActivity, "Could not get search options",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        setupRecyclerView()
        setupSearchView()
        setupFilterButton()

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

    private fun setupFilterButton() {
        val filterButton: Button = findViewById(R.id.filterButton)
        filterButton.setOnClickListener {
            val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val additionalSearchOptionsView =
                inflater.inflate(R.layout.additional_search_options, null)
            prepareAdditionalSearchOptions(additionalSearchOptionsView)
            val alert = AlertDialog.Builder(this)
            alert.setView(additionalSearchOptionsView)
            alert.setPositiveButton("Save") { _, _ ->
                setSelectedSearchOptions(additionalSearchOptionsView)
                intent.putExtra("selectedSearchOptions", selectedSearchOptions)
                intent.putExtra("currentPage", 0)
                recreate()
            }
            alert.show()
        }
    }

    private fun prepareAdditionalSearchOptions(additionalSearchOptionsView: View) {
        val container: LinearLayout =
            additionalSearchOptionsView.findViewById(R.id.additionalSearchOptionsContainer)
        for (searchOption in searchOptions.keys) {
            val textInputLayout = TextInputLayout(
                ContextThemeWrapper(
                    this,
                    R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox_Dense_ExposedDropdownMenu
                )
            )
            textInputLayout.boxBackgroundColor = ContextCompat.getColor(this, R.color.white)
            textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            textInputLayout.hint = searchOption
            val autoCompleteTextView = AutoCompleteTextView(textInputLayout.context)
            autoCompleteTextView.setPaddingRelative(15.dp, 0, 15.dp, 0)
            autoCompleteTextView.id = getSearchViewId("search_option_$searchOption")
            autoCompleteTextView.isEnabled = false
            autoCompleteTextView.inputType = EditorInfo.IME_ACTION_NONE
            autoCompleteTextView.setTextColor(Color.parseColor("black"))
            autoCompleteTextView.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    searchOptions[searchOption]?.plus("") as MutableList
                )
            )
            textInputLayout.addView(autoCompleteTextView)
            container.addView(textInputLayout)
        }
        val sortOption: AutoCompleteTextView =
            additionalSearchOptionsView.findViewById(R.id.sortOption)
        sortOption.setAdapter(
            ArrayAdapter(
                this, android.R.layout.simple_dropdown_item_1line, SortOption.values()
            )
        )
        fillTextOfExistingOptions(additionalSearchOptionsView)
    }

    private fun fillTextOfExistingOptions(additionalSearchOptionsView: View) {
        for (key: String in selectedSearchOptions.keys) {
            if (key == "title") continue
            val text: TextView =
                additionalSearchOptionsView.findViewById(getSearchViewId("search_option_$key"))
            if (text is AutoCompleteTextView) {
                if (selectedSearchOptions[key] == null)
                    text.setText("", false)
                else
                    text.setText(selectedSearchOptions[key].toString(), false)
            } else
                text.text =
                    if (selectedSearchOptions[key] == "0.0") null else selectedSearchOptions[key]
        }
    }

    private fun getSearchViewId(key: String): Int {
        when (key) {
            "search_option_startPrice" -> return R.id.startPriceAlertDialog
            "search_option_endPrice" -> return R.id.endPriceAlertDialog
            "search_option_sortOption" -> return R.id.sortOption
            "search_option_color" -> return R.id.search_option_color
            "search_option_material" -> return R.id.search_option_material
        }
        throw java.lang.IllegalArgumentException("No such search option")
    }

    private fun setSelectedSearchOptions(additionalSearchOptionsView: View) {
        val startPrice: TextInputEditText =
            additionalSearchOptionsView.findViewById(R.id.startPriceAlertDialog)
        val endPrice: TextInputEditText =
            additionalSearchOptionsView.findViewById(R.id.endPriceAlertDialog)
        val sortOption: AutoCompleteTextView =
            additionalSearchOptionsView.findViewById(R.id.sortOption)
        selectedSearchOptions["startPrice"] =
            if (startPrice.text.isNullOrBlank()) "0.0" else startPrice.text.toString()
        selectedSearchOptions["endPrice"] =
            if (endPrice.text.isNullOrBlank()) "0.0" else endPrice.text.toString()
        selectedSearchOptions["sortOption"] =
            if (sortOption.text.isNullOrBlank()) null else sortOption.text.toString()
        for (searchOption in searchOptions.keys) {
            val searchOptionTextBox: AutoCompleteTextView =
                additionalSearchOptionsView.findViewById(getSearchViewId("search_option_$searchOption"))
            selectedSearchOptions[searchOption] =
                if (searchOptionTextBox.text == null) null else searchOptionTextBox.text.toString()
        }

    }

    private fun setupSearchView() {
        val mutableMap =
            intent.getSerializableExtra("selectedSearchOptions")
        if (mutableMap != null)
            this.selectedSearchOptions = mutableMap as HashMap<String, String?>

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