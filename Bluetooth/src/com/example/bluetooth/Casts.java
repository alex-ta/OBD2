package com.example.bluetooth;

import android.util.Log;

public class Casts {
	public static final String NOVALUE = "value not supported";
	public static final String NOMODE = "mode not supported";
	
	
	public static String getString(byte ...bs){
		String summe = "";
		for(byte b: bs){
		summe +=((char)((Byte)b).intValue());	
		}
		return summe;
	}
	
	public   static String getResponse(String msg){
		msg = msg.trim();
		msg = msg.replaceAll("[ <>]", "");
		String erg = NOVALUE;
		Log.d("msg", msg);
		if(msg.contains(OBD2E.M1_2_PID_SPEED)){
			erg = getSpeed(msg.split(OBD2E.M1_2_PID_SPEED)[1]);
		}else if(msg.contains(OBD2E.M1_2_PID_FUELSHORTTERMB1)){
			erg = getFuel(msg.split(OBD2E.M1_2_PID_FUELSHORTTERMB1)[1]);
		}else if(msg.contains("4100")){
			erg = "OBD started";
		}else if(msg.contains("NO")){
			erg = "NO DATA check if your car is on";
		}else if(msg.contains("ABLE")){
			erg = "CONNECTION ERROR check if your car is and supports OBD2";	
		}else if(msg.contains("EARC")){
			erg = "PROTOCOL TIMEOUT wait a minuite";
			OBD2E.sleep(OBD2E.LONGTIME);
		}else if(msg.contains("41")){
			erg = "";
		}else {
			erg = "not implemented";
		}
		return erg;
	}
	
	private static String getSpeed(String msg){
		msg = msg.replaceAll("[\\r\\n]","");
		return Integer.parseInt(msg,16)+"";
	}
	
	private static String getFuel(String msg){
		int value = Integer.parseInt(msg,16); // cast the string hex value to an int
		int fuel = (value-128) * 100/128;
		return fuel+"";
	}
	
}
