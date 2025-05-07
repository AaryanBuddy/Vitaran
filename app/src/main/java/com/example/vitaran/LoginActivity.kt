package com.example.vitaran

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(){
    var userId:TextView?=null
    var userPass:TextView?=null
    var submit:Button?=null
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        userId=findViewById(R.id.loginID)
        userPass=findViewById(R.id.loginPass)
        submit=findViewById(R.id.submitButton)

        submit?.setOnClickListener {
            checkCredential()
        }
    }


    fun checkCredential()
    {
        val username = userId?.text.toString().trim()
        val password = userPass?.text.toString().trim()

        if (userId?.getText().toString().trim().isNullOrEmpty()){
            Toast.makeText(this,"Please Enter User ID", Toast.LENGTH_SHORT).show()
        }

        else if (userPass?.getText().toString().trim().isNullOrEmpty()){
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT).show()
        }

        else{
            getData(username, password)
        }
    }

    fun getData(username: String, password: String){

        showLoading()
        var loginRequest = LoginRequest(
            LogOut = "No",
            Password = password,
            UserName = username,
            Version = "1.0.4"
        )

        val api = RetrofitInstance.getRetrofit().create(apiInterface::class.java)

        var call = api.login(loginRequest)

        call.enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                hideLoading()
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!
                    val token = responseData.data?.Token
                    val username = responseData.data?.UserName
                    val fullname = responseData.data?.FullName

                    val sharedPreferences: SharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("Username", username)
                    editor.putString("Token", token)
                    editor.putString("FullName", fullname)
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    Toast.makeText(applicationContext, "Welcome ${responseData.data.FullName}", Toast.LENGTH_SHORT).show()
                    Log.d("LoginSuccess", "Token: ${responseData.data.Token}")

                    val intent = Intent(this@LoginActivity, HomescreenActivity::class.java)
                  //  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(applicationContext, "Login Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginError", "Response Code: ${response.code()} | Error Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                hideLoading()
                Toast.makeText(this@LoginActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginError", "Error: ${t.message}")
            }

        })

    }

}