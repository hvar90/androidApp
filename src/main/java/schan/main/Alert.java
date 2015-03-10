package schan.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Alert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent myIntent = getIntent(); // gets the previously created intent
		String title = myIntent.getStringExtra("title"); // will return "FirstKeyValue"
		String mesage= myIntent.getStringExtra("message");
		new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(mesage)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	finish();
            }
         })       
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
		
	

       
      
	}
	
}
