package com.avocadosoft.lasvegasadvisor;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class LocationsActivity extends Activity 
{
	private static ListView lv;
	private static ArrayAdapter<String> arrayAdapter;
	private ArrayList<String> arrayList;
	private double latitude;
	private double longitude;
	private static VegasLocation[] vegasLocationData;
	
	@Override
	public void onCreate(Bundle icicle)
	{
		
		super.onCreate(icicle);
		setContentView(R.layout.locations);
		
		// Double latitude =
		// intent.getDoubleExtra("com.avocadosoft.lasvegasadvisor.Latitude",
		// -1);
		// Double longitude =
		// intent.getDoubleExtra("com.avocadosoft.lasvegasadvisor.Longitude",
		// -1);

		Intent intent = getIntent();
		
		arrayList = new ArrayList<String>();
		lv = (ListView)findViewById(R.id.list);
		//arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
		
		//arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_item_row, arrayList);
		
		
		
		String sLatitude = intent.getStringExtra("Latitude");
		String sLongitude = intent.getStringExtra("Longitude");
		latitude = Double.parseDouble(sLatitude);
		longitude = Double.parseDouble(sLongitude);
		GetNearbyTargets(latitude, longitude);
	}
	
	private static void AddListViewItems()
	{
		//arrayAdapter.add(text);
		LazyAdapter adapter = new LazyAdapter(MainVegasActivity.getAppContext(), R.layout.listview_item_row, vegasLocationData);
		lv.setAdapter(adapter);
		lv.setTextFilterEnabled(true);
	}

	private static void GetNearbyTargets(double dLatitude, double dLongitude)
	{
		String[][] arrayOfCoordinates = null;
		arrayOfCoordinates = MainVegasActivity.RetrieveAllLocationCoordinatesFromDatabase();
		String[][] output = new String[arrayOfCoordinates.length][5];
		int foundDeals = 0;
		
		vegasLocationData = new VegasLocation[arrayOfCoordinates.length];

		float metersBetween;

		for (int i = 0; i < arrayOfCoordinates.length; i++)
		{
			if (arrayOfCoordinates[i] != null)
			{
				Log.i("Avocado", "AA");
				String parentLocID = arrayOfCoordinates[i][0];
				String targetLatitude = arrayOfCoordinates[i][1];
				String targetLongitude = arrayOfCoordinates[i][2];
				int targetNotificationRange = Integer.parseInt(arrayOfCoordinates[i][3].toString());
				metersBetween = GPSUtilities.getDistanceBetween(dLatitude, dLongitude, Double.parseDouble(targetLatitude), Double.parseDouble(targetLongitude));

				if (metersBetween < targetNotificationRange)
				{
					Log.i("Avocado", "A");
					String[][] nearbyDeals = MainVegasActivity.RetrieveDealsByParentLocation(Integer.parseInt(parentLocID));
					Log.i("Avocado", "Bgg");
					// This is to accomodate for locations with no deals
					if (nearbyDeals.length > 0)
					{
						int icon = R.drawable.redwifiicon;
						Log.i("Avocado", "Target type: " + nearbyDeals[0][3]);
						
						String targetType = nearbyDeals[0][3];
						int iTargetType;
						if (targetType != null && targetType.length() > 0)
						{
							iTargetType = Integer.parseInt(targetType);
							if (iTargetType == 1)
							{
								Log.i("Avocado", "TAasaaasRGdET TYPE IS 1");
								icon = R.drawable.food;
							}
							if (iTargetType == 2)
							{
								Log.i("Avocado", "TARGEsT TYPE IS 2");
								icon = R.drawable.drinks;
							}
							if (iTargetType == 3)
							{
								Log.i("Avocado", "TARGET TYPE IS 3");
								icon = R.drawable.redwifiicon;
							}
							if (iTargetType == 4)
							{
								Log.i("Avocado", "TARGET TYPE IS 4");
								icon = R.drawable.cards;
							}
							
							if (iTargetType == 5)
							{
								Log.i("Avocado", "TARGET TYPE IS 5");
								icon = R.drawable.microphone;
							}
						}
						
						
							
						Log.i("Avocado", "axCa");
						VegasLocation vegasLocation = new VegasLocation(icon, nearbyDeals[0][1] + " at " + nearbyDeals[0][0], nearbyDeals[0][2]);
						Log.i("Avocado", "D");
						vegasLocationData[foundDeals] = vegasLocation;
						Log.i("Avocado", "Ex");
						foundDeals++;		
						Log.i("Avocado", "F");
						Log.i("Avocado", "Avodado");
					}

					arrayOfCoordinates[i] = null;
				}
			}
		}
		AddListViewItems();
		
	}

}
