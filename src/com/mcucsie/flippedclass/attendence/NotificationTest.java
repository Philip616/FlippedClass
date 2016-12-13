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

             // -- �s���g�k
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
                        // �]�m���A�C�̭����ϥܡ]�p�ϥܡ^�@�@�@�@�@�@�@�@�@�@�@
                       .setLargeIcon(
                                 BitmapFactory. decodeResource(context.getResources(),
                                            R.drawable.ic_launcher)) // �U�ԤU�ԲM��̭����ϥܡ]�j�ϥܡ^
                       .setTicker(message) // �]�m���A�C����ܪ���T
                      .setWhen(System. currentTimeMillis())// �]�m�ɶ��o�ͮɶ�
                       .setAutoCancel( false) // �]�m�i�H�M��
                       .setContentTitle( "½��Ы�APP-�I�W�����q��") // �]�m�U�ԲM��̪����D
                       .setContentText(message); // �]�m�W�U�夺�e

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
         
             // �᭱���]�w�|�\���e����

             // ����
            notification. defaults |= Notification.DEFAULT_VIBRATE ; // �Y�Ǥ�����䴩 �Х[
                                                                                        // try catch 

             // �{���O
            notification. defaults |= Notification.DEFAULT_LIGHTS; // ���ըS����


             // �[i�O���F��ܦh��Notification
            notificationManager.notify(1, notification);
//             i++;
             // --
       } catch (Exception e) {

       }
 }
}
