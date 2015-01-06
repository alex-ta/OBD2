package com.example.bluetooth;
 
import obdservice.OBDservice;
import com.example.bluetooth.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class StartButton extends Activity{
	/**
	 * Diese Activity kümmert sich um den Datenaustausch zwischen Handy und Gerät.
	 * Sie verfügt über ein UserInterface zum Teste, dies Klasse wird nicht in der 
	 * eigentlichen App benötigt und dient nur zum Testen.
	 * */
	
	private Button obdonoff;
	private static boolean state;
		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startbutton);
		obdonoff = (Button) findViewById(R.id.button_obdonoff);
		state = false;
	}
	
	public void startobd(View v){
	if(!state){
		obdon();
		Intent myIntent = new Intent(this, BluetoothActivity.class);
		this.startActivityForResult(myIntent, 0);
	}else{
		obdoff();
		stopService(new Intent(this, OBDservice.class));
	}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == RESULT_OK && !data.equals(null)) {
	        	if(!data.getExtras().getBoolean("ison")){
	        	obdoff();}
	    }else{
	    	obdoff();}
	    }
	
	private void obdon(){
		state = true;
		obdonoff.setText(R.string.offobd);}
	
	private void obdoff(){
		state = false;
		obdonoff.setText(R.string.onobd);}
}

