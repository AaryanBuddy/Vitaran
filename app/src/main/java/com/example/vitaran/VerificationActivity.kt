package com.example.vitaran

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BitmapCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import java.io.File

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
    var nameTextView: TextView? = null
    var remarkEditText: TextView? = null
    var signImage: ImageView? = null
    var varifyLayout: RelativeLayout?= null
    var btnYes: Button? = null
    private var confirmationDialog: AlertDialog? = null


    private val signatureActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val name = data?.getStringExtra("name_key")
            val remark = data?.getStringExtra("remark_key")
            val signaturePath = data?.getStringExtra("signature_key")

            nameTextView?.text = "Name: ${name}"
            remarkEditText?.setText("Remark: ${remark}")

            signaturePath?.let {
                val file = File(it)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    signImage?.setImageBitmap(bitmap)
                }
            }
            varifyLayout?.visibility = View.VISIBLE
        }
    }
    

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

    @SuppressLint("MissingInflatedId")
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
        nameTextView=findViewById(R.id.FullName)
        remarkEditText=findViewById(R.id.remarks)
        signImage=findViewById(R.id.imgView)
        varifyLayout=findViewById(R.id.varifyLayout)
        btnYes=findViewById(R.id.btnYes)

        varifyLayout?.visibility= View.GONE

        dd = intent.getStringExtra("ddNo")
        resv = intent.getStringExtra("revNo")
        matDocNumber = intent.getStringExtra("matDocNo")
        routeNumber = intent.getStringExtra("routeNo")


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

            confirmationDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            btnYes.setOnClickListener {
                confirmationDialog?.dismiss()
                confirmationDialog = null

                val intent = Intent(this, SignatureActivity::class.java)
                signatureActivityLauncher.launch(intent)
            }

            btnChange.setOnClickListener {
                confirmationDialog?.dismiss()
                confirmationDialog = null
            }

            confirmationDialog?.show()
        }

        showData()
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