package com.mcucsie.flippedclass;

import static com.mcucsie.flippedclass.CommonUtilities.SENDER_ID;
import static com.mcucsie.flippedclass.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.mcucsie.flippedclass.R;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	
    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", MainActivity.name);
        ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        
        if(message==null)
        {
            message="首次登入，成功註冊GCM";
        	displayMessage(context, message);
        	// notifies user
        	generateNotification(context, message);
        }
        else if(message.contains("TAG1"))
        {
        	displayMessage(context, message);
            // notifies user
        	Log.i("<==GCMIntentService==>", "進入含有TAG1的Function，點名推播");
            generateNotification_attend(context, message);
        }
        else if(message.contains("TAG2")){
        	displayMessage(context, message);
            // notifies user
        	Log.i("<==GCMIntentService==>", "進入含有TAG2的Function，分組推播");
            generateNotification_groupchange(context, message);
        }
        else if(message.contains("TAG3")){
        	displayMessage(context, message);
            // notifies user
        	Log.i("<==GCMIntentService==>", "進入含有TAG3的Function，提醒推播");
            generateNotification_remind(context, message);
        }      
        else if(message.contains("TAG5")){
        	displayMessage(context, message);
            // notifies user
        	Log.i("<==GCMIntentService==>", "進入含有TAG5的Function，討論推播");
            generateNotification_discuss(context, message);
        }
        else{
        	displayMessage(context, message);
        	// notifies user
        	generateNotification(context, message);
        }
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        
        
        GetNowAccountInfo gnai=new GetNowAccountInfo(context);
        Bundle bundle=new Bundle();
        bundle.putString("Account", gnai.getNowAccountID());
        bundle.putString("Password", gnai.getNowAccountPassword());
        bundle.putInt("LauncherTag",1);
        bundle.putInt("GCM_TAG", 1);
        bundle.putString("RemindTag", "RemindStu");
        if(gnai.getNowAccountType().equals("student"))
         bundle.putInt("Type", 1);
        else
         bundle.putInt("Type", 0);
        
        notificationIntent.putExtras(bundle);
        
        Log.d("=====>", "msg = "+message);
        notificationManager.notify(0, notification);      
    }
    
    private void generateNotification_attend(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        
        GetNowAccountInfo gnai=new GetNowAccountInfo(context);
        Bundle bundle=new Bundle();
        bundle.putString("Account", gnai.getNowAccountID());
        bundle.putString("Password", gnai.getNowAccountPassword());
        bundle.putInt("LauncherTag",1);
       //bundle.putInt("GCM_TAG", 1);
        bundle.putString("AttendenceTag", "attend_now");
        bundle.putString("GroupTag", "");
        if(gnai.getNowAccountType().equals("student"))
         bundle.putInt("Type", 1);
        else
         bundle.putInt("Type", 0);
        
        notificationIntent.putExtras(bundle);
        
        
//        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        
        Log.d("=====>", "msg = "+message);
        notificationManager.notify(0, notification);      

    }
    
    private void generateNotification_groupchange(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        
        GetNowAccountInfo gnai=new GetNowAccountInfo(context);
        Bundle bundle=new Bundle();
        bundle.putString("Account", gnai.getNowAccountID());
        bundle.putString("Password", gnai.getNowAccountPassword());
        bundle.putInt("LauncherTag",1);
       // bundle.putInt("GCM_TAG", 1);
        bundle.putString("AttendenceTag", "");
        bundle.putString("GroupTag", "group_change");
        if(gnai.getNowAccountType().equals("student"))
         bundle.putInt("Type", 1);
        else
         bundle.putInt("Type", 0);
        
        notificationIntent.putExtras(bundle);
        
        
//        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        
        Log.d("=====>", "msg = "+message);
        notificationManager.notify(0, notification);      

    }
    
    private void generateNotification_remind(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        
        GetNowAccountInfo gnai=new GetNowAccountInfo(context);
        Bundle bundle=new Bundle();
        bundle.putString("Account", gnai.getNowAccountID());
        bundle.putString("Password", gnai.getNowAccountPassword());
        bundle.putInt("LauncherTag",1);
        bundle.putInt("GCM_TAG", 1);
        bundle.putString("RemindTag", "RemindStu");
        if(gnai.getNowAccountType().equals("student"))
         bundle.putInt("Type", 1);
        else
         bundle.putInt("Type", 0);
        
        notificationIntent.putExtras(bundle);
        
        
//        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
              Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        
        Log.d("=====>", "msg = "+message);
        notificationManager.notify(0, notification);      

    }
    
    private void generateNotification_discuss(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        
        GetNowAccountInfo gnai=new GetNowAccountInfo(context);   
        Bundle bundle=new Bundle();
        bundle.putString("Account", gnai.getNowAccountID());
        bundle.putString("Password", gnai.getNowAccountPassword());
        bundle.putInt("LauncherTag",1);
        bundle.putInt("GCM_TAG", 1);
        bundle.putString("DiscussTag", "ask_share");
        bundle.putString("DiscussMessage", message);
        if(gnai.getNowAccountType().equals("student"))
         bundle.putInt("Type", 1);
        else
         bundle.putInt("Type", 0);
        
        notificationIntent.putExtras(bundle);
        
        
//        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
              Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        
        Log.d("=====>", "msg = "+message);
        notificationManager.notify(0, notification);      

    }

}
