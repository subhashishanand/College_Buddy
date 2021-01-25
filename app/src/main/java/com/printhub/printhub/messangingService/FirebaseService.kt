package com.printhub.printhub.messangingService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.printhub.printhub.HomeScreen.MainnewActivity
import com.printhub.printhub.R
import kotlin.random.Random

private const val CHANNEL_ID="my_channel";

class FirebaseService: FirebaseMessagingService() {

    private val db = FirebaseFirestore.getInstance()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.notification!=null){
            Log.d("subhu", message.notification!!.title.toString())
        }

        //Log.d("subhu",message.data["title"].toString()+" "+message.data["message"])

        val intent = Intent(this, MainnewActivity::class.java)
        val notificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        var notification : Notification

        if(message.notification!=null){
            notification= NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(message.notification!!.title)
                    .setContentText(message.notification!!.body)
                    .setSmallIcon(R.drawable.notification)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()
        }else{
            notification= NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(message.data["title"])
                    .setContentText(message.data["message"])
                    .setSmallIcon(R.drawable.notification)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()
        }

        notificationManager.notify(notificationID, notification)
    }

    override fun onNewToken(p0: String) {
        var cityNameSharedPref: SharedPreferences? = null
        var collegeNameSharedPref: SharedPreferences? = null
        var userIdSharedPref: SharedPreferences? = null
        var firebaseUserId: String? = null
        var cityName: String? = null
        var collegeName: String? = null
        userIdSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE)
        collegeNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE)
        cityNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE)
        collegeName = collegeNameSharedPref.getString("collegeName", null)
        cityName = cityNameSharedPref.getString("cityName", null)
        firebaseUserId=userIdSharedPref.getString("userId",null)
        if (collegeName != null && cityName!=null && firebaseUserId!=null) {
            db.collection(collegeName).document(cityName).collection("users").document(firebaseUserId).update("token", p0)
        }
        super.onNewToken(p0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description= "My channel description"
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}