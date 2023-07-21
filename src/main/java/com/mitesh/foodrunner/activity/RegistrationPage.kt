package com.mitesh.foodrunner.activity

import android.app.DownloadManager
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

class RegistrationPage : AppCompatActivity(), View.OnClickListener {
    lateinit var et_name_reg: EditText
    lateinit var et_email_reg: EditText
    lateinit var et_mobile_reg: EditText
    lateinit var et_adress_reg: EditText
    lateinit var et_password_reg: EditText
    lateinit var et_confpassword_reg: EditText
    lateinit var bt_register_reg: Button
    lateinit var spReg: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.title = "Register Yourself"
        super.onCreate(savedInstanceState)
        spReg = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_registration_page)

        et_name_reg = findViewById(R.id.et_name_reg)
        et_email_reg = findViewById(R.id.et_email_reg)
        et_mobile_reg = findViewById(R.id.et_mobile_reg)
        et_adress_reg = findViewById(R.id.et_adress_reg)
        et_password_reg = findViewById(R.id.et_password_reg)
        et_confpassword_reg = findViewById(R.id.et_confpassword_reg)
        bt_register_reg = findViewById(R.id.bt_register_reg)
        bt_register_reg.setOnClickListener(this@RegistrationPage)
    }

    override fun onClick(p0: View?) {
        if (et_confpassword_reg.text.toString() == et_password_reg.text.toString()) {

            val cm = ConnectionManager()
            val isConnected = cm.checkConnectivity(this)
            if(isConnected) {

                val request_queue = Volley.newRequestQueue(this@RegistrationPage)
                val url = "http://13.235.250.119/v2/register/fetch_result/"
                val jsonParams = JSONObject()
                jsonParams.put("name", et_name_reg.text.toString())
                jsonParams.put("mobile_number", et_mobile_reg.text.toString())
                jsonParams.put("password", et_password_reg.text.toString())
                jsonParams.put("address", et_adress_reg.text.toString())
                jsonParams.put("email", et_email_reg.text.toString())


                val jsonrequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if (success) {
                                val data = data1.getJSONObject("data")
                                val user_id = data.getString("user_id")
                                val name = data.getString("name")
                                val email = data.getString("email")
                                val address = data.getString("address")
                                val mobile_number = data.getString("mobile_number")

                                spReg.edit().putString("user_id", user_id).apply()
                                spReg.edit().putString("Name", name).apply()
                                spReg.edit().putString("Email", email).apply()
                                spReg.edit().putString("Mobile", mobile_number).apply()
                                spReg.edit().putString("Address", address).apply()

                                Toast.makeText(
                                    this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@RegistrationPage, AppActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                val error_msg = data1.getString("errorMessage")
                                Toast.makeText(this, error_msg, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this,
                                "Some error occured",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    },
                        Response.ErrorListener {
                            //error listner
                            Toast.makeText(
                                this@RegistrationPage,
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
            }else{
                Toast.makeText(this,"Pleas connect to Internet and try again",Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Please enter same Password in both field", Toast.LENGTH_LONG)
                .show()
        }

    }
}