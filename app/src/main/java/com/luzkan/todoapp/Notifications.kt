package com.luzkan.todoapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.luzkan.todoapp.todo.TodoActivity

internal class Notifications : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val todoName = intent?.getSerializableExtra("title") as String?
        val todoDesc = intent?.getSerializableExtra("desc") as String?
        val todoPrior = intent?.getSerializableExtra("prior") as Int?

        val channel = NotificationChannel("TodoChanID", "TodoChanID", NotificationManager.IMPORTANCE_DEFAULT)
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        var notifIcon = R.drawable.low_priority
        if(todoPrior == 2) notifIcon = R.drawable.medium_priority
        else if (todoPrior == 3) notifIcon = R.drawable.high_priority

        val builder = Notification.Builder(context, "TodoChanID")
            .setContentTitle("Todo: $todoName")
            .setContentText("$todoDesc")
            .setAutoCancel(true)
            .setSmallIcon(notifIcon)

        val int = Intent(context, TodoActivity::class.java)
        val pending = PendingIntent.getActivity(context, 0, int, 0)
        builder.setContentIntent(pending)

        manager.notify(1, builder.build())
    }
}