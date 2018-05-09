package com.avocadosoft.lasvegasadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Validate numbers only
		// callCountThresholdPreference.setOnPreferenceChangeListener(numberCheckListener);

		//Intent pushIntent = new Intent(this, gps.class);
		//this.startService(pushIntent);
	}
	
}
