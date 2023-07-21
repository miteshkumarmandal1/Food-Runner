package com.mitesh.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class PasswordResetActivity : AppCompatActivity(),View.OnClickListener {

    lateinit var et_OTP_reset: EditText
    lateinit var et_newPassword_reset: EditText
    lateinit var et_cnfPassword_reset: EditText
    lateinit var bt_submit_reset: Button
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        et_OTP_reset=findViewById(R.id.et_OTP_reset)
        et_newPassword_reset=findViewById(R.id.et_newPassword_reset)
        et_cnfPassword_reset=findViewById(R.id.et_cnfPassword_reset)
        bt_submit_reset=findViewById(R.id.bt_submit_reset)
        bt_submit_reset.setOnClickListener(this@PasswordResetActivity)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

    }

    override fun onClick(p0: View?) {
        if(et_cnfPassword_reset.text.toString()==et_newPassword_reset.text.toString()) {
            val cm = ConnectionManager()
            val isConnected = cm.checkConnectivity(this)
            if (isConnected) {
                val request_queue = Volley.newRequestQueue(this@PasswordResetActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", sharedPreferences.getString("Mobile", "1234567890"))
                jsonParams.put("password", et_newPassword_reset.text.toString())
                jsonParams.put("otp", et_OTP_reset.text.toString())

                val jsonrequest =
                    object : JsonObjectRequest(
                        Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if (success) {
                                    val successMessage=data1.getString("successMessage")
                                    Toast.makeText(
                                        this,
                                        successMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(
                                        this@PasswordResetActivity,
                                        AppActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()

                                } else {
                                    val error_msg = data1.getString("errorMessage")
                                    Toast.makeText(this, error_msg, Toast.LENGTH_LONG).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this,
                                    "Some error occured: $e",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        },
                        Response.ErrorListener {
                            //error listner
                            Toast.makeText(
                                this@PasswordResetActivity,
                                "Volley error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val myHeaders = HashMap<String, String>()
                            myHeaders["Content-type"] = "application/json"
                            myHeaders["token"] = "1534961f5f0009"
                            return myHeaders
                        }
                    }
                request_queue.add(jsonrequest)

            } else {
                Toast.makeText(
                    this@PasswordResetActivity,
                    "Please connect to Internet",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            Toast.makeText(this@PasswordResetActivity,"Please enter same password",Toast.LENGTH_SHORT).show()
        }
    }
}