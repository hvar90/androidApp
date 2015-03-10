/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schan.main;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import schan.main.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public final static  int NOTIFICATION_ID = 1;
    public static Random r = new Random();
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
               // sendNotification("Send error: " + extras.toString(),"");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
               // sendNotification("Deleted messages on server: " + extras.toString(),"");
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                String type = extras.getString("type");
                if (type.equals("response")) {                    
                	String response = extras.getString("response");
                	if (response.equals("registered")) {
                		
	                	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.socialchan.net/#log_in"));
	                	browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                	
	                	String FILENAME = "user_is_registered";
	                	String string = "yes";
	                	FileOutputStream fos;
						try {
							fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
							fos.write(string.getBytes());
		                	fos.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	                	
	                	
	                	startActivity(browserIntent);
                	 } 
                }else if (type.equals("notification")) {  
                	//JSONObject objJson = stringToJson(extras.getString("args"));                
								
                    try {
						selectNot( extras.getString("message"),extras.getString("args"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}     
                     
                }
                else {
                	
                	String error = extras.getString("error");
                	if (error.equals("not_registered")) {
	                	Intent myIntent = new Intent(this,Alert.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                	String message = getResources().getString(R.string.pass_user_incorr);
	                	String title = getResources().getString(R.string.notification);
	                	myIntent.putExtra("message",message);
	                	myIntent.putExtra("title",title);
	                	startActivity(myIntent);
                	}else if (error.equals("username_repeated")){                	
                		Intent myIntent = new Intent(this,Alert.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                	String message = getResources().getString(R.string.name_repeated);
	                	String title = getResources().getString(R.string.notification);
	                	myIntent.putExtra("message",message);
	                	myIntent.putExtra("title",title);
	                	startActivity(myIntent);
                	   }
                    } 
               
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
    public JSONObject stringToJson( String msg) {
    	JSONObject objJson = null;
    	
    	try {
    		objJson = new  JSONObject(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return objJson;
    }
    
    public  void selectNot( String msg, String arg ) throws JSONException {
    	JSONObject obj=stringToJson(arg);
    	if ("new post".equals(msg )) {     		 
    		  String nameGroup=obj.getString("name_group");   
    		  String slugGroup=obj.getString("slug");    		
    		  newPostNot(msg,nameGroup,slugGroup);  
    		}
    	else if ("new message".equals(msg )) {    		 
    		  String from=obj.getString("from");   
    		  String slugChat=obj.getString("slug");  
    		  newMessageNot(msg,from,slugChat);   
			}
    	else if ("new invitation".equals(msg )) {
    		  String username=obj.getString("username");
    		  String from=obj.getString("from");  
    		  newInvitationNot(msg,username,from);   
			}
    	else if ("accept invitation".equals(msg )) {
    		  String username=obj.getString("username");
    		  String from=obj.getString("from");  
    		  aceptInvitationNot(msg,username,from);   
			}
    	 
    }
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void newMessageNot(String msg, String from,String slugChat) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.socialchan.net/"+slugChat));    	
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		browserIntent, 0);      
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(from)
        .setStyle(new NotificationCompat.BigTextStyle())
        .setAutoCancel(true)        
        .setLights(Color.RED, 3000, 3000)
        .setContentText( getResources().getString(R.string.new_message));
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_NORMAL:
            	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                break;
        } 
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(from,NOTIFICATION_ID, mBuilder.build());       
    }
    
    private void newPostNot(String msg,String nameGroup, String slugGroup) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.socialchan.net/"+slugGroup));    	
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		browserIntent, 0);         
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(nameGroup)
        .setStyle(new NotificationCompat.BigTextStyle())
        .setAutoCancel(true)       
        .setLights(Color.RED, 3000, 3000)
        .setContentText(getResources().getString(R.string.new_post));
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_NORMAL:
            	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                break;
        }
        
  

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(nameGroup,NOTIFICATION_ID, mBuilder.build());
       
    }
    private void newInvitationNot(String msg, String username,String from) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.socialchan.net/user/"+username));    	
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		browserIntent, 0); 
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(from)
        .setStyle(new NotificationCompat.BigTextStyle())
        .setAutoCancel(true)     
        .setLights(Color.RED, 3000, 3000)
        .setContentText(getResources().getString(R.string.new_invitation));
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_NORMAL:
            	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                break;
        } 

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(r.nextInt(10000), mBuilder.build());
       
    }
    private void aceptInvitationNot(String msg, String username,String from) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.socialchan.net/user/"+username));    	
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		browserIntent, 0);     
       
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(from)
        .setStyle(new NotificationCompat.BigTextStyle())
        .setAutoCancel(true)      
        .setLights(Color.RED, 3000, 3000)
        .setContentText( getResources().getString(R.string.accepted_invitation));
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
            	mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                break;
            case AudioManager.RINGER_MODE_NORMAL:
            	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                break;
        }
        
  
       
        mBuilder.setContentIntent(contentIntent);       
        mNotificationManager.notify(r.nextInt(10000), mBuilder.build());
       
    }
}
