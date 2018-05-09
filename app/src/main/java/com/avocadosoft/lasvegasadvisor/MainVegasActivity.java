package com.avocadosoft.lasvegasadvisor;

import java.io.IOException;
import java.util.List;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainVegasActivity extends ListActivity implements View.OnClickListener
{
	private static SQLiteDatabase db;
	private static EditText tv;
	private static Cursor cursor;
	private Resources resources;
	private ProximityIntentReceiver proximityIntentReceiver;
	private LocationManager locationManager;
	private LocationListener locationListener;
	public static MySQLiteHelper myDbHelper;
	double currentLatitude;
	double currentLongitude;
	private static final String PROX_ALERT_INTENT = "com.avocadosoft.lasvegasadvisor.ProximityIntentReceiver";

	private static Context context;
	private String providerName;
	private static String retrieveDealsByParentLocationQuery;
	private String[][] allLocations;
	private int proximityExpiration;
	private Boolean locationListenerRunning;

	/*
	 * Functions to add
	 * 
	 * "Find me this..." SETUP: Split screen. Bottom half show map, top half
	 * show text with info, address, phone number, etc. (walking distance, cab
	 * distance) Find Nearest Food Coupon Find Nearest Drink Coupon Find Nearest
	 * Show Coupon
	 * 
	 * 
	 * General "Whats near me?" SETUP: List, with the option to show on map
	 * 
	 * 
	 * List, with option to map (Color code for easy identification. (Red =
	 * food, blue = drink, etc) Or use icons. Martini glass for drinks,
	 * spoon/knife/fork for food, etc. "Show attraction coupons on map" (Eiffel
	 * Tower, rides, etc) Show food coupon options on map Show drink coupons
	 * options on map
	 */

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.main);
		tv = (EditText) findViewById(R.id.editText1);
		tv.setKeyListener(null);
		resources = getResources();
		
		// Create DBHelper instance for database operations
		myDbHelper = new MySQLiteHelper(this);

		// Create db object
		db = myDbHelper.getReadableDatabase();

		// Begin listening to GPS locations
		SetupLocationListener();

		context = this;

		// SetupListView();
	}
	
	 public static Context getAppContext() {
	        return MainVegasActivity.context;
	    }

	@Override
	public void onStop()
	{
		super.onStop();

		try
		{
			// We have to unregister the broadcast receiver when the app is
			// "stopped", which happens when user hits home button.
			// I don't know why yet
			unregisterReceiver(proximityIntentReceiver);
		}
		catch (IllegalArgumentException e)
		{

		}
	}




    public static void LogText(String stringToLog)
    {
        tv.setText(tv.getText() + "\r\n" + stringToLog);
        Log.v("Avocado", stringToLog);
    }

	public void btnClearText_onClick(View view)
	{
		tv.setText("");
	}
	
	public void btnWhatsNearMe_onClick(View view)
	{
		Intent myIntent = new Intent();
		myIntent.setClassName(this, "com.avocadosoft.lasvegasadvisor.LocationsActivity");
		
		Log.i("Avocado", "Sending this lat: " + currentLatitude);
		
		myIntent.putExtra("Latitude", "" + currentLatitude); // key/value pair, where key needs current package prefix.
		myIntent.putExtra("Longitude", "" + currentLongitude); // key/value pair, where key needs current package prefix.
		
		startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(com.avocadosoft.lasvegasadvisor.R.menu.menu, menu);

		Intent prefsIntent = new Intent(this, SettingsActivity.class);
		MenuItem preferences = menu.findItem(R.id.menuiItemSettings);
		preferences.setIntent(prefsIntent);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menuiItemSettings:

			this.startActivity(item.getIntent());
			break;
		}
		return true;
	}
	
	public static String[][] RetrieveAllLocationCoordinatesFromDatabase()
	{
		try
		{
			String retrieveAllLocationsQuery = "Select plc.*, plc.plcparid from ParentLocationCoordinates plc Inner Join ParentLocations pl on pl._id = plc.plcparid";
			cursor = db.rawQuery(retrieveAllLocationsQuery, null);
			int recordCount = cursor.getCount();

			cursor.moveToFirst();
			String[][] allLocations = new String[recordCount][4];
			Log.i("Avocado", "1");
			for (int i = 0; i < recordCount; i++)
			{
				allLocations[i][0] = cursor.getString(cursor.getColumnIndexOrThrow("PlcParID"));
				allLocations[i][1] = cursor.getString(cursor.getColumnIndexOrThrow("PlcLat"));
				allLocations[i][2] = cursor.getString(cursor.getColumnIndexOrThrow("PlcLong"));
				allLocations[i][3] = cursor.getString(cursor.getColumnIndexOrThrow("PlcRange"));
				cursor.moveToNext();
			}
			Log.i("Avocado", "2");
			cursor.close();
			return allLocations;
		}
		catch (Exception ex)
		{
			Log.i("Avocado", "Exception: " + ex.getMessage());
			return new String[1][4];
		}
	}

	public static String[][] RetrieveActiveLocationCoordinatesFromDatabase()
	{
		try
		{
			//String retrieveAllLocationsNotSentQuery = "Select plc.*, plc.plcparid from ParentLocationCoordinates plc Inner Join ParentLocations pl on pl._id = plc.plcparid where pl.ParSentAlert != 1";
            //Temporarily removed "where not sent" clause
            String retrieveAllLocationsQuery = "Select plc.*, plc.plcparid from ParentLocationCoordinates plc Inner Join ParentLocations pl on pl._id = plc.plcparid";
			cursor = db.rawQuery(retrieveAllLocationsQuery, null);
            int recordCount = cursor.getCount();
			cursor.moveToFirst();
			String[][] allLocations = new String[recordCount][4];
			Log.i("Avocado", "1");
			for (int i = 0; i < recordCount; i++)
			{
				allLocations[i][0] = cursor.getString(cursor.getColumnIndexOrThrow("PlcParID"));
				allLocations[i][1] = cursor.getString(cursor.getColumnIndexOrThrow("PlcLat"));
				allLocations[i][2] = cursor.getString(cursor.getColumnIndexOrThrow("PlcLong"));
				allLocations[i][3] = cursor.getString(cursor.getColumnIndexOrThrow("PlcRange"));
				cursor.moveToNext();
			}
			Log.i("Avocado", "2");
			cursor.close();
			return allLocations;
		}
		catch (Exception ex)
		{
			Log.i("Avocado", "Exception: " + ex.getMessage());
			return new String[1][4];
		}
	}

	public static String[][] RetrieveDealsByParentLocation(int parId)
	{
		retrieveDealsByParentLocationQuery = "Select pl.parname, t.* from Targets t Inner Join ParentLocations pl on pl._id = t.tarparid where TarParId = "
				+ parId + " and TarAlerted IS NULL";
		cursor = db.rawQuery(retrieveDealsByParentLocationQuery, null);
		cursor.moveToFirst();

		int recordCount = cursor.getCount();

		String[][] nearbyTargets = new String[recordCount][5];

		for (int i = 0; i < recordCount; i++)
		{
			nearbyTargets[i][0] = cursor.getString(cursor.getColumnIndexOrThrow("ParName"));
			nearbyTargets[i][1] = cursor.getString(cursor.getColumnIndexOrThrow("TarTitle"));
			nearbyTargets[i][2] = cursor.getString(cursor.getColumnIndexOrThrow("TarDescription"));
			nearbyTargets[i][3] = cursor.getString(cursor.getColumnIndexOrThrow("TarType"));
            nearbyTargets[i][4] = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			
			Log.i("Avocado", "nearbyTargets: s " + nearbyTargets[i][2]);
			
			cursor.moveToNext();
		}
		cursor.close();
		return nearbyTargets;
	}

	public String RetrieveParentLocationNameByParentID(int id)
	{
		String query = "Select * from ParentLocations where _id = " + id;
		cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndexOrThrow("ParName"));
		cursor.close();
		return name;
	}

	public void StopLocationListener()
	{
		if (locationListener != null)
		{
			locationManager.removeUpdates(locationListener);
			locationListenerRunning = false;
			Button btn = (Button) findViewById(R.id.btnStopListener);
			btn.setText(R.string.stopButtonTextStopped);
		}
	}

	public void SetupLocationListener()
	{
		Button btn = (Button) findViewById(R.id.btnStopListener);

		btn.setText(R.string.stopButtonTextStarted);

		locationListenerRunning = true;

		String sharedPrefsFileName = resources.getString(R.string.preferencesFileName);

		SharedPreferences sharedPreferences = this.getSharedPreferences(sharedPrefsFileName, 0);

		int pollingInterval = sharedPreferences.getInt(String.format(resources.getString(R.string.prefPollingIntervalKeyName)), 30000);
		int pollingDistance = sharedPreferences.getInt(String.format(resources.getString(R.string.prefPollingDistanceKeyName)), 10);
		proximityExpiration = sharedPreferences.getInt(String.format(resources.getString(R.string.prefPollingExpirationKeyName)), 10000);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new mylocationlistener(this, tv);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		
		providerName = locationManager.getBestProvider(criteria, true);
		tv.setText("Location provider: " + providerName);
		locationManager.requestLocationUpdates(providerName, pollingInterval, pollingDistance, locationListener);
	}

	public void AddProximityAlert(LocationManager lm, int dealCount, String name, String info, double latitude, double longitude, int range)
	{
		Intent intent = new Intent(PROX_ALERT_INTENT);

		intent.putExtra("Name", name);
		intent.putExtra("Info", info);
		intent.putExtra("DealCount", dealCount);

		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		lm.addProximityAlert(latitude, longitude, range, proximityExpiration, proximityIntent);

		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		proximityIntentReceiver = new ProximityIntentReceiver();
		registerReceiver(proximityIntentReceiver, filter);
	}

	public void btnRestartListener_onClick(View view)
	{
		Toast.makeText(this, "Restarting Listener", Toast.LENGTH_LONG).show();
		StopLocationListener();
		SetupLocationListener();
	}

	public void btnResetDatabase_onClick(View view)
	{
		Log.i("Avocado", "Resetting database");
		db.execSQL("Update Targets Set TarAlerted = null");
	}

	public void btnStopListener_onClick(View view)
	{
		if (locationListenerRunning)
			StopLocationListener();
		else
			SetupLocationListener();
	}

	public class mylocationlistener implements LocationListener
	{
		private String[][] arrayOfCoordinates;

		public mylocationlistener(Context con, TextView tv)
		{

		}

		@Override
		public void onLocationChanged(Location location)
		{
			
			if (location != null)
			{
				tv.setText("Location locked!");
				currentLatitude = location.getLatitude();
				currentLongitude = location.getLongitude();
				Log.i("Avocado", "Logging current latitude: " + currentLatitude);
				// The users location changed. Lets check for nearby targets
				GetNearbyTargets(location);
                LogText("Current Position: " + location.getLatitude() + " " + location.getLongitude());

			}
		}


		private String[][] GetNearbyTargets(Location location)
		{
			arrayOfCoordinates = null;
			arrayOfCoordinates = MainVegasActivity.this.RetrieveActiveLocationCoordinatesFromDatabase();
			Log.i("Avocado", "arrayOfCoordinates.length: " + arrayOfCoordinates.length);
            //LogText("arrayOfCoordinates.length: " + arrayOfCoordinates.length);
			String[][] output = new String[arrayOfCoordinates.length][5];

			float metersBetween;

			for (int i = 0; i < arrayOfCoordinates.length; i++)
			{
				if (arrayOfCoordinates[i] != null)
				{

					String parentLocID = arrayOfCoordinates[i][0];

                    //LogText("Parent LocationID: " +  parentLocID);
                    //Log deals to output
                    String[][] deals = MainVegasActivity.this.RetrieveDealsByParentLocation(Integer.parseInt(parentLocID));
					String targetLatitude = arrayOfCoordinates[i][1];
					String targetLongitude = arrayOfCoordinates[i][2];

					int targetNotificationRange = Integer.parseInt(arrayOfCoordinates[i][3].toString());

					metersBetween = GPSUtilities.getDistanceBetween(location.getLatitude(), location.getLongitude(), Double.parseDouble(targetLatitude),
							Double.parseDouble(targetLongitude));

                    //if (deals.length > 0)
                     //   LogText("Distance from " + deals[0][1] + ": " + metersBetween + " meters. (Target range: " + targetNotificationRange + ".");

					if (metersBetween < targetNotificationRange)
					{

                        //LogText("Nearby deal found " + deals[0][0] + ": " + metersBetween + " meters. (Target range: " + targetNotificationRange + ".");
						if (myDbHelper == null)
						{

							myDbHelper = new MySQLiteHelper(MainVegasActivity.this);

							db = myDbHelper.getWritableDatabase();

						}



						String[][] nearbyDeals = MainVegasActivity.this.RetrieveDealsByParentLocation(Integer.parseInt(parentLocID));

                        //LogText("Deal found! " + nearbyDeals[0][0]);

						// This is to accomodate for locations with no deals
						if (nearbyDeals.length > 0)
						{
							int dealCount = nearbyDeals.length;
							Log.i("Avocado", "Adding proximity alert");
                            db.execSQL("Update Targets Set TarAlerted = 1 where _id = " + nearbyDeals[0][4]);
                            LogText("Update Targets Set TarAlerted = 1 where _id = " + nearbyDeals[0][4]);
							// Right now it's adding a proximity alert for every
							// parent location i'm near.
							// I need to change it to add a single one, and pass
							// the arrays to the AddProximityAlert method.
							// Then create the string message based on all the
							// locations. Maybe just show a single count
                            LogText("Found: " +  nearbyDeals[0][1].toString() + " at " + nearbyDeals[0][0].toString());
                            LogText(GPSUtilities.ConvertFeetToMiles(GPSUtilities.ConvertMetersToFeet(metersBetween)) + " away");
							MainVegasActivity.this.AddProximityAlert(MainVegasActivity.this.locationManager, dealCount, nearbyDeals[0][0].toString(),
									nearbyDeals[0][1].toString(), Double.parseDouble(targetLatitude), Double.parseDouble(targetLongitude),
									targetNotificationRange);
						}

						arrayOfCoordinates[i] = null;
					}
				}
			}
			return output;
		}

		@Override
		public void onProviderDisabled(String provider)
		{

		}

		@Override
		public void onProviderEnabled(String provider)
		{

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}
	}

	// Will be called via the onClick attribute of the buttons declared in
	// main.xml
	public void onClick(View view)
	{/*
	 * @SuppressWarnings("unchecked") ArrayAdapter<ParentLocation> adapter =
	 * (ArrayAdapter<ParentLocation>) getListAdapter(); ParentLocation
	 * parentLocation = null; switch (view.getId()) { case R.id.add: String[]
	 * parentLocations = new String[] { "Cool", "Very nice", "Hate it" }; int
	 * nextInt = new Random().nextInt(3); // Save the new comment to the
	 * database parentLocation = datasource.createParentLocation("Mirage",
	 * "123 Main Street", "", "Las Vegas", "NV", "48038", 1);
	 * adapter.add(parentLocation); break; case R.id.delete: if
	 * (getListAdapter().getCount() > 0) { parentLocation = (ParentLocation)
	 * getListAdapter().getItem(0);
	 * datasource.deleteParentLocation(parentLocation);
	 * adapter.remove(parentLocation); } break; }
	 * adapter.notifyDataSetChanged();
	 */
	}

	public void SetupListView()
	{
		/*
		 * datasource = new ParentLocationsDataSource(); datasource.open();
		 * List<ParentLocation> values = datasource.getAllLocations();
		 * 
		 * // Use the SimpleCursorAdapter to show the // elements in a ListView
		 * ArrayAdapter<ParentLocation> adapter = new
		 * ArrayAdapter<ParentLocation>( this,
		 * android.R.layout.simple_list_item_1, values);
		 * setListAdapter(adapter);
		 */
	}

}
