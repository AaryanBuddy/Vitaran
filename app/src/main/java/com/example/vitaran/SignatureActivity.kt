package com.example.vitaran

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.File
import java.io.FileOutputStream

class SignatureActivity : AppCompatActivity() {

    var name: TextView?=null
    var remark: EditText?=null
    var clear: Button?=null
    var proceed: Button?=null
    var sign: SignaturePad?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signature)
        name=findViewById(R.id.Name)
        remark=findViewById(R.id.remark)
        clear=findViewById(R.id.clearButton)
        proceed=findViewById(R.id.proceedButton)
        sign=findViewById(R.id.SignaturePad)

        proceed?.isEnabled = false

        setClickListener()
        view()

        sign?.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //nothing
            }

            override fun onSigned() {
                proceed?.isEnabled = true
            }

            override fun onClear() {
                proceed?.isEnabled = false
            }
        })

    }

    fun view(){

        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val saveFullName = sharedPreferences.getString("FullName", null)

        name?.text=saveFullName

    }

    fun setClickListener(){
        clear?.setOnClickListener {
            sign?.clear()
        }
        proceed?.setOnClickListener{

            val remarkStr = remark?.text.toString()
            val nameStr = name?.text.toString()
            val signatureBitmap = sign?.signatureBitmap

            val fileName = "signature.png"
            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            signatureBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val resultIntent = Intent()
            resultIntent.putExtra("remark_key", remarkStr)
            resultIntent.putExtra("name_key", nameStr)
            resultIntent.putExtra("signature_key", file.absolutePath)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

}