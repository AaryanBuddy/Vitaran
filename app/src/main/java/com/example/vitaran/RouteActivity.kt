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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

class RouteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    var totalCount: TextView?=null
    var loadingDialog: Dialog?=null
    var routeId: String?=null
    var vehicleNo: TextView?=null

    @SuppressLint("ResourseType")
    private fun showLoading() {
        if (loadingDialog == null){
            loadingDialog = Dialog(this).apply {
                setContentView(R.layout.dialog_loading)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
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
        setContentView(R.layout.activity_route)
        vehicleNo=findViewById(R.id.VehicleHeading)
        totalCount=findViewById(R.id.RouteTotalText)
        recyclerView = findViewById(R.id.RecyclerView3)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        routeId = intent.getStringExtra("routeId")

        showData()

    }

    fun showData(){
        showLoading()

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("Username", null)
        val savedToken = sharedPreferences.getString("Token", null)

        var routeRequest = RouteRequest(
            username = savedUsername.toString(),
            route_Id = routeId.toString()
        )

        var api = RetrofitInstance
            .getAuthRetrofit(savedToken.toString(), savedUsername.toString())
            .create(apiInterface::class.java)

        var call = api.routes(routeRequest)

        call.enqueue(object : Callback<RoutesResponse>{
            override fun onResponse(call: Call<RoutesResponse>, response: Response<RoutesResponse>) {
                if (response.isSuccessful && response.body() != null){
                    hideLoading()
                    val responseData = response.body()!!
                    val count = responseData.data?.size
                    val vNo = responseData.data.firstOrNull()?.Vehicle_Number
                    if (count != null){
                        vehicleNo?.text = "Vehicle No: $vNo"
                        totalCount?.text = "Total Count: $count"
                        responseData.data?.let {
                            val adapter = RoutesAdapter(it)
                            recyclerView.adapter = adapter
                        }
                    }
                    else{
                        totalCount?.text="Total Count: 0"
                    }
                }
                else{
                    hideLoading()
                    Toast.makeText(applicationContext, "API not hit: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error body: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<RoutesResponse>, t: Throwable) {
                hideLoading()
                Toast.makeText(this@RouteActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}