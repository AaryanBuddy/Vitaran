package com.example.vitaran

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager


class HomescreenActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    var totalCount: TextView?=null
    var userName: TextView?=null
    var loadingDialog: Dialog?=null

    @SuppressLint("ResourceType")
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
        setContentView(R.layout.activity_homescreen)
        userName=findViewById(R.id.Welcome_message)
        totalCount=findViewById(R.id.TotalCount)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val logoutimage = findViewById<ImageView>(R.id.LogoutImage)
        logoutimage.setOnClickListener{
            performLogout()
        }

        val reportLogo = findViewById<ImageView>(R.id.ReportLogo)
        reportLogo.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        showData()
    }

    private fun performLogout() {
        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    fun showData() {

        showLoading()

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("Username", null)
        val savedToken = sharedPreferences.getString("Token", null)
        val saveFullName = sharedPreferences.getString("FullName", null)

        if (saveFullName == null || savedToken.isNullOrEmpty() || savedUsername.isNullOrEmpty()){
            userName?.text = "Welcome, Guest"
            return
        }
        userName?.text = "Welcome, $saveFullName"

        var reportRequest = ReportRequest(
            UserName = savedUsername
        )

        val api = RetrofitInstance
            .getAuthRetrofit(savedToken, savedUsername)
            .create(apiInterface::class.java)

        var call = api.report(reportRequest)

        call.enqueue(object : Callback<ReportResponseData> {
            override fun onResponse(call: Call<ReportResponseData>, response: Response<ReportResponseData>) {
                if (response.isSuccessful && response.body() != null) {
                    hideLoading()
                    val responseData = response.body()!!

                    val count = responseData.data?.size
                    if(count != null) {
                        totalCount?.text = "Total Count: $count"
                        responseData.data?.let {
                            val adapter =  ReportAdapter(it)
                            recyclerView.adapter = adapter
                        }
                    }
                    else{
                        totalCount?.text = "Total Count: 0"
                    }
                } else {
                    hideLoading()
                    Toast.makeText(applicationContext, "API not hit: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ReportResponseData>, t: Throwable) {
                hideLoading()
                Toast.makeText(this@HomescreenActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}