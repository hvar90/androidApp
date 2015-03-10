package schan.main;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import schan.main.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


public class LoginActivity extends ActionBarActivity {
	
	 String SENDER_ID = "11178319894";	  
	 AtomicInteger msgId = new AtomicInteger();
	 GoogleCloudMessaging gcm =DemoActivity.getGcm();
	
	 ProgressDialog progressDia;
	
	 
	
	  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);        
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setIcon(R.drawable.ic_launcher);
		setContentView(R.layout.activity_login);
		
		progressDia = new ProgressDialog(this);
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
		getMenuInflater().inflate(R.menu.login, menu);
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
	
	public void onLogin(final View view) {		  
	    	
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
                        EditText editText = (EditText) findViewById(R.id.loginUserName);
                		String username = editText.getText().toString();
                		editText = (EditText) findViewById(R.id.loginPass);
                		String passw = editText.getText().toString();
                        data.putString("username", username);
                        data.putString("passw", passw);
                        data.putString("command", "check_login");
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
	    }
}
