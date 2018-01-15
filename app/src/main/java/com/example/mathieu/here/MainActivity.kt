package com.example.mathieu.here

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.graphics.Color
import android.view.MotionEvent
import android.view.View.OnTouchListener



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun findFriend(view : View) {
        println("findFriend")
        val intent = Intent(this, FindFriendActivity::class.java)
        startActivity(intent)
    }

    fun sharePosition(view : View) {
        println("sharePosition")
        val intent = Intent(this, SharePositionActivity::class.java)
        startActivity(intent)
    }
}
