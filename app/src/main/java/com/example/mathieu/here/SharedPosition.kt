package com.example.mathieu.here

import java.util.*

/**
 * Created by eldh on 15/01/2018.
 */
data class GpsPosition(val latitude : Double, val longitude : Double)
data class SharedPosition(val _id : String, val gpsPos : GpsPosition)
