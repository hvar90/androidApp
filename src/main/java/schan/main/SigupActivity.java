package schan.main;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import schan.main.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class SigupActivity extends ActionBarActivity {
	
    ProgressDialog progressDia;
    AtomicInteger msgId = new AtomicInteger();
	GoogleCloudMessaging gcm =DemoActivity.getGcm();
	String SENDER_ID = "11178319894";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setIcon(R.drawable.ic_launcher);		
		setContentView(R.layout.activity_sigup);
		progressDia = new ProgressDialog(this);
	}
	public final static boolean isValidEmail(CharSequence target) {
		  return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}
	
	 @Override
	 protected void onPause() {
	     super.onPause();
	     if (progressDia != null) {
	    	 progressDia.dismiss();
	     }
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sigin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void Register(final View view) {
		
	    EditText editText = (EditText) findViewById(R.id.registEmail);
 		String email = editText.getText().toString();
 		
 		if (isValidEmail(email)) {
 			new AsyncTask<Void, Integer, Void>() { 		          
 				@Override
				protected void onPreExecute() {
 					progressDia.setMessage(getResources().getString(R.string.loading));
 					progressDia.setCancelable(false);
 					progressDia.setCanceledOnTouchOutside(false);
	             	progressDia.show();    
	             	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 	             }
 	        	
 	        	@Override
 	            protected Void doInBackground(Void... params) {
 	                String msg = "";
 	           
 	                try {
 	            
 	                    Bundle data = new Bundle();
 	               
 	                    EditText editText = (EditText) findViewById(R.id.regisUsername);
 	        
 	            		String username = editText.getText().toString();
 	            	
 	            		editText = (EditText) findViewById(R.id.regisPass);
 	            	
 	            		String passw = editText.getText().toString();
 	            	
 	            		editText = (EditText) findViewById(R.id.registEmail);
 	            		
 	            		String email = editText.getText().toString();
 	            	
 	                    data.putString("username", username);
 	                    data.putString("passw", passw);
 	                    data.putString("email", email);
 	                    data.putString("command", "register");
 	                  
 	                    String id = Integer.toString(msgId.incrementAndGet());
 	                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
 	                    msg = "Sent message"; 
 	                   
 	                    while (progressDia.isShowing())
 			           {
 			              
 			           }
 	                    
 	                } catch (IOException ex) {
 	                    msg = "Error :" + ex.getMessage();
 	                } 
 					return null;                   
 	            }
 	        	@Override
 	        	protected void onProgressUpdate(Integer... progress) {
 	                 //setProgress(progress[0]);
 	                 
 	             }
 	            
 				@Override
 				protected void onPostExecute(Void v) {
 					super.onPostExecute(v);
 					progressDia.dismiss();
 	            }
 	        }.execute(null, null, null);		
		}else{
			Intent myIntent = new Intent(this,Alert.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	 		myIntent.putExtra("title", getResources().getString(R.string.notification));
			myIntent.putExtra("message", getResources().getString(R.string.invalid_email)); 	
		    startActivity(myIntent);
		}
      
    }
}
