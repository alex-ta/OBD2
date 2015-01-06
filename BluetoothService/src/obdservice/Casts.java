package obdservice;

import android.util.Log;


public class Casts {
	
	public static String NOVALUE = "NoData";
	
	public static String getString(byte ...bs){
		String summe = "";
		for(byte b: bs){
		summe +=((char)((Byte)b).intValue());	
		}
		return summe;
	}
	
	private static String clearString(String msg){
		msg = msg.trim();
		msg = msg.replaceAll("[ <>\\r\\n]", "");
		return msg;
	}
	
	public static String getSpeed(String msg)throws NumberFormatException{
		Log.e("msgspeed", msg);
		msg = clearString(msg);
		if(msg.contains("410D")){
		msg = msg.replace("410D","");
		return Integer.parseInt(msg,16)+"";
		}
		return NOVALUE;
	}
	
	public static String getFuel(String msg)throws NumberFormatException{
		msg = clearString(msg);
		Log.e("msgfuel", msg);
		if(msg.contains("7F01")){
			msg = msg.replace("7F01","");
			return Integer.parseInt(msg,16)/60+"";
			}
		return NOVALUE;
	}
	
}
