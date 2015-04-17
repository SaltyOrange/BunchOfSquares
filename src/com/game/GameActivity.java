package com.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity implements SensorEventListener {
	// TODO fix private variables
	public static final String PREFS_NAME = "MyPrefsFile";
	boolean key_down;
	GameEngine engine;
	private SensorManager sensorManager;
	int height, width, unit;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	static AlertDialog paused_dialog, gameover_dialog, quit_sure_dialog, restart_sure_dialog;
	BroadcastReceiver mReceiver;	
	static TextView unpause, quit1, quit2, retry, quit_yes, quit_no , restart, restart_yes, restart_no;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		Log.d("onCreate", "called");
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
		
		Display display = getWindowManager().getDefaultDisplay();	
				
		Point size = new Point();
		display.getSize(size);		
		height = size.y;
		width = size.x;					
		
		if((unit = (int) Math.round(height * 0.005)) < 2) unit = 2;
		
		createPausedDialog();
		createGameOverDialog();
		createQuitSureDialog();
		createRestartSureDialog();
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		prefs = getSharedPreferences(PREFS_NAME, 0);
		editor = prefs.edit();
		long highscore_value = prefs.getLong(getString(R.string.saved_high_score), 0);
		boolean vibrator_bool = prefs.getBoolean(getString(R.string.saved_vibration), false);
		
        engine = new GameEngine(this, handler, height, width);
        Vibrator vibrator  = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		engine.setVibrator(vibrator);
		engine.setVibrator_bool(vibrator_bool);
        engine.setHighscore_value(highscore_value);
		setContentView(engine);
		engine.onResumeEngine();
	}	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
	    float eventY = event.getY();
	    if(event.getAction() == MotionEvent.ACTION_DOWN) engine.handleTouchInput(eventX, eventY); 
	    
		return super.onTouchEvent(event);		
	}	
	
	protected static Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	if(msg.what == 1 && !gameover_dialog.isShowing() && !paused_dialog.isShowing()) gameover_dialog.show();	 
	    	if(msg.what == 2 && !gameover_dialog.isShowing() && !paused_dialog.isShowing()) paused_dialog.show();
	    }
	};
	
	@Override
	public void onBackPressed() {
		if(engine.isRunning()){
			   engine.onPauseEngine(false);
		   }
	}
		
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			engine.updatePlayerSpeed(event.values[0] * (-2.0f), 0.0f);
			// getAccelerometer(event);
		}
	}	

	/*// TODO 　　 this a shit 　　
	private void getAccelerometer(SensorEvent event) {
		
		 
		
		float[] values = event.values;		
		// Movement
	    float x = values[0];

	    long actualTime = event.timestamp;
	    if(lastUpdate == 0){
	    	start_x = x;
	    	lastUpdate = actualTime;
	    }	       
	    
		if (x > (start_x + 0.1)){
	    	lastUpdate = actualTime;
	    	engine.updatePlayerSpeed(x * (-2.0f), 0.0f);
	    	// Log.d("Left", "OK: " + x);
	    } else if (x < (start_x - 0.1)){
	    	lastUpdate = actualTime;
	    	engine.updatePlayerSpeed(x * (-2.0f), 0.0f);
	    	// Log.d("Right", "OK: " + x);
	    } else {
	    	engine.updatePlayerSpeed(0.0f, 0.0f);
	    }
	}*/
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
	
	private void createPausedDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
	    RelativeLayout dialog_layout =  new RelativeLayout(this);
	    RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9,
	    							params10, params11, params12, params13;
	    TextView title;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		View divider1, divider2, divider3, divider4, divider5, divider6, divider7, divider8, divider9;	
	    	  
	    title = new TextView(this);		
	    title.setText("Game Paused");
	    title.setTextColor(Color.GREEN);
	    title.setTypeface(typeface);
	    title.setTextSize(40);
	    title.setId(1);
	    
	    divider1 = new View(this);
		divider1.setBackgroundColor(Color.BLACK);
		divider1.setId(2);
		
		divider2 = new View(this);
		divider2.setBackgroundColor(Color.GREEN);
		divider2.setId(3);
	    
		unpause = new TextView(this);		
		unpause.setText("Unpause");
		unpause.setTextColor(Color.GREEN);
		unpause.setTypeface(typeface);
		unpause.setTextSize(40);
		unpause.setId(4);
	    
	    divider3 = new View(this);
		divider3.setBackgroundColor(Color.GREEN);
		divider3.setId(5);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.BLACK);
		divider4.setId(6);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(7);
	    
		restart = new TextView(this);		
		restart.setText("Restart");
		restart.setTextColor(Color.GREEN);
		restart.setTypeface(typeface);
		restart.setTextSize(40);
		restart.setId(8);
	    
	    divider6 = new View(this);
		divider6.setBackgroundColor(Color.GREEN);
		divider6.setId(9);
		
		divider7 = new View(this);
		divider7.setBackgroundColor(Color.BLACK);
		divider7.setId(10);
		
		divider8 = new View(this);
		divider8.setBackgroundColor(Color.GREEN);
		divider8.setId(11);
	    
	    quit1 = new TextView(this);		
	    quit1.setText("Quit");
	    quit1.setTextColor(Color.GREEN);
	    quit1.setTypeface(typeface);
	    quit1.setTextSize(40);
	    quit1.setId(12);
	    
	    divider9 = new View(this);
		divider9.setBackgroundColor(Color.GREEN);
		divider9.setId(13);
	    
	    params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4 * unit);
		params2.addRule(RelativeLayout.BELOW, title.getId());
		
		params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params3.addRule(RelativeLayout.BELOW, divider1.getId());
		
		params4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params4.addRule(RelativeLayout.BELOW, divider2.getId());
		
		params5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params5.addRule(RelativeLayout.BELOW, unpause.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params6.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, restart.getId());
		
		params10 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params10.addRule(RelativeLayout.BELOW, divider6.getId());
		
		params11 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params11.addRule(RelativeLayout.BELOW, divider7.getId());
		
		params12 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params12.addRule(RelativeLayout.BELOW, divider8.getId());
		
		params13 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params13.addRule(RelativeLayout.BELOW, quit1.getId());
		
		dialog_layout.addView(title, params1);
		dialog_layout.addView(divider1, params2);
		dialog_layout.addView(divider2, params3);
		dialog_layout.addView(unpause, params4);
		dialog_layout.addView(divider3, params5);
		dialog_layout.addView(divider4, params6);
		dialog_layout.addView(divider5, params7);		
		dialog_layout.addView(restart, params8);
		dialog_layout.addView(divider6, params9);
		dialog_layout.addView(divider7, params10);
		dialog_layout.addView(divider8, params11);		
		dialog_layout.addView(quit1, params12);
		dialog_layout.addView(divider9, params13);
		
		dialog_layout.setBackgroundColor(Color.BLACK);
		
		alertDialog.setView(dialog_layout);
		
		unpause.setOnTouchListener(listener);
		
		restart.setOnTouchListener(listener);
		
		quit1.setOnTouchListener(listener);
		
		paused_dialog = alertDialog.create();
	    
		paused_dialog.setCancelable(false);
		
		paused_dialog.setCanceledOnTouchOutside(false);
	}
	
	private void createGameOverDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
	    RelativeLayout dialog_layout =  new RelativeLayout(this);
	    RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9;
	    TextView title;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		View divider1, divider2, divider3, divider4, divider5, divider6;	
	    	  
	    title = new TextView(this);		
	    title.setText("Game Over");
	    title.setTextColor(Color.GREEN);
	    title.setTypeface(typeface);
	    title.setTextSize(40);
	    title.setId(1);
	    
	    divider1 = new View(this);
		divider1.setBackgroundColor(Color.BLACK);
		divider1.setId(2);
		
		divider2 = new View(this);
		divider2.setBackgroundColor(Color.GREEN);
		divider2.setId(3);
	    
		retry = new TextView(this);		
		retry.setText("Retry");
		retry.setTextColor(Color.GREEN);
		retry.setTypeface(typeface);
		retry.setTextSize(40);
		retry.setId(4);
	    
	    divider3 = new View(this);
		divider3.setBackgroundColor(Color.GREEN);
		divider3.setId(5);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.BLACK);
		divider4.setId(6);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(7);
	    
	    quit2 = new TextView(this);		
	    quit2.setText("Quit");
	    quit2.setTextColor(Color.GREEN);
	    quit2.setTypeface(typeface);
	    quit2.setTextSize(40);
	    quit2.setId(8);
	    
	    divider6 = new View(this);
		divider6.setBackgroundColor(Color.GREEN);
		divider6.setId(9);
	    
	    params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4 * unit);
		params2.addRule(RelativeLayout.BELOW, title.getId());
		
		params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params3.addRule(RelativeLayout.BELOW, divider1.getId());
		
		params4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params4.addRule(RelativeLayout.BELOW, divider2.getId());
		
		params5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params5.addRule(RelativeLayout.BELOW, retry.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params6.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, quit2.getId());
		
		dialog_layout.addView(title, params1);
		dialog_layout.addView(divider1, params2);
		dialog_layout.addView(divider2, params3);
		dialog_layout.addView(retry, params4);
		dialog_layout.addView(divider3, params5);
		dialog_layout.addView(divider4, params6);
		dialog_layout.addView(divider5, params7);		
		dialog_layout.addView(quit2, params8);
		dialog_layout.addView(divider6, params9);
		
		dialog_layout.setBackgroundColor(Color.BLACK);
		
		alertDialog.setView(dialog_layout);
		
		retry.setOnTouchListener(listener);
		
		quit2.setOnTouchListener(listener);
		
		gameover_dialog = alertDialog.create();
		
		gameover_dialog.setCancelable(false);
	    
		gameover_dialog.setCanceledOnTouchOutside(false);
	}
	
	private void createQuitSureDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
	    RelativeLayout dialog_layout =  new RelativeLayout(this);
	    RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9;
	    TextView title;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		View divider1, divider2, divider3, divider4, divider5, divider6;	
	    	  
	    title = new TextView(this);		
	    title.setText("You Sure?");
	    title.setTextColor(Color.GREEN);
	    title.setTypeface(typeface);
	    title.setTextSize(40);
	    title.setId(1);
	    
	    divider1 = new View(this);
		divider1.setBackgroundColor(Color.BLACK);
		divider1.setId(2);
		
		divider2 = new View(this);
		divider2.setBackgroundColor(Color.GREEN);
		divider2.setId(3);
	    
		quit_yes = new TextView(this);		
		quit_yes.setText("Yes");
		quit_yes.setTextColor(Color.GREEN);
		quit_yes.setTypeface(typeface);
		quit_yes.setTextSize(40);
		quit_yes.setId(4);
	    
	    divider3 = new View(this);
		divider3.setBackgroundColor(Color.GREEN);
		divider3.setId(5);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.BLACK);
		divider4.setId(6);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(7);
	    
		quit_no = new TextView(this);		
	    quit_no.setText("No");
	    quit_no.setTextColor(Color.GREEN);
	    quit_no.setTypeface(typeface);
	    quit_no.setTextSize(40);
	    quit_no.setId(8);
	    
	    divider6 = new View(this);
		divider6.setBackgroundColor(Color.GREEN);
		divider6.setId(9);
	    
	    params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4 * unit);
		params2.addRule(RelativeLayout.BELOW, title.getId());
		
		params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params3.addRule(RelativeLayout.BELOW, divider1.getId());
		
		params4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params4.addRule(RelativeLayout.BELOW, divider2.getId());
		
		params5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params5.addRule(RelativeLayout.BELOW, quit_yes.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params6.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, quit_no.getId());
		
		dialog_layout.addView(title, params1);
		dialog_layout.addView(divider1, params2);
		dialog_layout.addView(divider2, params3);
		dialog_layout.addView(quit_yes, params4);
		dialog_layout.addView(divider3, params5);
		dialog_layout.addView(divider4, params6);
		dialog_layout.addView(divider5, params7);		
		dialog_layout.addView(quit_no, params8);
		dialog_layout.addView(divider6, params9);
		
		dialog_layout.setBackgroundColor(Color.BLACK);
		
		alertDialog.setView(dialog_layout);
		
		quit_yes.setOnTouchListener(listener);
		
		quit_no.setOnTouchListener(listener);
		
		quit_sure_dialog = alertDialog.create();
		
		quit_sure_dialog.setCancelable(false);
	    
		quit_sure_dialog.setCanceledOnTouchOutside(false);
	}
	
	private void createRestartSureDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
	    RelativeLayout dialog_layout =  new RelativeLayout(this);
	    RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9;
	    TextView title;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		View divider1, divider2, divider3, divider4, divider5, divider6;	
	    	  
	    title = new TextView(this);		
	    title.setText("You Sure?");
	    title.setTextColor(Color.GREEN);
	    title.setTypeface(typeface);
	    title.setTextSize(40);
	    title.setId(1);
	    
	    divider1 = new View(this);
		divider1.setBackgroundColor(Color.BLACK);
		divider1.setId(2);
		
		divider2 = new View(this);
		divider2.setBackgroundColor(Color.GREEN);
		divider2.setId(3);
	    
		restart_yes = new TextView(this);		
		restart_yes.setText("Yes");
		restart_yes.setTextColor(Color.GREEN);
		restart_yes.setTypeface(typeface);
		restart_yes.setTextSize(40);
		restart_yes.setId(4);
	    
	    divider3 = new View(this);
		divider3.setBackgroundColor(Color.GREEN);
		divider3.setId(5);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.BLACK);
		divider4.setId(6);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(7);
	    
		restart_no = new TextView(this);		
		restart_no.setText("No");
		restart_no.setTextColor(Color.GREEN);
		restart_no.setTypeface(typeface);
		restart_no.setTextSize(40);
		restart_no.setId(8);
	    
	    divider6 = new View(this);
		divider6.setBackgroundColor(Color.GREEN);
		divider6.setId(9);
	    
	    params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4 * unit);
		params2.addRule(RelativeLayout.BELOW, title.getId());
		
		params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params3.addRule(RelativeLayout.BELOW, divider1.getId());
		
		params4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params4.addRule(RelativeLayout.BELOW, divider2.getId());
		
		params5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params5.addRule(RelativeLayout.BELOW, restart_yes.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params6.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, restart_no.getId());
		
		dialog_layout.addView(title, params1);
		dialog_layout.addView(divider1, params2);
		dialog_layout.addView(divider2, params3);
		dialog_layout.addView(restart_yes, params4);
		dialog_layout.addView(divider3, params5);
		dialog_layout.addView(divider4, params6);
		dialog_layout.addView(divider5, params7);		
		dialog_layout.addView(restart_no, params8);
		dialog_layout.addView(divider6, params9);
		
		dialog_layout.setBackgroundColor(Color.BLACK);
		
		alertDialog.setView(dialog_layout);
		
		restart_yes.setOnTouchListener(listener);
		
		restart_no.setOnTouchListener(listener);
		
		restart_sure_dialog = alertDialog.create();
		
		restart_sure_dialog.setCancelable(false);
	    
		restart_sure_dialog.setCanceledOnTouchOutside(false);
	}
	
	View.OnTouchListener listener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {			
			switch (event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			    	v.setBackgroundColor(Color.DKGRAY);
			        break;
			    case MotionEvent.ACTION_UP:
			    	v.setBackgroundColor(Color.TRANSPARENT);
			    	
					if(v == unpause){
						paused_dialog.dismiss();
						if(!engine.isRunning()) engine.onResumeEngine();
					}
					
					if(v == restart) restart_sure_dialog.show();
					
					if(v == retry){						
						gameover_dialog.dismiss();
						engine.newGame();
						if(!engine.isRunning()) engine.onResumeEngine();
					}
					
					if(v == quit1 || v == quit2) quit_sure_dialog.show();
					
					if(v == quit_yes){		
						if(paused_dialog.isShowing() )paused_dialog.dismiss();
						if(gameover_dialog.isShowing()) gameover_dialog.dismiss();
						if(quit_sure_dialog.isShowing()) quit_sure_dialog.dismiss();
						if(restart_sure_dialog.isShowing()) restart_sure_dialog.dismiss();
						engine.surface_holder.getSurface().release();
						GameActivity.this.finish();
					}
					
					if(v == quit_no) quit_sure_dialog.dismiss();
					
					if(v == restart_yes){		
						if(paused_dialog.isShowing() )paused_dialog.dismiss();
						if(gameover_dialog.isShowing()) gameover_dialog.dismiss();
						if(quit_sure_dialog.isShowing()) quit_sure_dialog.dismiss();
						if(restart_sure_dialog.isShowing()) restart_sure_dialog.dismiss();
						engine.newGame();
						if(!engine.isRunning()) engine.onResumeEngine();
					}
					
					if(v == restart_no) restart_sure_dialog.dismiss();
					
			        v.performClick();
			        break;
			    default:
			        break;
		    }
		    return true;
		}
	};
			
	@Override
	protected void onResume() {
		super.onResume();			
		
		// register this class as a listener for the orientation and
	    // accelerometer sensors
	    sensorManager.registerListener(GameActivity.this,
	    								sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    									SensorManager.SENSOR_DELAY_NORMAL);     	   
	        			
	}
	  
	@Override
	protected void onPause() {	
		super.onPause();
		
		editor.putLong(getString(R.string.saved_high_score), engine.getHighscore_value());
		editor.commit();
		
		// when the screen is about to turn off
        if (ScreenReceiver.wasScreenOn) {
            // this is the case when onPause() is called by the system due to a screen state change
        	if(engine.isRunning()) engine.onPauseEngine(false);    	
    		// unregister listener
    		sensorManager.unregisterListener(this);
        } else {
            // this is when onPause() is called when the screen state has not changed
        }				
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();	
		Log.d("onDestroy", "called");
		gameover_dialog.dismiss();
		paused_dialog.dismiss();
		quit_sure_dialog.dismiss();
		
		unregisterReceiver(mReceiver);
	}
		
}
