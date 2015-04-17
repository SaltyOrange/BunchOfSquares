package com.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	Intent intent;
	int height, width, unit;
	long highscore_value;
	boolean sound_bool, vibraton_bool;
	SharedPreferences prefs;
	TextView highscore, start, options, sound, vibration, hs_reset, hs_yes, hs_no, quit, quit_yes, quit_no;
	AlertDialog options_dialog, hsreset_dialog, sure_dialog;
	SharedPreferences.Editor editor;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("menu onCreate", "ok");
		
		RelativeLayout main_layout;
		RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9,
									params10, params11, params12, params13, layout_params;
		TextView game_name;
		View divider1, divider2, divider3, divider4, divider5, divider6, divider7, divider8;	
		Typeface typeface;
		
		Display display = getWindowManager().getDefaultDisplay();
		intent = new Intent(this, GameActivity.class);
		
		prefs = getSharedPreferences(PREFS_NAME, 0);	
		editor = prefs.edit();			
		
		Point size = new Point();
		display.getSize(size);		
		height = size.y;
		width = size.x;
			
		
		if((unit = (int) Math.round(height * 0.005)) < 2) unit = 2;
		
		typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		
		createOptionsDialog();
		createHSResetDialog();
		createSureDialog();
								
		game_name = new TextView(this);
		game_name.setText("A\nBunch\nof\nSquares");
		game_name.setTextColor(Color.GREEN);
		game_name.setTypeface(typeface);
		game_name.setTextSize(75);
		game_name.setId(1);	
				
		highscore = new TextView(this);		
		highscore.setTextColor(Color.GREEN);
		highscore.setTypeface(typeface);
		highscore.setTextSize(30);
		highscore.setId(2);
		
		divider1 = new View(this);
		divider1.setBackgroundColor(Color.GREEN);
		divider1.setId(3);
		
		start = new TextView(this);	
		start.setText("Start Game");
		start.setTextColor(Color.GREEN);
		start.setTypeface(typeface);
		start.setTextSize(40);		
		start.setId(4);		

		divider2 = new View(this);
		divider2.setBackgroundColor(Color.GREEN);
		divider2.setId(5);	
		
		divider3 = new View(this);
		divider3.setBackgroundColor(Color.TRANSPARENT);
		divider3.setId(6);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.GREEN);
		divider4.setId(7);	
		
		options = new TextView(this);		
		options.setText("Options");
		options.setTextColor(Color.GREEN);
		options.setTypeface(typeface);
		options.setTextSize(40);		
		options.setId(8);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(9);	
		
		divider6 = new View(this);
		divider6.setBackgroundColor(Color.TRANSPARENT);
		divider6.setId(10);
		
		divider7 = new View(this);
		divider7.setBackgroundColor(Color.GREEN);
		divider7.setId(11);	
		
		quit = new TextView(this);		
		quit.setText("Quit");
		quit.setTextColor(Color.GREEN);
		quit.setTypeface(typeface);
		quit.setTextSize(40);		
		quit.setId(12);
		
		divider8 = new View(this);
		divider8.setBackgroundColor(Color.GREEN);
		divider8.setId(13);	
		
		params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params2.addRule(RelativeLayout.BELOW, game_name.getId());
		
		params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params3.addRule(RelativeLayout.CENTER_VERTICAL);
		
		params4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params4.addRule(RelativeLayout.BELOW, divider1.getId());
		
		params5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params5.addRule(RelativeLayout.BELOW, start.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4 * unit);
		params6.addRule(RelativeLayout.BELOW, divider2.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, options.getId());
		
		params10 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4 * unit);
		params10.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params11 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params11.addRule(RelativeLayout.BELOW, divider6.getId());
		
		params12 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params12.addRule(RelativeLayout.BELOW, divider7.getId());
		
		params13 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params13.addRule(RelativeLayout.BELOW, quit.getId());
		
		main_layout = new RelativeLayout(this);
		
		layout_params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		main_layout.setLayoutParams(layout_params);
		
		main_layout.addView(game_name, params1);		
		main_layout.addView(highscore, params2);
		main_layout.addView(divider1, params3);
		main_layout.addView(start, params4);
		main_layout.addView(divider2, params5);
		main_layout.addView(divider3, params6);
		main_layout.addView(divider4, params7);
		main_layout.addView(options, params8);
		main_layout.addView(divider5, params9);
		main_layout.addView(divider6, params10);
		main_layout.addView(divider7, params11);
		main_layout.addView(quit, params12);
		main_layout.addView(divider8, params13);
		
		start.setOnTouchListener(listener);
		options.setOnTouchListener(listener);
		quit.setOnTouchListener(listener);
		
		setContentView(main_layout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();			
		
		highscore.setText("HighScore: " + prefs.getLong(getString(R.string.saved_high_score), 0));  
	}
	
	private void createOptionsDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
	    RelativeLayout dialog_layout =  new RelativeLayout(this);
	    RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9,
	    							params10, params11, params12, params13;
	    TextView title;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		View divider1, divider2, divider3, divider4, divider5, divider6, divider7, divider8, divider9;	
	    	  
	    title = new TextView(this);		
	    title.setText("Options");
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
	    
		sound = new TextView(this);		
		if(prefs.getBoolean(getString(R.string.saved_sound), true)) sound.setText("Sound is On");
		else sound.setText("Sound is Off");
		sound.setTextColor(Color.GREEN);
		sound.setTypeface(typeface);
		sound.setTextSize(40);
		sound.setId(4);
	    
	    divider3 = new View(this);
		divider3.setBackgroundColor(Color.GREEN);
		divider3.setId(5);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.BLACK);
		divider4.setId(6);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(7);
	    
		vibration = new TextView(this);		
		if(vibraton_bool = prefs.getBoolean(getString(R.string.saved_vibration), true)) vibration.setText("Vibration is On");
		else vibration.setText("Vibration is Off");
		vibration.setTextColor(Color.GREEN);
		vibration.setTypeface(typeface);
		vibration.setTextSize(40);
		vibration.setId(8);
	    
	    divider6 = new View(this);
		divider6.setBackgroundColor(Color.GREEN);
		divider6.setId(9);
		
		divider7 = new View(this);
		divider7.setBackgroundColor(Color.BLACK);
		divider7.setId(10);
		
		divider8 = new View(this);
		divider8.setBackgroundColor(Color.GREEN);
		divider8.setId(11);
	    
	    hs_reset = new TextView(this);		
	    hs_reset.setText("Reset HighScore");
	    hs_reset.setTextColor(Color.GREEN);
	    hs_reset.setTypeface(typeface);
	    hs_reset.setTextSize(40);
	    hs_reset.setId(12);
	    
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
		params5.addRule(RelativeLayout.BELOW, sound.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params6.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, vibration.getId());
		
		params10 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params10.addRule(RelativeLayout.BELOW, divider6.getId());
		
		params11 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params11.addRule(RelativeLayout.BELOW, divider7.getId());
		
		params12 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params12.addRule(RelativeLayout.BELOW, divider8.getId());
		
		params13 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params13.addRule(RelativeLayout.BELOW, hs_reset.getId());
		
		dialog_layout.addView(title, params1);
		dialog_layout.addView(divider1, params2);
		dialog_layout.addView(divider2, params3);
		dialog_layout.addView(sound, params4);
		dialog_layout.addView(divider3, params5);
		dialog_layout.addView(divider4, params6);
		dialog_layout.addView(divider5, params7);		
		dialog_layout.addView(vibration, params8);
		dialog_layout.addView(divider6, params9);
		dialog_layout.addView(divider7, params10);
		dialog_layout.addView(divider8, params11);		
		dialog_layout.addView(hs_reset, params12);
		dialog_layout.addView(divider9, params13);
		
		dialog_layout.setBackgroundColor(Color.BLACK);
		
		alertDialog.setView(dialog_layout);
		
		sound.setOnTouchListener(listener);
		
		vibration.setOnTouchListener(listener);
		
		hs_reset.setOnTouchListener(listener);
		
		options_dialog = alertDialog.create();
	}
	
	private void createHSResetDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
	    RelativeLayout dialog_layout =  new RelativeLayout(this);
	    RelativeLayout.LayoutParams params1, params2, params3, params4, params5, params6, params7, params8, params9;
	    TextView title;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "UQ_0.ttf");
		View divider1, divider2, divider3, divider4, divider5, divider6;	
	    	  
	    title = new TextView(this);		
	    title.setText("Reset HighScore?");
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
	    
	    hs_yes = new TextView(this);		
	    hs_yes.setText("Yes");
	    hs_yes.setTextColor(Color.GREEN);
	    hs_yes.setTypeface(typeface);
	    hs_yes.setTextSize(40);
	    hs_yes.setId(4);
	    
	    divider3 = new View(this);
		divider3.setBackgroundColor(Color.GREEN);
		divider3.setId(5);
		
		divider4 = new View(this);
		divider4.setBackgroundColor(Color.BLACK);
		divider4.setId(6);
		
		divider5 = new View(this);
		divider5.setBackgroundColor(Color.GREEN);
		divider5.setId(7);
	    
	    hs_no = new TextView(this);		
	    hs_no.setText("No");
	    hs_no.setTextColor(Color.GREEN);
	    hs_no.setTypeface(typeface);
	    hs_no.setTextSize(40);
	    hs_no.setId(8);
	    
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
		params5.addRule(RelativeLayout.BELOW, hs_yes.getId());
		
		params6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2 * unit);
		params6.addRule(RelativeLayout.BELOW, divider3.getId());
		
		params7 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params7.addRule(RelativeLayout.BELOW, divider4.getId());
		
		params8 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.BELOW, divider5.getId());
		
		params9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, unit);
		params9.addRule(RelativeLayout.BELOW, hs_no.getId());
		
		dialog_layout.addView(title, params1);
		dialog_layout.addView(divider1, params2);
		dialog_layout.addView(divider2, params3);
		dialog_layout.addView(hs_yes, params4);
		dialog_layout.addView(divider3, params5);
		dialog_layout.addView(divider4, params6);
		dialog_layout.addView(divider5, params7);		
		dialog_layout.addView(hs_no, params8);
		dialog_layout.addView(divider6, params9);
		
		dialog_layout.setBackgroundColor(Color.BLACK);
		
		alertDialog.setView(dialog_layout);
		
		hs_yes.setOnTouchListener(listener);
		
		hs_no.setOnTouchListener(listener);
		
	    hsreset_dialog = alertDialog.create();
	    
	    hsreset_dialog.setCanceledOnTouchOutside(true);
	}
	
	private void createSureDialog(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this, AlertDialog.THEME_HOLO_DARK);
	    
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
		
		sure_dialog = alertDialog.create();
	    
		sure_dialog.setCanceledOnTouchOutside(false);
	}	
		
	@Override
	public void onBackPressed() {
		if(sure_dialog.isShowing()){
			   sure_dialog.dismiss();
		   }else {
			   sure_dialog.show();
		   }
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
					if(v == start) MenuActivity.this.startActivity(intent);
					if(v == options) options_dialog.show();					
					if(v == sound) {
						boolean temp = !prefs.getBoolean(getString(R.string.saved_sound), true);
						editor.putBoolean(getString(R.string.saved_sound), temp);
						editor.commit();
						if(temp) sound.setText("Sound is On");
						else sound.setText("Sound is Off");
					}
					if(v == vibration) {
						boolean temp = !prefs.getBoolean(getString(R.string.saved_vibration), true);
						editor.putBoolean(getString(R.string.saved_vibration), temp);
						editor.commit();
						if(temp) vibration.setText("Vibration is On");
						else vibration.setText("Vibration is Off");
					}
					if(v == hs_reset) hsreset_dialog.show();
					if(v == hs_yes) {   
			        	editor.putLong(getString(R.string.saved_high_score), 0);
			    		editor.commit();
			    		highscore.setText("HighScore: " + 0);   
			    		hsreset_dialog.dismiss();
			        }
					if(v == hs_no) hsreset_dialog.dismiss();
					if(v == quit) sure_dialog.show();
					if(v == quit_yes) { 
			    		sure_dialog.dismiss();
			    		MenuActivity.this.finish();
			        }
					if(v == quit_no)sure_dialog.dismiss();
			        v.performClick();
			        break;
			    default:
			        break;
		    }
		    return true;
		}
	};
}
