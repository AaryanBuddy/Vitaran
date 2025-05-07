package com.example.vitaran

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call

class VerificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    var totalCount: TextView? = null
    var loadingDialog: Dialog? = null
    var ddNumber: TextView? = null
    var resvNumber: TextView? = null
    var matDocNumber: String? = null
    var routeNumber: String? = null
    var dd: String? = null
    var resv: String? = null
    var radioGroup: RadioGroup? = null
    var submit: Button? = null
    

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

        setContentView(R.layout.activity_verification_ativity)
        totalCount=findViewById(R.id.TotalCount)
        ddNumber=findViewById(R.id.DD_No)
        dd = intent.getStringExtra("ddNo")
        resvNumber=findViewById(R.id.Resv_No)
        resv = intent.getStringExtra("revNo")
        recyclerView=findViewById(R.id.recyclerView4)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        matDocNumber = intent.getStringExtra("matDocNo")
        routeNumber = intent.getStringExtra("routeNo")
        radioGroup = findViewById(R.id.radioGroup)

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()

            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
            val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
            val message = dialogView.findViewById<TextView>(R.id.dialogMessage)
            val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
            val btnChange = dialogView.findViewById<Button>(R.id.btnChange)

            title.text = "वितरण"
            message.text = "Are you sure you want to select ${selectedText}?"

            val alertDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            btnYes.setOnClickListener {
                alertDialog.dismiss()
                val intent = Intent(this@VerificationActivity, HomescreenActivity::class.java)
                startActivity(intent)
                finish()
            }

            btnChange.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }



        showData()
    }

    fun confirmation(){

        val confirmDialog = AlertDialog.Builder(this)
        confirmDialog.setTitle("वितरण")
        confirmDialog.setMessage("Are you Sure?")
        confirmDialog.setCancelable(false)

        confirmDialog.setPositiveButton("Yes"){_, _ ->
            val intent = Intent(this@VerificationActivity, HomescreenActivity::class.java)
            finish()
        }

        confirmDialog.setNegativeButton("Cancel"){_, _ ->

        }

    }

    fun showData(){
        showLoading()

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("Username", null)
        val savedToken = sharedPreferences.getString("Token", null)

        var verificationRequest = VerificationRequest(
            DD_Number = dd.toString(),
            Mat_Doc_No = matDocNumber.toString(),
            Reservation_No = resv.toString(),
            Route_No = routeNumber.toString(),
            username = savedUsername.toString()
        )

        var api = RetrofitInstance
            .getAuthRetrofit(savedToken.toString(), savedUsername.toString())
            .create(apiInterface::class.java)

        var call = api.verification(verificationRequest)

        call.enqueue(object : Callback<VerificationResponse>{
            override fun onResponse(call: Call<VerificationResponse>, response: Response<VerificationResponse>) {
                if (response.isSuccessful && response.body() != null){
                    hideLoading()
                    val responseData = response.body()!!
                    val count = responseData.data?.size
                    if (count != null){
                        ddNumber?.text = "DD No: $dd"
                        resvNumber?.text = "Resv No: $resv"
                        totalCount?.text = "Total Count: $count"
                        responseData.data?.let {
                            val adapter = VerificationAdapter(it)
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

            override fun onFailure(call: retrofit2.Call<VerificationResponse>, t: Throwable) {
                hideLoading()
                Toast.makeText(this@VerificationActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}