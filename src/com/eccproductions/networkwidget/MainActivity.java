package com.eccproductions.networkwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	//Variables
	Button btn_main_exit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupVariables();
	}

	private void setupVariables() {
		// link items with XML
		btn_main_exit = (Button) findViewById(R.id.btn_main_exit);
		
		//set onClickListener for XML items
		btn_main_exit.setOnClickListener(this);
		}

	public void onClick(View v) {
		// TODO set items to do their thing when clicked
		switch(v.getId()){
		case R.id.btn_main_exit:
			finish();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// return super.onCreateOptionsMenu(menu);
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Make aboutUs page
		switch (item.getItemId()) {
		case R.id.aboutUs:
			// aboutUs
			Intent a = new Intent("com.eccproductions.networkwidget.ABOUTUS");
			startActivity(a);
			break;
		case R.id.preferences:
			// preferences
			Intent p = new Intent("com.eccproductions.networkwidget.PREFERENCES");
			startActivity(p);
			break;
		case R.id.exit:
			// exit
			finish();
			break;
		}
		return false;
	}

}
