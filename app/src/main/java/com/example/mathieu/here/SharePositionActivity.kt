package com.example.mathieu.here

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationListener;
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.util.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_share_position.*
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.R.attr.phoneNumber
import android.preference.PreferenceManager
import android.view.View


class SharePositionActivity : AppCompatActivity(), LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private val database = FirebaseDatabase.getInstance()
    private val link : String = "http://github.com/3ldh/Here-android/FindFriend/"
    private val TAG = "SharePosition"
    private val INTERVAL = (1000 * 10).toLong()
    private val FASTEST_INTERVAL = (1000 * 5).toLong()
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mCurrentLocation: Location
    private lateinit var mLastUpdateTime: String
    private var sessionID = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_position)
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, READ_GPS)
        if (!isGooglePlayServicesAvailable()) {
            finish()
        }
        //TODO check if UUID exist in Firebase BDD, if so regenerate
        sessionIDValue.text = sessionID
        createLocationRequest()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    public override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart fired ..............")
        mGoogleApiClient.connect()
    }

    public override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop fired ..............")
       // mGoogleApiClient.disconnect()
        stopLocationUpdates()
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected)
    }

    public override fun onDestroy() {
        Log.d(TAG, "onDestroy ..............")
        mGoogleApiClient.disconnect()
        super.onDestroy()
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if (ConnectionResult.SUCCESS == status) {
            true
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, 0).show()
            false
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected())
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (isGooglePlayServicesAvailable() && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this)
            Log.d(TAG, "Location update started ..............: ")
        }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString())
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Firing onLocationChanged..............................................")
        mCurrentLocation = location
        mLastUpdateTime = DateFormat.format("dd-mm-yyyy hh:mm:ss a", Date()) as String
        Log.d(TAG, "LastUpdate : $mLastUpdateTime")
        updateUI()
        val sharedPosition = SharedPosition(_id = sessionID, gpsPos = GpsPosition(latitude = mCurrentLocation.latitude, longitude = mCurrentLocation.longitude))
        val myRef = database.getReference(sharedPosition._id)

        myRef.setValue(sharedPosition.gpsPos)
    }

    private fun updateUI() {
        Log.d(TAG, "UI update initiated .............")
        val lat = mCurrentLocation.latitude
        val lng = mCurrentLocation.longitude
        val text = "At Time: " + mLastUpdateTime + "\n" +
                "Latitude: " + lat + "\n" +
                "Longitude: " + lng + "\n" +
                "Accuracy: " + mCurrentLocation.accuracy + "\n" +
                "Provider: " + mCurrentLocation.provider
        Log.d(TAG, text)
        //  tvLocation?.text = text
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        if (mGoogleApiClient.isConnected)
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this)
        Log.d(TAG, "Location update stopped .......................")
    }

    public override fun onResume() {
        super.onResume()
        if (mGoogleApiClient.isConnected) {
            startLocationUpdates()
            Log.d(TAG, "Location update resumed .....................")
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val gosResult: Int = ContextCompat.checkSelfPermission(applicationContext, permission)
        return gosResult == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission : String, permissionID: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            requestPermissions(arrayOf(permission), permissionID)
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(permission), permissionID)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("sessionID", sessionID)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        sessionID = savedInstanceState?.getString("sessionID").toString()
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun sendEmail(view: View) {
        Log.i(TAG, "Send email...")
//        val TO = arrayOf("someone@gmail.com")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        with(emailIntent) {
            putExtra(Intent.EXTRA_SUBJECT, "HereApplication - FindYourFriend - $sessionID")
            putExtra(Intent.EXTRA_TEXT,
                    "Enter the sessionID in the Here application :\n$sessionID\nor click on the following link to launch the application Here and find your friend :\n$link$sessionID")
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            Log.i(TAG, "Finished sending email...")
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSMS(view: View) {
        if (!checkPermission(Manifest.permission.SEND_SMS))
            requestPermission(Manifest.permission.SEND_SMS, SEND_SMS)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:"))
        val message = "Enter the sessionID in the Here application : \n$sessionID\n or click on the following link to launch the application Here and find your friend :\n$link$sessionID"
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }

    companion object {
        /**
         * Id to identity READ_GPS permission request.
         */
        private val READ_GPS = 0
        private val SEND_SMS = 0

    }
}
