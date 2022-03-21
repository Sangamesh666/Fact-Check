package com.example.factcheck

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


const val RC_SIGN_IN = 123


class LoginActivity() : AppCompatActivity() {

    lateinit var loginBtn : Button
    lateinit var sign_in_button : SignInButton
    lateinit var editText: EditText
    lateinit var ivShowHidePass : ImageView
    lateinit var textView: TextView
    private  var  mIsShowPass = false

    var callbackManager: CallbackManager? = null
    var firebaseAuth: FirebaseAuth? = null
    //Firebase Authentication//
    private lateinit var auth: FirebaseAuth

    constructor(parcel: Parcel) : this() {
        mIsShowPass = parcel.readByte() != 0.toByte()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //init ui views //
        loginBtn = findViewById(R.id.loginBtn)
        editText = findViewById(R.id.et_passwordLogin)
        ivShowHidePass = findViewById(R.id.ivShowHidePass)
        textView = findViewById(R.id.txt_register)

        callbackManager = CallbackManager.Factory.create()

        btn_login.setReadPermissions("email")
        btn_login.setOnClickListener {
        }

        printKeyHash()

        phoneBtn.setOnClickListener {
            startActivity(Intent(this,MobileActivity::class.java))
            finish()
        }

        //Firebase Auth//
        auth = FirebaseAuth.getInstance()

        //Register Activity Page to activity Register Page//
        textView.setOnClickListener {

            startActivity(Intent(this,Register::class.java))
            finish()
        }

        //Main Activity Page  to go to Next Activity//
        loginBtn.setOnClickListener {
            doLogin()

        }

        //Google Sign In Button//
        sign_in_button = findViewById<SignInButton>(R.id.sign_in_button)

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        sign_in_button.visibility = View.VISIBLE
        sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            sign_in_button.visibility = View.VISIBLE

        }


        }





    private fun printKeyHash() {
        try{
            val info = packageManager.getPackageInfo("com.example.factcheck", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures)
            {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT))

            }
        }

        catch (e:PackageManager.NameNotFoundException){

        }
        catch (e:NoSuchAlgorithmException){

        }
    }


    //Login Using Firebase Authentication//
    private fun doLogin() {
        if (et_userLogin.text.toString().isEmpty()){
            et_userLogin.error = "Please Enter Name/Email"
            et_userLogin.requestFocus()
            return
        }
        //for wrong email//
        if (!Patterns.EMAIL_ADDRESS.matcher(et_userLogin.text.toString()).matches()){
            et_userLogin.error = "Please Enter Valid User Name"
            et_userLogin.requestFocus()
            return
        }
        //for empty passord//
        if (et_passwordLogin.text.toString().isEmpty()){
            et_passwordLogin.error = "Please Enter Password"
            et_passwordLogin.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(et_userLogin.text.toString(), et_passwordLogin.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUi(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Email Password are not Registered.",
                        Toast.LENGTH_SHORT).show()
                    updateUi(null)
                }
            }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUi(currentUser)
    }

    private fun updateUi(currentUser: FirebaseUser?) {
        if (currentUser !=null)
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, PostActivity::class.java))
                finish()
            }else{
                Toast.makeText(baseContext, "Please Verify The Email.",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(baseContext, "Your Account Is Not Registered",
                    Toast.LENGTH_SHORT).show()
            }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            sign_in_button.visibility = View.VISIBLE        } catch (e: ApiException) {
                startActivity(Intent(this,PostActivity::class.java))
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            sign_in_button.visibility = View.VISIBLE

        }
    }
    }




