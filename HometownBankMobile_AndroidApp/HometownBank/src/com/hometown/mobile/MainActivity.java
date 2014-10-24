package com.hometown.mobile;

import com.hometown.mobile.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void accountSignOnButton(View v){
		Uri uri = Uri.parse("https://secure-hometowncoop.com/Common/SignOn/Start.asp");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	public void branchLocationsButton(View v){
		startActivity(new Intent(this, BranchesActivity.class));
	}
	
	public void depositRatesButton(View v){
		Uri uri = Uri.parse("https://hometowncoop.com/rates");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	public void mortgageRatesButton(View v){
		Uri uri = Uri.parse("https://hometowncoop1.mortgagewebcenter.com/Default.asp?bhcp=1");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	public void contactUsButton(View v){
		startActivity(new Intent(this, ContactUsActivity.class));
	}
	
	public void fullWebsiteText(View v){
		Uri uri = Uri.parse("http://m.hometowncoop.com/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
