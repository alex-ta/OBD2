package com.example.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import com.example.bluetooth.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {
	/**
	 * @author Alex
	 * Die Mainactivity ist der Einstiegspunkt des Bluetoothfeatures. Durch
	 * diese Activity wird das Paaren (pairing) zweier Ger�te intern �berwacht.
	 * Nach dem pairing, werden Die Ger�te miteinander Verbunden, diese
	 * Verbindung wird im Attribute bluesocket gespeichert. Nach dem der
	 * Bluetoothsocket etabliert wurde startet der OBD Service automatisch
	 * */

	private ArrayAdapter<String> listAdapter;
	private ListView listView;
	private Activity view;
	private BluetoothAdapter bluetooth;
	private ArrayList<BluetoothDevice> devices;
	private IntentFilter filter;
	private BroadcastReceiver receiver, receiver1, receiver2, receiver3,
			receiverpair;
	private OnItemClickListener listener;
	public static BluetoothSocket bluesocket;
	private static boolean isturnonrequest = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/**
		 * Hier werden �hnlich wie in einem Konstruktor Startdaten festgelegt.
		 * Es wird eine Layout verkn�pft, das wir als XMLvordefiniert haben
		 * (select_bluetooth). Nach dem Layout werden die einzelnen
		 * LayoutElemente Verkn�pft. Das wichtigste Element des Layouts ist die
		 * Liste, Sie zeigt sp�ter die verf�gbaren Bluetoothger�te an. eine
		 * Liste besteht aus einem GUI Element ListView und einem ListAdaper,
		 * der den Inhalt der Liste verwaltet.
		 * 
		 * Desweiteren wird hier der BluetoothAdapter bluetooth inizalisiert,
		 * indem der Bluetoothsensor per getDefaultAdapter zur Verf�gung
		 * gestellt wird. Falls Bluetooth aus ist, oder kein Sensor vorhanden
		 * ist, hat bluetooth den Wert null. Ist das der Fall, wird Bluetoth
		 * angeschalten oder eine Meldung erscheint das kein Sensor vorhanden
		 * ist.
		 * 
		 * Zuletzt werden Intentfilter angemeldet. Diese werden durch Aktionen
		 * der Bluetoothfilters ausgel�st, um sp�ter die Fortschritte zu
		 * �berwachen (tracing)
		 * */
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.select_bluetooth);
		view = this;
		setProgressBarIndeterminateVisibility(false);
		listView = (ListView) findViewById(R.id.b_listView);
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 0);
		listView.setAdapter(listAdapter);
		listener = new ItemClickBT();
		listView.setOnItemClickListener(listener);

		bluetooth = BluetoothAdapter.getDefaultAdapter();
		devices = new ArrayList<BluetoothDevice>();

		if (bluetooth == null) {
			Toast.makeText(getApplicationContext(), "No bluetooth detected",
					Toast.LENGTH_SHORT).show();
			close();
		} else {
			if (!bluetooth.isEnabled()) {
				turnOnBT();
			} else
				startDiscovery();

		}
		for (BluetoothDevice d : bluetooth.getBondedDevices())
			unpairDevice(d);

		filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		receiverpair = new ReceiverBTPair();
		registerReceiver(receiverpair, filter);
		receiver = new ReceiverBT();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		receiver1 = new ReceiverBT();
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver1, filter);
		receiver2 = new ReceiverBT();
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver2, filter);
		receiver3 = new ReceiverBT();
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver3, filter);
		reset();
	}

	private void startDiscovery() {
		/**
		 * startet eine Bluetooth Ger�tesuche, und beended die vorherige, falls
		 * vorhanden
		 */
		bluetooth.cancelDiscovery();
		bluetooth.startDiscovery();

	}

	private void reset() {
		/**
		 * L�scht die Liste der Bluetooth Ger�te und startet einen neuen
		 * Ger�tesuchlauf
		 */
		devices.clear();
		startDiscovery();
	}

	public void refresh(View view) {
		/**
		 * Wrapper der ResetMethode f�r einen Button (erwartet intern View
		 * Parameter)
		 */
		reset();
	}

	private void turnOnBT() {
		/**
		 * isturnonrequest wird auf true gesetzt, dannach wird eine
		 * Bluetoothanfrage ausgel�st
		 */
		isturnonrequest = true;
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/**
		 * Diese Methode wird aufgerufen, wenn die Methode ein Ergebnis
		 * zur�ckbekommet (Hier von der Bluetoothanfrage) falls das anschalten
		 * des Bluetooth nicht erfolg wird die App beendet falls das Bluetooth
		 * angeschalten wurde, wird das suchen nache Ger�tern gestartet.
		 * */
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(),
					"Bluetooth must be enabled to continue", Toast.LENGTH_SHORT)
					.show();
			close();
		}
		isturnonrequest = false;
		startDiscovery();
	}

	private class ReceiverBT extends BroadcastReceiver {
		/**
		 * Private Klasse ReceiverBT, diese Klasse arbeitet mit den Einkommenden
		 * Bluetooth Ereignissen. Der Receiver wurde oben mit IntentFiltern auf
		 * die Ereignisse spezialisiert.
		 * */

		@Override
		public void onReceive(Context context, Intent intent) {
			/**
			 * diese Methode entscheided was passiert, wenn ein angemeldetes
			 * Ereignis ausgel�st wird. 1) Ein neues Bluetoothger�t wird
			 * gefunden, falls es noch nicht vorhanden ist, wird es der Liste
			 * Hinzugef�gt, dannach wird die Liste gel�scht und alle gefunden
			 * Ger�te werden wieder eingetragen, damit Ger�te die nicht mehr
			 * vorhanden sind auch nicht auftauchen. 2) Ger�tesuche wird
			 * gestarte, Wartesymbol wird gesetzt. 3) Ger�tesuche wird beendet,
			 * Wartesymbol wird versteckt. 4) Bluetooth wird deaktivert, eine
			 * Anfrage zur Bluetoothaktivierung wird gesendet.
			 * */
			switch (intent.getAction()) {
			case BluetoothDevice.ACTION_FOUND:
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (!devices.contains(device))
					devices.add(device);
				listAdapter.clear();
				for (BluetoothDevice dev : devices) {
					if (bluetooth.getBondedDevices().contains(dev)) {
						listAdapter.add(dev.getName() + "      paired      "
								+ "\n" + dev.getAddress());
					} else {
						listAdapter
								.add(dev.getName() + "\n" + dev.getAddress());
					}
				}

				break;
			case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
				setProgressBarIndeterminateVisibility(true);
				break;
			case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
				setProgressBarIndeterminateVisibility(false);
				break;

			case BluetoothAdapter.ACTION_STATE_CHANGED:
				if (bluetooth.getState() == BluetoothAdapter.STATE_OFF
						&& !isturnonrequest) {
					turnOnBT();
				}
			}
		}

	}

	private class ItemClickBT implements OnItemClickListener {
		/** Private Klasse die die Clicks auf Listensymbole auswertet */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/**
			 * Diese Methode wird durch eine Klick auf eine Listenelement
			 * ausgel�st. das Ger�tesuchen wird beendet. Dannach wird das Ger�t
			 * gepaired, oder falls es schon gepaired ist unpaired.
			 * */
			if (bluetooth.isDiscovering()) {
				bluetooth.cancelDiscovery();
			}
			BluetoothDevice device = devices.get(position);
			if (bluetooth.getBondedDevices().contains(device)) {
				unpairDeciveConnected(device);
			} else {
				new AsyncPair().execute(device);
			}
		}

	}

	/** RMI METHODES OF BLUETOOTH **/
	private void unpairDevice(BluetoothDevice device) {
		/**
		 * Diese Methode l�scht ein gepaired es Ger�t. Um auch Ger�te ab 3.0 Zu
		 * nutzten, wird die private Methode removeBond aufgerufen.
		 * */
		try {
			Method method = device.getClass().getMethod("removeBond",
					(Class[]) null);
			method.invoke(device, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unpairDeciveConnected(BluetoothDevice device) {
		/**
		 * Diese Methode l�scht alle gepaired es Ger�t und gibt einen Text aus.
		 * */
		unpairDevice(device);
		Toast.makeText(getApplicationContext(), "Pairing dissconnected",
				Toast.LENGTH_SHORT).show();
	}

	private void pairDevice(BluetoothDevice device) {
		/**
		 * Diese Methode paired ein Ger�t. Um auch Ger�te ab 3.0 Zu nutzten,
		 * wird die private Methode removeBond aufgerufen.
		 * */
		try {
			Method method = device.getClass().getMethod("createBond",
					(Class[]) null);
			method.invoke(device, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class AsyncPair extends
			AsyncTask<BluetoothDevice, BluetoothDevice, BluetoothDevice> {
		/**
		 * Dieser Thread f�hrt das Paaren zweiter Bluetoothger�te asynchrone aus
		 */

		@Override
		protected BluetoothDevice doInBackground(BluetoothDevice... params) {
			/**
			 * asynchrone werden alle Ger�te gel�scht, dannach wir das richtige
			 * Ger�te mit dem Device gepaired.
			 * */
			if (bluetooth.getBondedDevices().size() > 0)
				for (BluetoothDevice d : bluetooth.getBondedDevices()) {
					unpairDevice(d);
				}
			pairDevice(params[0]);
			return params[0];
		}

		@Override
		protected void onPreExecute() {
			/**
			 * Wird vor der ausf�hrung des asynchronen Tasks ausgef�hrt, sorgt
			 * f�r das Bessere Userverst�ndiss (Wartesymbol und Text was
			 * passiert)
			 * */
			super.onPreExecute();
			bluetooth.cancelDiscovery();
			view.setProgressBarIndeterminateVisibility(true);
			Toast.makeText(getApplicationContext(), "Pairing started",
					Toast.LENGTH_SHORT).show();
		}

	}

	private class ReceiverBTPair extends BroadcastReceiver {
		/**
		 * Receiver der wie davor die Ereignisse des Bluetoothger�tes abf�ngt,
		 * dieser Receiver ist auf den Verbindungsstatus von Bluetooth
		 * spezialisiert. 1) Wird ausgel�st falls etwas mit Bluetooth passiert
		 * 1.1) Falls das Ger�t versucht hat sich zu paaren und nun gepaart ist,
		 * wird eine Meldung ausgegeben das Bluetooth gepaired ist, au�erdem
		 * wird jetzt die verbindung (connection) gestartet. Und die Liste der
		 * Ger�te gel�scht. 1.2) Falls das Ger�t vom Zustand gepaart zu nicht
		 * gepaart wechselt wird hier die Liste der Ger�te gel�scht
		 * 
		 * das l�schen der Liste �hnelt einem update, da alle Werte erneut
		 * eingetragen werden.
		 * 
		 * */

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
				final int state = intent
						.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
								BluetoothDevice.ERROR);
				final int prevState = intent.getIntExtra(
						BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
						BluetoothDevice.ERROR);

				if (state == BluetoothDevice.BOND_BONDED
						&& prevState == BluetoothDevice.BOND_BONDING) {
					Toast.makeText(getApplicationContext(),
							"Pairing established", Toast.LENGTH_SHORT).show();
					reset();
					new AsyncConnect().execute((BluetoothDevice) bluetooth
							.getBondedDevices().toArray()[0]);

				} else if (state == BluetoothDevice.BOND_NONE
						&& prevState == BluetoothDevice.BOND_BONDED) {
					// gets also called when existing paired Devices get deleted
					reset();
				}
			}
		}
	}

	private class AsyncConnect extends
			AsyncTask<BluetoothDevice, BluetoothSocket, BluetoothSocket> {
		/** Diese Klasse ist eine Thread der asynchrone die Verbindung etabliert */

		@Override
		protected BluetoothSocket doInBackground(BluetoothDevice... params) {
			/**
			 * Diese Methode wird asyncrhone ausgef�hr, hier wird ein Ger�t das
			 * gepaart wurde verbunden, dabei wird die Ger�teId des anderen
			 * Ger�tes in die Methode createRfcommSocketToServiceRecord
			 * �bergeben, diese Methode �ffnet den Bluetoothsocket �ber den die
			 * Communication l�uft. Dannach wird der Socket verbunden. Fehler
			 * werden abgefangen und gelogged. Der Socket wird aus zur�ckgegebn.
			 * */
			BluetoothSocket socket = null;
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				Log.e("error s1", socket + "");
				UUID deviceUuid = params[0].getUuids()[0].getUuid();
				Log.e("Uuid", deviceUuid + "");
				socket = params[0]
						.createRfcommSocketToServiceRecord(deviceUuid);
				Log.e("error s2", socket + "");
				socket.connect();
				Log.e("error s3", socket + "");
			} catch (IOException e) {
				Log.e("Bluetooth run", "connect failed:/n" + e.getStackTrace());
				// Unable to connect; close the socket and get out
				try {
					socket.close();
					socket = null;
					close();
				} catch (IOException closeException) {
				}
			}

			return socket;
		}

		@Override
		protected void onPreExecute() {
			/**
			 * Diese Methode wird vor dem asynchronen Thread ausgef�rht und
			 * informiert den Nutzer was gerade Passiert.
			 * */
			super.onPreExecute();
			view.setProgressBarIndeterminateVisibility(true);
			Toast.makeText(getApplicationContext(), "Connection started",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(BluetoothSocket result) {
			/**
			 * Diese Methode wird nach dem Beenden des asynchronen Threads
			 * ausgef�hrt, der Nutzer wird wieder informiert, desweiteren wird
			 * der OBD2 Service gestartet. Falls das Verbinden Fehlgeschlagen
			 * ist, erscheint eine Meldung.
			 * */
			super.onPostExecute(result);
			if (result != null) {
				bluesocket = result;
				Toast.makeText(getApplicationContext(), "Connection success",
						Toast.LENGTH_SHORT).show();
				view.setProgressBarIndeterminateVisibility(false);

				startService(new Intent(getApplicationContext(),
						obdservice.OBDservice.class));
				close();
			} else
				Toast.makeText(getApplicationContext(), "Connection failed",
						Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onBackPressed() {
		/** die Action bei dem R�ckw�rtsbutton wird hier �berschrieben */
		close();
	}

	public void close() {
		/**
		 * diese Funktion schlie�t die Activity und gibt zur�ck ob die
		 * Verbindung geklappt hat oder nicht
		 * */
		Intent data = new Intent();
		if (bluesocket != null)
			data.putExtra("ison", true);
		else
			data.putExtra("ison", false);
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, data);
		} else {
			getParent().setResult(Activity.RESULT_OK, data);
		}
		this.unregisterReceiver(receiver);
		this.unregisterReceiver(receiver1);
		this.unregisterReceiver(receiver2);
		this.unregisterReceiver(receiver3);
		this.unregisterReceiver(receiverpair);
		finish();
	}

}
