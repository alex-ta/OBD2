package com.example.bluetooth;

public class OBD2E {
	/**For more support look at http://en.wikipedia.org/wiki/OBD-II_PIDs*/
	
	public static int M_CURRENTDATA = 1;
	public static int M_FREEZFRAME = 2;
	//public static int M_TROUBLECODES = 3;
	//public static int M_CLEARTROUBLECODES = 4;
	public static int M_VEHICLEINFO = 9;
	
	public static final char fin ='\r';
	public static final int PIDRECEIVELENGTH = 24;
	
	public static final String INITATZ ="ATZ"+fin;
	public static final String INITATS0 ="ATS0"+fin;
	public static final String INITATA0 ="ATAT0"+fin;
	public static final String INITATST10="ATST10"+fin;
	public static final String INITATSS ="ATSS"+fin;
	public static final String INITATSI ="ATSI"+fin;
	public static final String INITATH1 = "ATH1"+fin;
	public static final String INITATE0="ATE0"+fin;
	public static final String INITATL0="ATL0"+fin;
	public static final String INITATST="ATST"+fin;
	public static final String FIRSTCOMMAND ="0100"+fin;
	
	public static final int SHORTTIME=500;
	public static final int MIDDLETIME = 1000;
	public static final int LONGTIME =2000;
	public static final int READTIME = 400;
	
	
	// change mode ?
	public static String INITATSP(String i){
		return "ATSP"+i+fin;
	}
	
	public static String AUTO_ATSP0 ="ATSP0"+fin;
	public static String AUTO_ATDP ="ATDP"+fin;
	
	
	public static final String M1_2_PID_PIDSUPPORT_20 = "00";
	public static final String M1_2_PID_FUELSYSSTATUS = "03";
	public static final String M1_2_PID_ENGINELOADVALUE = "04";
	public static final String M1_2_PID_ENGINETEMPERATURE = "05";
	public static final String M1_2_PID_FUELSHORTTERMB1 = "06";
	public static final String M1_2_PID_FUELSHORTTERMB2 = "08";
	public static final String M1_2_PID_FUELLONGTERMB1 = "07";
	public static final String M1_2_PID_FUELLONGTERMB2 = "09";
	public static final String M1_2_PID_FUELPRESSURE = "0A";
	public static final String M1_2_PID_ENGINERPM = "0C";
	public static final String M1_2_PID_SPEED = "0D";
	public static final String M1_2_PID_AIRTEMPIN = "0F";
	public static final String M1_2_PID_AIRFLOWRATE = "10";
	public static final String M1_2_PID_ODBSTANDART = "1C";
	public static final String M1_2_PID_ENGINERUNTIME = "1F";
	
	public static final String M1_2_PID_PIDSUPPORT_40 = "20";
	public static final String M1_2_PID_FUELLVLINPUT = "2F";
	
	public static final String M9_PID_PIDSUPPORT_20 = "00";
	public static final String M9_PID_VIN = "02";
	public static final String M9_PID_ECUNAME = "0A";
	
	/**returns the max value of bytes returend -> normally 2-4 because of m9 its 20*/
	public static int MAX_RETURN_LENGTH = 20;
	
	public static String Msg(String pid){
		return "01"+pid+fin;
	}
	public static String Msg(int mode,String pid){
		String m = Integer.toHexString(mode);	
		return "0"+m+pid+fin;
	}
	
	public static synchronized String sleepCommand(int time,String command){
		sleep(time);
		return command;
	}
	public static synchronized String sleepCommandAuto(int time,String command){
		sleep(time);
		return Msg(command);
	}
	
	
	
	public static void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
