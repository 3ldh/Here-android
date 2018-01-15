package com.example.mathieu.here

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_find_friend.*

class FindFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)
        val appLinkIntent: Intent? = intent
        val appLinkAction: String? = appLinkIntent?.action
        val appLinkData: Uri? = appLinkIntent?.data
        findFriendSessionID.setText(appLinkData?.lastPathSegment)
    }

    fun findFriend(view: View)
    {

    }
}
