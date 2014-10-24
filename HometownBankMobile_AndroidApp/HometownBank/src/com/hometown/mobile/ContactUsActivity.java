package com.hometown.mobile;

import com.hometown.mobile.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ContactUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendEmailButton(View v){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String aEmailList[] = { "customerserv@hometowncoop.com" };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
		emailIntent.setType("text/plain");
		startActivity(emailIntent);  
	}
	
	public void contactUsPhone(View v){
		Intent callIntent = new Intent(Intent.ACTION_DIAL);
		TextView txt = (TextView) v;
		callIntent.setData(Uri.parse("tel:"+txt.getText().toString()));
		startActivity(callIntent);
	}
	
}