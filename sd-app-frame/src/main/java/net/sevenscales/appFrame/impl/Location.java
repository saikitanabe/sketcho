package net.sevenscales.appFrame.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Location {
	private Map params = new HashMap();
	
	public Location(String url) {
		String[] splittedUrl = url.split("\\?");
		if (splittedUrl.length == 2) {
			String[] idValuePairs = splittedUrl[1].split("&");
			
			for (int i = 0; i < idValuePairs.length; ++i) {
				String[] idValuePair = idValuePairs[i].split("=");
				params.put(idValuePair[0], idValuePair[1]);
			}
		}
	}

	public Location(Map requests) {
		this.params = requests;
	}

	public Map getRequests() {
		return params;
	}
	
	public boolean equals(Object arg0) {
		Location e = (Location) arg0;
		Iterator i = e.params.keySet().iterator();
		
		if (e.params.size() != this.params.size()) {
			return false;
		}
		
		while (i.hasNext()) {
			String key = (String) i.next();
			String checkValue = (String ) this.params.get(key);
			String value = e.params.get(key).toString();
			if (checkValue == null || !checkValue.equals(value)) {
				return false;
			}
		}
		return true;
	}

	public static String formatRequests(Map requests) {
		String result = new String();
		Set keys = requests.keySet();
		if (keys.size() == 0) {
			return "";
		}
		result = "?";
		
		Iterator i = keys.iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			String id = (String) i.next();
			String value = convertToString(requests.get(id));
			result += id;
			result += "=";
			result += value;
			hasNext = i.hasNext();
			if (hasNext) {
				// separate params with &
				result += "&";
			}
		}
		return result;
	}
	
	public native String currentLocation() /*-{
	  return $wnd.location.href;
	}-*/;
	
	private static String convertToString(Object object) {
		return object.toString();
	}
}
