package com.avocadosoft.lasvegasadvisor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{
	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.avocadosoft.lasvegasadvisor/databases/";
	private static String DB_NAME = "lva";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public MySQLiteHelper(Context context)
	{
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		try
		{
			createDataBase();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException
	{
		Log.i("Avocado", "Creating database");
		boolean dbExist = checkDataBase();
		Log.i("Avocado", "dbExist: " + dbExist);
		if (!dbExist)
		{
			Log.i("Avocado", "!dbExist Attemping to get readable database");
			this.getReadableDatabase();

			try
			{
				copyDataBase();
			}
			catch (IOException e)
			{
				throw new Error("Error copying database");
			}
		}
		
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase()
	{

		SQLiteDatabase checkDB = null;

		try
		{
			String myPath = DB_PATH + DB_NAME;
			Log.i("Avocado", "About to open database read only");
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException e)
		{
			Log.i("Avocado", "Database doesn't exist yet");
		}

		if (checkDB != null)
		{

			checkDB.close();
		}

		Boolean checkDBBoolean = checkDB != null;

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException
	{
		Log.i("Avocado", "Attemping to copy database");
		// Open your local db as the input stream
		Log.i("Avocado", "About to open input stream: " + DB_NAME);
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		Log.i("Avocado", "About to open OUTPUT stream: " + outFileName);
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		Log.i("Avocado", "OPENED OUTPUT stream: " + outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0)
		{
			myOutput.write(buffer, 0, length);
		}
		Log.i("Avocado", "Transferred bytes");
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException
	{

		// Open the database
		String myPath = DB_PATH + DB_NAME;

		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		return myDataBase;
	}

	@Override
	public synchronized void close()
	{

		if (myDataBase != null)
		{

			myDataBase.close();
		}

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

	public void InsertParentLocations()
	{
		/*
		 * Log.v("kjdv", "Attempting to insert parent location");
		 * ParentLocationsDataSource pldc = new ParentLocationsDataSource(null);
		 * Log.v("kjdv", "Created instance of ParentLocationsDataSource");
		 * ParentLocation pl = pldc.createParentLocation("Mirage",
		 * "Vegas Strip", "", "Las Vegas", "NV", "12345", Long.valueOf("1"));
		 * //ParentLocation pl2 = pldc.createParentLocation("Treasure Island",
		 * "Vegas Strip", "", "Las Vegas", "NV", "12345", 1); Log.v("kjdv",
		 * "Inserted records!"); ParentLocationCoordinatesDataSource plcdc = new
		 * ParentLocationCoordinatesDataSource(null); ParentLocationCoordinates
		 * plc = new ParentLocationCoordinates();
		 * 
		 * //plcdc.createParentLocationCoordinates(pl.getID(),
		 * Double.valueOf("36.123259"), Double.valueOf("-115.176949"), 170);
		 * Log.v("kjdv", "Inserted parent location");
		 */
	}

}