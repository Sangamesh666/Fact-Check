package com.example.factcheck

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import java.security.Policy
import java.util.regex.Pattern




class Register : AppCompatActivity() {

    lateinit var ivShowHidePass : ImageView
    private  var  mIsShowPass = false


    private lateinit var auth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //ui views //
        var btn_Register = findViewById<Button>(R.id.btn_Register)
        var et_RegisterName = findViewById<EditText>(R.id.et_RegisterName)
        var et_RegisterPass = findViewById<EditText>(R.id.et_RegisterPass)

        auth = FirebaseAuth.getInstance()

        btn_Register.setOnClickListener {
            Toast.makeText(this, "Registered Succesfull", Toast.LENGTH_SHORT).show()
            registerUser()

        }


        }
    fun registerUser(){
        if (et_RegisterName.text.toString().isEmpty()){
            et_RegisterName.error = "Please Enter Email"
            et_RegisterName.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(et_RegisterName.text.toString()).matches()){
            et_RegisterName.error = "Please Enter Valid Email"
            et_RegisterName.requestFocus()
            return
        }
        if (et_RegisterPass.text.toString().isEmpty()){
            et_RegisterPass.error = "Please Enter Password"
            et_RegisterPass.requestFocus()
            return
        }
        auth.createUserWithEmailAndPassword(et_RegisterName.text.toString(), et_RegisterPass.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                    // Sign in success, update UI with the signed-in user's information

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


    }



}


//After creating new account it will go back to the login page//
//After login is successfull, it will go to the Main Activity//



