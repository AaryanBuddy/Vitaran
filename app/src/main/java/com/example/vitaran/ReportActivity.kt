package com.example.vitaran

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Dialog
import android.content.Intent
import android.media.tv.SectionResponse
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

class ReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    var totalCount: TextView?=null
    var loadingDialog: Dialog?=null

    @SuppressLint("ResourceType")
    private fun showLoading() {
        if (loadingDialog == null){
            loadingDialog = Dialog(this).apply {
                setContentView(R.layout.dialog_loading)
                setCancelable(false)
            }
        }
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        totalCount=findViewById(R.id.TextTotalCount)
        recyclerView=findViewById(R.id.RecyclerView2)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        showData()

    }

    fun showData(){

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("Username", null)
        val savedToken = sharedPreferences.getString("Token", null)

        var reportRequest = ReportRequest(
            UserName = savedUsername.toString()
        )

        val api = RetrofitInstance
            .getAuthRetrofit(savedToken.toString(), savedUsername.toString())
            .create(apiInterface::class.java)

        var call = api.route(reportRequest)

        call.enqueue(object : Callback<RouteResponse>{
            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful && response.body() != null){
                    val responseData = response.body()!!
                    val count = responseData.data?.size
                    if(count != null) {
                        totalCount?.text = "Total Count: $count"
                        responseData.data?.let {
                            val adapter =  RouteAdapter(it)
                            recyclerView.adapter = adapter
                            Toast.makeText(applicationContext, "API is hit: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        totalCount?.text = "Total Count: 0"
                    }
                }
                else{
                    Toast.makeText(applicationContext, "API not hit: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                Toast.makeText(this@ReportActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}