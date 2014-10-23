/**
 * Author: Trevor Hodde
 * H&F Solutions
 * 
 * HometownCoop Android Application
 * Copyright 2012
 */

package com.HometownAndroid;

import com.HometownAndroid.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	private Button signOnButton;
	private Button locationsButton;
	private Button loanRatesButton;
	private Button mortgageRatesButton;
	private Button contactUsButton;
	private ImageButton facebookButton;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.findAllViewsById();
        this.changeButtonColors();
                
        signOnButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {        		
        		String url = checkURL("https://secure-hometowncoop.com/Common/SignOn/Start.asp");
        		
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		startActivity(browserIntent);
        	}
        });
        
        locationsButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		String url = checkURL("m.hometowncoop.com/locations.html");
        		
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		startActivity(browserIntent);
        	}
        }); 
        
        loanRatesButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		String url = checkURL("m.hometowncoop.com/mobilerates.htm");
        		
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		startActivity(browserIntent);
        	}
        }); 
        
        mortgageRatesButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		String url = checkURL("http://hometowncoop1.mortgagewebcenter.com/PowerSite/CheckRates.aspx/Index/16343");
        		
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		startActivity(browserIntent);
        	}
        }); 
        
        contactUsButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		String url = checkURL("m.hometowncoop.com/contactus.html");
        		
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		startActivity(browserIntent);
        	}
        });
        
        facebookButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		String url = checkURL("http://www.facebook.com/pages/Hometown-Bank/116047599437");
        		
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        		startActivity(browserIntent);
        	}
        });
    }
    
    private void findAllViewsById() {
    	signOnButton = (Button) findViewById(R.id.signOnButtonId);
    	locationsButton = (Button) findViewById(R.id.locationsButtonId);
    	loanRatesButton = (Button) findViewById(R.id.loanRatesButtonId);
    	mortgageRatesButton = (Button) findViewById(R.id.mortgageRatesButtonId);
    	contactUsButton = (Button) findViewById(R.id.contactUsButtonId);
    	
    	facebookButton = (ImageButton) findViewById(R.id.facebook_logo_image);
    } 
    
    private void changeButtonColors() {
        signOnButton.getBackground().setColorFilter(0xFF034c38, PorterDuff.Mode.MULTIPLY);
        locationsButton.getBackground().setColorFilter(0xFF034c38, PorterDuff.Mode.MULTIPLY);
        loanRatesButton.getBackground().setColorFilter(0xFF034c38, PorterDuff.Mode.MULTIPLY);
        mortgageRatesButton.getBackground().setColorFilter(0xFF034c38, PorterDuff.Mode.MULTIPLY);
        contactUsButton.getBackground().setColorFilter(0xFF034c38, PorterDuff.Mode.MULTIPLY);
    }
    
    private String checkURL(String url) {
    	if (!url.startsWith("http://") && !url.startsWith("https://"))
			   url = "http://" + url;
    	
    	return url;
    }
}