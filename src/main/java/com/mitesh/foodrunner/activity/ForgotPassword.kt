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

class ForgotPassword : AppCompatActivity(), View.OnClickListener {
    lateinit var et_mobile_forgot: EditText
    lateinit var et_email_forgot: EditText
    lateinit var bt_next_forgot: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_forgot_password)
        et_mobile_forgot = findViewById(R.id.et_mobile_forgot)
        et_email_forgot = findViewById(R.id.et_email_forgot)
        bt_next_forgot = findViewById(R.id.bt_next_forgot)
        bt_next_forgot.setOnClickListener(this@ForgotPassword)
    }

    override fun onClick(p0: View?) {
        val cm = ConnectionManager()
        val isConnected = cm.checkConnectivity(this)
        if (isConnected) {
            sharedPreferences =
                getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
            val request_queue = Volley.newRequestQueue(this@ForgotPassword)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", et_mobile_forgot.text.toString())
            jsonParams.put("email", et_email_forgot.text.toString())

            val jsonrequest =
                object : JsonObjectRequest(
                    Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if (success) {

                                val first_try = data1.getBoolean("first_try")
                                var strMsg="1st step successful"
                                if(!first_try){
                                    strMsg="Use old otp"
                                }
                                sharedPreferences.edit()
                                    .putString("Mobile", et_mobile_forgot.text.toString()).apply()


                                Toast.makeText(
                                    this,
                                    strMsg,
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@ForgotPassword, PasswordResetActivity::class.java)
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
                            this@ForgotPassword,
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
            Toast.makeText(this@ForgotPassword, "Please connect to Internet", Toast.LENGTH_SHORT)
                .show()
        }
    }

}