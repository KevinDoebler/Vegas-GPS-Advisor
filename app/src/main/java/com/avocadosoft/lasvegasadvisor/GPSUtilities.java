package com.avocadosoft.lasvegasadvisor;
import android.location.Location;
import android.location.LocationManager;
import android.app.Activity;
import android.content.Context;


public class GPSUtilities
{

	private static LocationManager lManager;
	private static Location provider;
	private static Context localContext;
	
	public GPSUtilities(Context context)
	{
		localContext = context;
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}



	public static int ConvertMetersToFeet(float meters)
	{
		double product = meters * 3.2808;
		return (int) product;
	}

	public static double ConvertFeetToMiles(int feet)
	{
		return feet / 5280.0;
	}

	public static float getDistanceBetween(double startLat, double startLong,
			double endLat, double endLong)
	{

		float[] results = new float[3];
		Location.distanceBetween(startLat, startLong, endLat, endLong, results);
		return results[0];

	}

	public static float getDistanceBetween(double endLat, double endLong)
	{

		lManager = (LocationManager) localContext.getSystemService("location");
		provider = lManager.getLastKnownLocation("gps");

		float[] results = new float[3];
		Location.distanceBetween(provider.getLatitude(), provider
				.getLongitude(), endLat, endLong, results);
		return results[0];

	}
	
	public static double getCurrentLatitude()
	{
		lManager = (LocationManager) localContext.getSystemService("location");
		provider = lManager.getLastKnownLocation("gps");
		
		return provider.getLatitude();
		
	}
	
	public static double getCurrentLongitude()
	{
		lManager = (LocationManager) localContext.getSystemService("location");
		provider = lManager.getLastKnownLocation("gps");
		
		return provider.getLongitude();
	}

}
