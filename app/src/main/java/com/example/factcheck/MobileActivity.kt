package com.example.factcheck

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.factcheck.databinding.ActivityMobileBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber
import java.util.concurrent.TimeUnit

class MobileActivity : AppCompatActivity() {
    //view binding//
    private lateinit var binding: ActivityMobileBinding

    private lateinit var progressDialog: ProgressDialog

    private var forceResendingToken : PhoneAuthProvider.ForceResendingToken? = null
    private var mCallBacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId : String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "MAIN_TAG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.phoneLl.visibility = View.VISIBLE
        binding.codeLl.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait....")
        progressDialog.setCanceledOnTouchOutside(false)

        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential)


            }

            override fun onVerificationFailed(e: FirebaseException) {

                progressDialog.dismiss()
                Toast.makeText(this@MobileActivity, "${e.message}", Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent: $verificationId")

                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                binding.phoneLl.visibility = View.GONE
                binding.codeLl.visibility = View.VISIBLE
                Toast.makeText(this@MobileActivity, "Verification Code Sent....", Toast.LENGTH_SHORT).show()


            }
        }

        binding.phoneContinueBtn.setOnClickListener {

            val phone = binding.phonEt.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this, "Please Enter The Phone Number", Toast.LENGTH_SHORT).show()
            } else{
                startPhoneNumberVerification(phone)
            }

        }
        binding.resendCodeTv.setOnClickListener {
            val phone = binding.phonEt.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this,"Please Enter The Phone Number", Toast.LENGTH_SHORT).show()
            }else{
                resendVerificationCode(phone, forceResendingToken)
            }

        }
        binding.codesubmitBtn.setOnClickListener {
            val code = binding.codeEt.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this,"Please Enter The Verification Code", Toast.LENGTH_SHORT).show()
            }else{
                verifyPhoneNumberWithCode(mVerificationId, code)
            }

        }
    }

    private fun startPhoneNumberVerification(phone: String){

        progressDialog.setMessage("Verifying Phone Number....")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun resendVerificationCode(phone: String, token: ForceResendingToken?){

        progressDialog.setMessage("Resending Code....")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String ){
        progressDialog.setMessage("Verifying The OTP....")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging In....")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this,"Logged In As $phone", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,PostActivity::class.java))

            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this,"${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

}


