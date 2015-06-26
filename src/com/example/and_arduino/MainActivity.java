package com.example.and_arduino;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Delayed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class MainActivity extends Activity {

	Button btnOn = null;
	Button btnOff = null;
	TextView txt = null;
	static byte[] read;
	static UsbManager manager;

	static UsbSerialDriver driver;

	PrintAsyn myTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnOn = (Button) findViewById(R.id.btnOn);
		btnOff = (Button) findViewById(R.id.btnOff);
		txt = (TextView) findViewById(R.id.text);

		manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		driver = UsbSerialProber.acquire(manager);

		myTask = new PrintAsyn();

		if (driver != null) {
			try {
				driver.open();
				driver.setBaudRate(115200);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (driver != null) {
					try {
						if (v.getId() == R.id.btnOn) {
							driver.write("1".getBytes(), 100);
						} else {
							driver.write("0".getBytes(), 100);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					txt.setText("드라이버 없음!");
				}
			}
		};
		myTask.execute();

		btnOn.setOnClickListener(listener);
		btnOff.setOnClickListener(listener);

	}

	class PrintAsyn extends AsyncTask<StringBuilder, String, String> {

		byte[] read = new byte[100];

		String strTemp = new String();
		StringBuilder str = new StringBuilder();

		@Override
		protected String doInBackground(StringBuilder... params) {
			// TODO Auto-generated method stub

			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (driver != null) {
					try {
						int numByteRead = driver.read(read, 100);
						strTemp = new String(read, 0, numByteRead);
						
						if(strTemp.equals("LED ON\n")){
							str.append("켜짐");
						} else if(strTemp.equals("LED OFF\n")){
							str.append("꺼짐");
						}
//						str.append("aa"+strTemp);
												
						publishProgress(str.toString());
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values[0]);
			
			txt.setText(values[0]);

		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

	}
}