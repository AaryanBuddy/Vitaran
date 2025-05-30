package com.example.vitaran

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.BitmapCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import java.io.File
import android.Manifest
import android.graphics.Bitmap
import kotlin.jvm.java


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
    private var signatureBase64: String? = null
    private var signatureFileName: String? = null
    var verifyStatus: String? = null
    var addtionalInfo: String? = null
    var cameraIcon: ImageView? = null
    var infoIcon: ImageView? = null
    var camImage: ImageView? = null
    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private var cameraImageBase64: String? = null


//  Launcher for getting the Data from Signature Activity

    private val signatureActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val name = data?.getStringExtra("name_key")
            val remark = data?.getStringExtra("remark_key")
            val signaturePath = data?.getStringExtra("signature_key")
            signatureBase64 = data?.getStringExtra("signature_base64")
            signatureFileName = data?.getStringExtra("signature_filename")

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
            submit?.isEnabled = true
        }
    }

//  Functions for the Loading Screen

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
        submit=findViewById(R.id.submitButton)
        cameraIcon=findViewById(R.id.cameraIcon)
        infoIcon=findViewById(R.id.infoIcon)
        camImage=findViewById(R.id.camImage)

        varifyLayout?.visibility= View.GONE

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }

        cameraIcon?.setOnClickListener {
            openCamera()
        }

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

                verifyStatus = if (selectedRadioButton.text.toString().equals("Verified", ignoreCase = true)) {
                    "V"
                } else {
                    "R"
                }

                val intent = Intent(this, SignatureActivity::class.java)
                signatureActivityLauncher.launch(intent)
            }

            btnChange.setOnClickListener {
                confirmationDialog?.dismiss()
                confirmationDialog = null
            }

            confirmationDialog?.show()
        }

        submit?.isEnabled = false

        submit?.setOnClickListener {
            val verify = verifyStatus
            if (verify == null) {
                Toast.makeText(this, "Please select Verified or Reject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitSignature()
        }

        showData()
    }

    fun showData() {
        showLoading()

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("Username", null)
        val savedToken = sharedPreferences.getString("Token", null)

        val verificationRequest = VerificationRequest(
            DD_Number = dd.toString(),
            Mat_Doc_No = matDocNumber.toString(),
            Reservation_No = resv.toString(),
            Route_No = routeNumber.toString(),
            username = savedUsername.toString()
        )

        val api = RetrofitInstance
            .getAuthRetrofit(savedToken.toString(), savedUsername.toString())
            .create(apiInterface::class.java)

        val call = api.verification(verificationRequest)

        call.enqueue(object : Callback<VerificationResponse> {
            override fun onResponse(call: Call<VerificationResponse>, response: Response<VerificationResponse>) {
                hideLoading()
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!
                    val count = responseData.data?.size
                    if (count != null) {
                        ddNumber?.text = "DD No: $dd"
                        resvNumber?.text = "Resv No: $resv"
                        totalCount?.text = "Total Count: $count"
                        responseData.data?.let { dataList ->
                            val adapter = VerificationAdapter(dataList) { selectedItem ->
                                addtionalInfo = selectedItem.Additional_Info
                            }
                            recyclerView.adapter = adapter
                        }
                    } else {
                        totalCount?.text = "Total Count: 0"
                    }
                } else {
                    Toast.makeText(applicationContext, "API not hit: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                hideLoading()
                Toast.makeText(this@VerificationActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun submitSignature(){
        showLoading()

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("Username", null)
        val savedToken = sharedPreferences.getString("Token", null)

        val imageList = listOf(
            SignatureImages(
                ImageName = signatureFileName ?: "signature.png",
                ImageType = "PNG",
                base64 = signatureBase64.toString()
            )
        )

        val imageList2 = listOf(
            CameraImages(
                ImageName = "${System.currentTimeMillis()}.png",
                ImageType = "PNG",
                base64 = cameraImageBase64.toString()
            )
        )

        val signatureRequest = SignatureRequest(
            AdditionalInfo_guard = addtionalInfo ?: "",
            DD_Number = dd.toString(),
            ImagesList = imageList,
            Lat = "23.456",
            Lon = "78.123",
            Mat_Doc_No = matDocNumber.toString(),
            PhotoList = imageList2,
            Remarks = remarkEditText?.text?.toString() ?: "",
            Reservation_No = resv ?: "",
            Verify = verifyStatus!!,
            VerifybyName = nameTextView?.text?.toString() ?: "",
            routeno = routeNumber.toString(),
            username = savedUsername.toString()
        )

        val api = RetrofitInstance
            .getAuthRetrofit(savedToken.toString(), savedUsername.toString())
            .create(apiInterface::class.java)

        val call = api.signature(signatureRequest)

        call.enqueue(object : Callback<SignatureResponse> {
            override fun onResponse(
                call: Call<SignatureResponse>,
                response: Response<SignatureResponse>
            ) {
                if (response.isSuccessful) {
                    hideLoading()
                    val responseData = response.body()!!
                    Toast.makeText(applicationContext, "Signature submitted successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("SIGNATURE_RESPONSE", "Success: ${responseData.message}")
                    val intent = Intent(this@VerificationActivity, HomescreenActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    hideLoading()
                    Toast.makeText(applicationContext, "Submission failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("SIGNATURE_ERROR", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<SignatureResponse>, t: Throwable) {
                hideLoading()
                Toast.makeText(applicationContext, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("SIGNATURE_FAILURE", "Throwable", t)
            }
        })


    }

    fun openCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val imageFile = File(externalCacheDir, "${System.currentTimeMillis()}.jpg")
        imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                imageUri?.let { uri ->
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    // Convert bitmap to base64
                    val outputStream = java.io.ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val byteArray = outputStream.toByteArray()
                    cameraImageBase64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)


                } ?: run {
                    Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }


}