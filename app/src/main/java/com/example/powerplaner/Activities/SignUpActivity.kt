package com.example.powerplaner.Activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.powerplaner.Models.User
import com.example.powerplaner.R
import com.example.powerplaner.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, "You Have " +
                "Successfully Registered",Toast.LENGTH_LONG).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar=supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_240dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener{onBackPressed()}

        btn_sign_up.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser(){
        val name:String=et_name.text.toString().trim{it<=' '}
        val email:String=et_email.text.toString().trim{it<=' '}
        val password:String=et_password.text.toString().trim{it<=' '}

        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            val user= User(firebaseUser.uid,name,registeredEmail)
                            FirestoreClass().registerUser(this,user)
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG)
                                    .show()
                        }
                    }
        }
    }

    private fun validateForm(name:String,email:String,password: String): Boolean {
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter A Name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please Enter A Email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please Enter A Password")
                false
            }else->{
                true
            }
        }
    }

}