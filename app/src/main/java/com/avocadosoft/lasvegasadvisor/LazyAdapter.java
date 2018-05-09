package com.avocadosoft.lasvegasadvisor;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends ArrayAdapter<VegasLocation>
{

	Context context;
	int layoutResourceId;
	VegasLocation data[] = null;

	public LazyAdapter(Context context, int layoutResourceId, VegasLocation[] data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		VegasLocationHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new VegasLocationHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtDealTitle = (TextView) row.findViewById(R.id.txtDealTitle);
			//holder.txtVenue = (TextView) row.findViewById(R.id.txtVenue);

			row.setTag(holder);
		}
		else
		{
			holder = (VegasLocationHolder) row.getTag();
		}

		VegasLocation vegasLocation = data[position];
//		holder.txtDealTitle.setText(vegasLocation.title);
//		holder.txtVenue.setText(vegasLocation.venue);
//		holder.imgIcon.setImageResource(vegasLocation.icon);

		return row;
	}

	static class VegasLocationHolder
	{
		ImageView imgIcon;
		TextView txtDealTitle;
		TextView txtVenue;
	}
}