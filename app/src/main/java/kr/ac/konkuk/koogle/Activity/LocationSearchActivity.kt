package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import kr.ac.konkuk.koogle.Activity.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import kr.ac.konkuk.koogle.Adapter.SearchRecyclerAdapter
import kr.ac.konkuk.koogle.Model.Entity.LocationLatLngEntity
import kr.ac.konkuk.koogle.Model.Entity.SearchResultEntity
import kr.ac.konkuk.koogle.Utility.RetrofitUtil
import kr.ac.konkuk.koogle.databinding.ActivityLocationSearchBinding
import kr.ac.konkuk.locationsearchmapapp.Response.Search.Poi
import kr.ac.konkuk.locationsearchmapapp.Response.Search.Pois
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.math.log

class LocationSearchActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityLocationSearchBinding
    private lateinit var adapter: SearchRecyclerAdapter

    private lateinit var searchResult: SearchResultEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initViews()
        //클릭하는 시점에 api를 호출
        bindViews()
        initData()
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initViews() {
        binding.emptyResultTextView.isVisible = false
        binding.locationRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.locationRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.locationRecyclerView.adapter = adapter
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }
    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchEditText.text.toString())
        }
    }

    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                fullAddress = makeMainAddress(it),
                name = it.name ?: "",
                locationLatLng = LocationLatLngEntity(
//                    lng와 lat의 중심점 위치를 명시
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {
//            Toast.makeText(this, "빌딩이름 : {${it.name} 주소 : ${it.fullAddress}} 위도 : ${it.locationLatLng} ", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(SEARCH_RESULT_EXTRA_KEY, it)
            startActivityForResult(intent, CHOICE_LOCATION_REQUEST_CODE)
//            startActivityForResult(
//                Intent(this, MapActivity::class.java).apply {
//                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
//                }
//            ,CHOICE_LOCATION_REQUEST_CODE
//            )
        }
    }

    private fun searchKeyword(keywordString: String) {
        launch(coroutineContext) {
            try {
                //IO 쓰레드로 변환
                withContext(Dispatchers.IO) {
                    //api 호출
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())
                            body?.let { searchResponseSchema ->
                                setData(searchResponseSchema.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LocationSearchActivity,
                    "검색하는 과정에서 에러가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //문장 보정
    private fun makeMainAddress(poi: Poi): String =
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CHOICE_LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                data.let{
                    if (it != null) {
                        searchResult = it.getParcelableExtra(TEMP)!!
                        Log.i("LocationSearchActivity", "onActivityResult: fullAddress: ${searchResult.fullAddress}")
                        it.putExtra(SEARCH_RESULT_EXTRA_KEY, searchResult)
                    }
                    else {
                        Log.i("LocationSearchActivity", "onActivityResult: 데이터를 가져오지 못함")
                    }
                }
                finish()
            }
        }
        else { }

    }

    companion object {
        const val CHOICE_LOCATION_REQUEST_CODE = 1000
        const val TEMP = "temp"
    }
}