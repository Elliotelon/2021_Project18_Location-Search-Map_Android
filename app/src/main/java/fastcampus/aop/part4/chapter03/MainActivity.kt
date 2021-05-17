package fastcampus.aop.part4.chapter03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import fastcampus.aop.part4.chapter03.databinding.ActivityMainBinding
import fastcampus.aop.part4.chapter03.model.LocationLatLngEntity
import fastcampus.aop.part4.chapter03.model.SearchResultEntity
import fastcampus.aop.part4.chapter03.response.search.Poi
import fastcampus.aop.part4.chapter03.response.search.Pois
import fastcampus.aop.part4.chapter03.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()

    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAdress(it) ,
                locationLatLng = LocationLatLngEntity(
                    it.noorLat, it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(this, "빌딩이름 ${it.name} 주소 : ${it.fullAddress} 위도/경도 : ${it.locationLatLng}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchKeyword(keyword: String) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keyword
                    )
                    if(response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("response", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }

}