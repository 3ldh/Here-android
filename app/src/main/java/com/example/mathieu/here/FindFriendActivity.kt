package com.example.mathieu.here

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_find_friend.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class FindFriendActivity : AppCompatActivity() {

    private val TAG = "FiendPosition"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var refSessionID: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)
        val appLinkIntent: Intent? = intent
//        val appLinkAction: String? = appLinkIntent?.action
        val appLinkData: Uri? = appLinkIntent?.data
        findFriendSessionID.setText(appLinkData?.lastPathSegment)
    }

    fun findFriend(view: View) {
        refSessionID = database.getReference(findFriendSessionID.text.toString())
        // Attach a listener to read the data at our posts reference
        refSessionID.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d(TAG, "YOOOOLOOOOOOOOOOOOOOOO .............. $dataSnapshot")
                    val gpsPosition : GpsPosition? = dataSnapshot.getValue<GpsPosition>(GpsPosition::class.java)
                    Log.d(TAG, "Received Position : $gpsPosition")

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }
}
