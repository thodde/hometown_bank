package com.hometown.mobile;


import java.util.HashMap;
import java.util.Map;

import com.hometown.mobile.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class BranchesActivity extends FragmentActivity implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, 
        OnInfoWindowClickListener{

     // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

        // Handle to SharedPreferences for this app
    SharedPreferences mPrefs;

    // Handle to a SharedPreferences editor
    SharedPreferences.Editor mEditor;

    /*
     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
     * method handleRequestSuccess of LocationUpdateReceiver.
     *
     */
    boolean mUpdatesRequested = false;

    // Get a Map object
    private GoogleMap mMap;
    
    //HashMap and currentLocation for onInfoWindowClick implementation
    private Map<Marker, Intent> markersMap = new HashMap<Marker, Intent>();
    private Location mCurrentLocation;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);
        
         // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Note that location updates are off until the user turns them on
        mUpdatesRequested = false;

        // Open Shared Preferences
        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Get an editor
        mEditor = mPrefs.edit();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
        setUpMapIfNeeded();
        mMap.setOnInfoWindowClickListener(this);
    	
       
        LatLng mapCenterPoint = new LatLng(42.351059441067235, -71.86307430267334);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mapCenterPoint, (float) 8.5);
        mMap.animateCamera(cameraUpdate);
        
        //Text bellow the map
        //TextView textBellowMap =(TextView) findViewById(R.id.text_bellow_map);
        //textBellowMap.setMovementMethod(new ScrollingMovementMethod());
    }
    
    /*
     * Click MarkerInfoWindows will now open up Google Maps Applicatio= to show directions from current Location
     * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#onInfoWindowClick(com.google.android.gms.maps.model.Marker)
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = markersMap.get(marker);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    /*
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onPause() {

        // Save the current setting for updates
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.commit();

        super.onPause();
    }


    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

        // Otherwise, turn off location updates until requested
        } else {
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }
    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                    break;
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    private void putMarkersOnMap(){
    	//LatLng for the Branches
    	final LatLng ATHOL_BRANCH = new LatLng(42.593841, -72.231677);
    	final LatLng OXFORD_BRANCH = new LatLng(42.116241, -71.859942);
    	final LatLng LANCASTER_BRANCH = new LatLng(42.416747, -71.684626);
    	final LatLng STURBRIDGE_BRANCH = new LatLng(42.097334, -72.072635);
    	final LatLng WEBSTER_BRANCH = new LatLng(42.050815, -71.834792);
    	final LatLng AUBURN_BRANCH = new LatLng(42.190591, -71.846016);
    	final LatLng WEBSTER_CONV_CENTER = new LatLng(42.049149, -71.882981);
        final LatLng LEOMINSTER_BRANCH = new LatLng(42.417115, -71.684628);

    	String uri = "http://maps.google.com/maps?saddr="+
    				mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude();
    
    	//addMarker for Athol branch
    	Marker atholMarker = mMap.addMarker(new MarkerOptions()
    	                           .position(ATHOL_BRANCH)
    	                           .title("Athol Branch")
    	                           .snippet("90 Exchange St, Athol, MA"));
    	markersMap.put(atholMarker, new Intent(android.content.Intent.ACTION_VIEW,
    		    	Uri.parse(uri+"&daddr="+ATHOL_BRANCH.latitude+","+ATHOL_BRANCH.longitude)));

    	//addMarker for Oxford branch
    	Marker oxfordMarker = mMap.addMarker(new MarkerOptions()
						           .position(OXFORD_BRANCH)
						           .title("Oxford Branch")
						           .snippet("31 Sutton Ave, Oxford, MA"));
    	markersMap.put(oxfordMarker, new Intent(android.content.Intent.ACTION_VIEW,
		    	Uri.parse(uri+"&daddr="+OXFORD_BRANCH.latitude+","+OXFORD_BRANCH.longitude)));

    	//addMarker for Lancaster Branch
    	Marker lancasterMarker = mMap.addMarker(new MarkerOptions()
						           .position(LANCASTER_BRANCH)
						           .title("South Lancaster Branch")
						           .snippet("131 Main St, S. Lancaster, MA"));
		markersMap.put(lancasterMarker, new Intent(android.content.Intent.ACTION_VIEW,
		    	Uri.parse(uri+"&daddr="+LANCASTER_BRANCH.latitude+","+LANCASTER_BRANCH.longitude)));
    	
    	
    	//addMarker for Sturbridge Branch
    	Marker sturbridgeMarker = mMap.addMarker(new MarkerOptions()
    								.position(STURBRIDGE_BRANCH)
    								.title("Sturbridge Branch")
    								.snippet("331 Main St, Route 131, Sturbridge, MA"));
		markersMap.put(sturbridgeMarker, new Intent(android.content.Intent.ACTION_VIEW,
		    	Uri.parse(uri+"&daddr="+STURBRIDGE_BRANCH.latitude+","+STURBRIDGE_BRANCH.longitude)));
    	
    	
    	//addMarker for Webster Convenience Center
    	Marker websterConvMarker = mMap.addMarker(new MarkerOptions()
    								.position(WEBSTER_CONV_CENTER)
    								.title("Webster Convenience Center")
    								.snippet("218R Main St, Webster, MA"));
		markersMap.put(websterConvMarker, new Intent(android.content.Intent.ACTION_VIEW,
		    	Uri.parse(uri+"&daddr="+WEBSTER_CONV_CENTER.latitude+","+WEBSTER_CONV_CENTER.longitude)));
    	    	
    	//addMarker for Webster Branch
    	Marker websterMarker = mMap.addMarker(new MarkerOptions()
    								.position(WEBSTER_BRANCH)
    								.title("Webster Branch")
    								.snippet("4 Gore Rd, Webster, MA"));
		markersMap.put(websterMarker, new Intent(android.content.Intent.ACTION_VIEW,
		    	Uri.parse(uri+"&daddr="+WEBSTER_BRANCH.latitude+","+WEBSTER_BRANCH.longitude)));

    	//addMarker for Auburn Branch
		//TODO: get better LatLng Values, this comes up with a different Address
		//the current values are close but not exact.
    	Marker auburnMarker = mMap.addMarker(new MarkerOptions()
    								.position(AUBURN_BRANCH)
    								.title("Auburn Branch")
    								.snippet("569 Southbridge St, Heritage Plaza, Auburn, MA"));
		 markersMap.put(auburnMarker, new Intent(android.content.Intent.ACTION_VIEW,
			    	Uri.parse(uri+"&daddr="+AUBURN_BRANCH.latitude+","+AUBURN_BRANCH.longitude)));

        //addMarker for Leominster Branch
        Marker leominsterMarker = mMap.addMarker(new MarkerOptions()
                .position(LEOMINSTER_BRANCH)
                .title("Leominster Branch")
                .snippet("9 Sack Boulevard, Leominster, MA 01543"));
        markersMap.put(leominsterMarker, new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri+"&daddr="+LEOMINSTER_BRANCH.latitude+","+LEOMINSTER_BRANCH.longitude)));
    }
    
    private void setUpMapIfNeeded() {
    	if (mMap!=null){
    		return;
    	}
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            return;
        }
        
        //This line gives a RunTime error that seams to be a false positive
        mMap.setMyLocationEnabled(true);
    }
    
    public void branchPhoneNumber(View v){
    	Intent callIntent = new Intent(Intent.ACTION_DIAL);
		TextView txt = (TextView) v;
		callIntent.setData(Uri.parse("tel:"+txt.getText().toString()));
		startActivity(callIntent);
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    
    // Calls getLastLocation() to get the current location
    public Location getLocation() {

        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
           return mLocationClient.getLastLocation();
        }else{
        	Log.d("getLocation","LOCATION NULL");
        	return null;
        }
        
    }
    
    /**
     * Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {

        // Report to the UI that the location was updated
        //mConnectionStatus.setText(R.string.location_updated);

        // In the UI, set the latitude and longitude to the value received
        //mLatLng.setText(LocationUtils.getLatLng(this, location));
    }

    /**
     * Invoked by the "Start Updates" button
     * Sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates() {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates() {
        mUpdatesRequested = false;

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
       // mConnectionStatus.setText(R.string.connected);
            startPeriodicUpdates(); 
            mCurrentLocation = mLocationClient.getLastLocation();
            putMarkersOnMap();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    	//TODO: display with toast or dialog
        //mConnectionStatus.setText(R.string.disconnected);
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }
    
    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
       // mConnectionState.setText(R.string.location_updates_stopped);
    }


    
   /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}