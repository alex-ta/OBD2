package com.example.bluetooth;

import obdservice.OBDservice;
import com.example.bluetooth.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartButton extends Activity {
	/**
	 * @author Alex
	 * Diese Activity hat eine Start button, welcher sich um Das starten bzw
	 * beenden des Services k�mmert diese Activity kann man in ein Optionsmenu
	 * einbauen. Die Activity �berwacht zus�tzlich den status der Verbindung
	 * durch den boolean state
	 * */

	private Button obdonoff;
	private static boolean state = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/** Das Layout wird gesetzt und die Referenzen werden geholt */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startbutton);
		obdonoff = (Button) findViewById(R.id.button_obdonoff);
	}

	public void startobd(View v) {
		/**
		 * der Status des OBD Services wird �berpr�ft, wenn der OBD service aus
		 * ist wird beim clicken die BluetoothActivity aufgerufen, die den
		 * status des Services zur�ckgibt. Falls der Service an ist wird er
		 * ausgeschalten. Der Button hat den Listener in seiner XML definiert.
		 * */
		if (!state) {
			obdon();
			Intent myIntent = new Intent(this, BluetoothActivity.class);
			this.startActivityForResult(myIntent, 0);
		} else {
			obdoff();
			stopService(new Intent(this, OBDservice.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/**
		 * Activity wartet auf den status des Services und setzt den Status
		 * sowie den Buttontext nach dem Ergebnis.
		 * */
		if (resultCode == RESULT_OK && !data.equals(null)) {
			if (!data.getExtras().getBoolean("ison")) {
				obdoff();
			}
		} else {
			obdoff();
		}
	}

	private void obdon() {
		/** setzt die Werte wenn der Service l�uft */
		state = true;
		obdonoff.setText(R.string.offobd);
	}

	private void obdoff() {
		/** setzt die Werte wenn der Service aus ist */
		state = false;
		obdonoff.setText(R.string.onobd);
	}
}
