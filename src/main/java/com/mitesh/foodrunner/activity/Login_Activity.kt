package com.mitesh.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Login_Activity : AppCompatActivity(), View.OnClickListener {
    lateinit var et_email_login: EditText
    lateinit var et_password_login: EditText
    lateinit var bt_login_login: Button
    lateinit var tv_signup_login: TextView
    lateinit var tv_forgot_login: TextView
    lateinit var sharedPreferences: SharedPreferences
    var regDetails: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val reg_intent = Intent(this@Login_Activity, AppActivity::class.java)
            startActivity(reg_intent)
            finish()
        }
        et_email_login = findViewById(R.id.et_email_login)
        et_password_login = findViewById(R.id.et_password_login)
        bt_login_login = findViewById(R.id.bt_login_login)
        tv_signup_login = findViewById(R.id.tv_signup_login)
        tv_forgot_login = findViewById(R.id.tv_forgot_login)
        bt_login_login.setOnClickListener(this@Login_Activity)
        tv_signup_login.setOnClickListener(this@Login_Activity)
        tv_forgot_login.setOnClickListener(this@Login_Activity)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_login_login -> {
                val cm = ConnectionManager()
                val isConnected = cm.checkConnectivity(this)
                if(isConnected) {
                    val request_queue = Volley.newRequestQueue(this@Login_Activity)
                    val url = "http://13.235.250.119/v2/login/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", et_email_login.text.toString())
                    jsonParams.put("password", et_password_login.text.toString())

                    val jsonrequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams, Response.Listener {
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

                                    sharedPreferences.edit().putString("user_id", user_id).apply()
                                    sharedPreferences.edit().putString("Name", name).apply()
                                    sharedPreferences.edit().putString("Email", email).apply()
                                    sharedPreferences.edit().putString("Mobile", mobile_number).apply()
                                    sharedPreferences.edit().putString("Address", address).apply()
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                    Toast.makeText(
                                        this,
                                        "Log in Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@Login_Activity, AppActivity::class.java)
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
                                    this@Login_Activity,
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
                    Toast.makeText(this,"Please connect to Internet and try again",Toast.LENGTH_SHORT).show()
                }




            }
            R.id.tv_forgot_login -> {
                tv_forgot_login.setTextColor(Color.GREEN)
                val reg_intent = Intent(this@Login_Activity, ForgotPassword::class.java)
                startActivity(reg_intent)
            }
            R.id.tv_signup_login -> {
                tv_signup_login.setTextColor(Color.GREEN)
                val reg_intent = Intent(this@Login_Activity, RegistrationPage::class.java)
                startActivity(reg_intent)

            }
            else -> {
                Toast.makeText(
                    this@Login_Activity,
                    "Something went wrong in app",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    fun savePreference() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }
}
/*
            spReg.edit().putString("Name", et_name_reg.text.toString()).apply()
            spReg.edit().putString("Email", et_email_reg.text.toString()).apply()
            spReg.edit().putString("Mobile", et_mobile_reg.text.toString()).apply()
            spReg.edit().putString("Address", et_adress_reg.text.toString()).apply()
            spReg.edit().putString("Password", et_password_reg.text.toString()).apply()
            Toast.makeText(this,"Please enter same Password in both field",Toast.LENGTH_LONG)
 */