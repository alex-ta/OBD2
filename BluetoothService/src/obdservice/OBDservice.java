package obdservice;

import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OBDservice extends Service implements Serializable {

	/**
	 * @author Alex 
	 * 
	 * Service der gestartet und gestoppt werden kann, er verwaltet den Ablauf der 
	 * OBD2 verbindung, und l�uft im Hintergrund der App. Dazu f�hrt die Klasse
	 * eine Instanz der Connection Klasse aus.
	 * 
	 * serialVersionUID ist eine DefaultID
	 * Connect obd2 ist die Instanz der Connect Klasse
	 */
	private static final long serialVersionUID = 1L;
	private Connect obd2;
	
	@Override
    public IBinder onBind(Intent arg0) {
		/**Unwichtige Methode da der Service ohne Activity l�uft*/
        return null;
    }
	
	@Override
    public void onCreate() {
		/**Service wird erstellt, Instanz wird erstellt*/
		obd2 = new Connect();
		Log.d("obd2 service","created");
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    	/**Service wird gestartet, obd2 wird verbunden und ausgelesen*/
		Log.d("obd2 service","started");
		obd2.start();
    }
 
    @Override
    public void onDestroy() {
    	/**Service wird beended obd2 wird beended*/
		Log.d("obd2 service","destroyed");
		obd2.close();
    }

}
