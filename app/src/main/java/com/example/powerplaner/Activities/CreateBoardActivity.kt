package com.example.powerplaner.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.powerplaner.Models.Board
import com.example.powerplaner.R
import com.example.powerplaner.Utils.Constants
import com.example.powerplaner.firebase.FirestoreClass
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var mSelectedImageFileURI: Uri?=null

    private lateinit var mUserName:String

    private var mBoardImageURL:String=" "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME).toString()
        }

        iv_board_image.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this)
            }else{
            ActivityCompat.requestPermissions(
                    this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
            )
            }
        }

        btn_create.setOnClickListener{
            if(mSelectedImageFileURI!=null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> =ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        var board= Board(
                et_board_name.text.toString(),
                mBoardImageURL,
                mUserName,
                assignedUsersArrayList
        )

        FirestoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis()
                +"."+Constants.getFileExtension(this,mSelectedImageFileURI))

        sRef.putFile(mSelectedImageFileURI!!).addOnSuccessListener {
            taskSnapshot->
            Log.i(
                    "Board Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener{
                uri->
                Log.e("Downloadable Image URL",uri.toString())
                mBoardImageURL=uri.toString()

                createBoard()
            }}.addOnFailureListener {exception->
            Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }

    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_240dp)
            actionBar.title=resources.getString(R.string.create_board_title)
        }
        toolbar_create_board_activity.setNavigationOnClickListener {onBackPressed()
        }

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty()
                    && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"Oops,you just denied the permission for storage. You can allow it from the settings.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK
                && requestCode== Constants.PICK_IMAGE_REQUEST_CODE
                && data!!.data!=null){
            mSelectedImageFileURI= data.data
            try {
                Glide
                        .with(this)
                        .load(mSelectedImageFileURI)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(iv_board_image)
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }
}