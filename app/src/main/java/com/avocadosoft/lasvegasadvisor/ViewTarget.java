package com.avocadosoft.lasvegasadvisor;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ViewTarget extends ListActivity
{
	String classes[] = { "Movies", "Pictures" };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.v("kjdv2", "kjdv2");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			String[] allLocations = new String[extras.size()];
			String value = extras.getString("Name");

			/*
			 * for (int i = 0; i < extras.size(); ++i) { localArray[i] =
			 * extras.getString("") }
			 */
			Log.v("db", "Value: " + value);
		}
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, classes));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	}
}
