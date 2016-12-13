package com.mcucsie.flippedclass.attendence;

import com.mcucsie.flippedclass.MainActivity;
import com.mcucsie.flippedclass.R;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;

public class NotificationTest {
	Context context;
	
	public NotificationTest(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	public void sendNotification(String message) {

        try {

             // -- 新的寫法
            @SuppressWarnings("static-access")
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE );
            Builder builder = new Builder(context);
            Intent targetIntent=new Intent();
            Bundle bundle = new Bundle();
			bundle.putString("Account","00360123");
			bundle.putString("AttendenceTag", "attend_now");
            targetIntent.setClass(context, MainActivity.class);
            targetIntent.putExtras(bundle);
            PendingIntent contentIndent = PendingIntent.getActivity(
                       context, 0, targetIntent,
                       PendingIntent. FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIndent)
                       .setSmallIcon(R.drawable. ic_launcher)
                        // 設置狀態列裡面的圖示（小圖示）　　　　　　　　　　　
                       .setLargeIcon(
                                 BitmapFactory. decodeResource(context.getResources(),
                                            R.drawable.ic_launcher)) // 下拉下拉清單裡面的圖示（大圖示）
                       .setTicker(message) // 設置狀態列的顯示的資訊
                      .setWhen(System. currentTimeMillis())// 設置時間發生時間
                       .setAutoCancel( false) // 設置可以清除
                       .setContentTitle( "翻轉教室APP-點名推播通知") // 設置下拉清單裡的標題
                       .setContentText(message); // 設置上下文內容

            @SuppressWarnings("deprecation")
			Notification notification = builder.getNotification();

             // notification.defaults |= Notification.DEFAULT_SOUND;
            /*
            notification. sound = Uri
                      . parse("file:///sdcard/Notifications/hangout_ringtone.m4a");
            notification. sound = Uri. withAppendedPath(
                       Audio.Media. INTERNAL_CONTENT_URI, "6");
            notification. sound = Uri.parse("android.resource://"
                       + getPackageName() + "/" + R.raw.koko);*/
//            notification.sound=Uri.parse(Notification.DEFAULT_SOUND);
            notification. sound = Uri. withAppendedPath(
                    Audio.Media. INTERNAL_CONTENT_URI, "6");
         
             // 後面的設定會蓋掉前面的

             // 振動
            notification. defaults |= Notification.DEFAULT_VIBRATE ; // 某些手機不支援 請加
                                                                                        // try catch 

             // 閃光燈
            notification. defaults |= Notification.DEFAULT_LIGHTS; // 測試沒反應


             // 加i是為了顯示多條Notification
            notificationManager.notify(1, notification);
//             i++;
             // --
       } catch (Exception e) {

       }
 }
}
