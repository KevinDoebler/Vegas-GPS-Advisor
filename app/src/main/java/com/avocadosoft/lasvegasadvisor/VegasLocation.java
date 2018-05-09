package com.avocadosoft.lasvegasadvisor;

public class VegasLocation
{
	public int icon;
	public String title;
	public String venue;
	public VegasLocation()
	{
		super();
	}
	
	 public VegasLocation(int icon, String venue, String title) {
	        super();
	        this.icon = icon;
	        this.title = title;
	        this.venue = venue;
	    }
}
