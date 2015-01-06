package obdservice;

import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OBDservice extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connect obd2;
	
	@Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
	
	@Override
    public void onCreate() {
		obd2 = new Connect();
		Log.d("obd2 service","created");
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
		Log.d("obd2 service","started");
		obd2.start();
    }
 
    @Override
    public void onDestroy() {
		Log.d("obd2 service","destroyed");
		obd2.close();
    }

}
