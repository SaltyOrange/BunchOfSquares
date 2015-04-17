package com.game;

import java.util.ArrayList;
import java.util.ListIterator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameEngine extends SurfaceView implements Runnable {	
	// TODO fix private variables
	private static final int PERIOD = 16;
	private static final int[][] FIRE_MODES = {{0, 0, 0, 0, 0},
												{0, 0, 0, 0, 0},
												{1, 0, -1, 0, 0},
												{1, 0, 0, -1, 0},
												{2, 1, 0, -1, -2}};
	
	private static final long[] PATTERN = {0, 50, 50, 50};	
	
	private GameObject player;
	private Vibrator vibrator;
	ArrayList<GameObject> bullets = new ArrayList<GameObject>();
	ArrayList<GameObject> temp_bullets = new ArrayList<GameObject>();
	ArrayList<GameObject> enemies = new ArrayList<GameObject>();
	ArrayList<GameObject> fragments = new ArrayList<GameObject>();
	ArrayList<GameObject> texts = new ArrayList<GameObject>();
	ArrayList<GameObject> pickups = new ArrayList<GameObject>();
	private boolean running, new_hs, vibrator_bool, sound_bool;
	private int screen_height, screen_width, border;
	private long highscore_value;
	long score;	
	float height_ratio, width_ratio;
	int godmode, base_health, fire_mode;
	Thread thread = null;
    SurfaceHolder surface_holder;
    Paint menu_paint = new Paint();
    Paint text_paint = new Paint();
    Typeface typeface;
    Handler mHandler;    
    	
	public GameEngine(Context context, Handler handler, int screen_height, int screen_width) {
		super(context);	
		
		mHandler = handler;
		
		setScreen_height(screen_height);
		setScreen_width(screen_width);
		height_ratio = getScreen_height() / 480;
		width_ratio = getScreen_width() / 320;
				
		newGame();
		
		surface_holder = getHolder();
		
		typeface = Typeface.createFromAsset(context.getAssets(), "UQ_0.ttf");
		
		text_paint.setColor(Color.GREEN);
		text_paint.setStrokeWidth(4 * border);		
		text_paint.setTypeface(typeface);
		text_paint.setTextSize(10 * border);

		menu_paint.setColor(Color.GREEN);
		menu_paint.setStrokeWidth(border);
		
		Log.d("constructor", "OK");   
	}
		
	public void onResumeEngine(){
		running = true;
		Log.d("onResume", "Running: " + running);
		thread = new Thread(this);
		thread.start();
	}
		   
	public void onPauseEngine(boolean gameover){
		boolean retry = true;
		running = false;
		Log.d("onPause", "Running: " + running);
		if(gameover) mHandler.sendEmptyMessage(1);
		else mHandler.sendEmptyMessage(2);
		while(retry){
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		long start_time, sleep_time;
		
		while(running){
			start_time = System.currentTimeMillis();
			if(Math.random() < 0.02f + score / 10000) createEnemy();
			//enemyGenerator();
			updatePositions();
			checkCollisions();
			drawGame();	
			
			sleep_time = PERIOD - (System.currentTimeMillis() - start_time);
            try {
            	if (sleep_time > 0)  SystemClock.sleep(sleep_time);
            }catch (Exception e) {}
            
		}
	}
	
	public void newGame(){
		createPlayer();
		bullets.clear();
		temp_bullets.clear();
		enemies.clear();
		fragments.clear();
		texts.clear();
		pickups.clear();
		score = 0;
		godmode = 0;
		fire_mode = 1;
		base_health = 100;
		new_hs = false;
	}
	
	public boolean handleTouchInput(float x, float y){
		createPlayerBullet();
		if(vibrator_bool) vibrator.vibrate(20);
		return true;
	}
	
	/*private void enemyGenerator() {
		int iter;
		if(enemies.size() < (iter = Math.round(score / 4) + 5)) {
			iter = iter - enemies.size();
			while(iter > 0) {
				if(Math.random() < 0.01f) createEnemy();
				iter--;
			}
		}
	}*/
		
	private void updatePositions(){
		if(player.getHealth() <= 0 || base_health <= 0){
			createFragments(player.getX(), player.getY());	
			if(vibrator_bool) vibrator.vibrate(250);
	    	onPauseEngine(true);
		}else{
			if(player.getX() + player.getX_speed() > (0 + player.getSize() * 0.5 + border) && 
				player.getX() + player.getX_speed() < (getScreen_width() - player.getSize() * 0.5 - border)) 
				player.updatePosition();													// update player position
		}			
		
		bullets.addAll(temp_bullets);
		temp_bullets.clear();
		for(ListIterator<GameObject> i = bullets.listIterator(); i.hasNext(); ) {			// *
		    GameObject bullet = i.next();													// **
		    if((bullet.getY() + bullet.getSize() * 0.5) < 0 								// ***	
		    	|| (bullet.getY() - bullet.getSize() * 0.5) > screen_height
		    	|| bullet.getHealth() <= 0) i.remove();										// update positions of bullets		    
		    else bullet.updatePosition();													// *
		}
		
		for(ListIterator<GameObject> i = enemies.listIterator(); i.hasNext(); ) {				// *
		    GameObject enemy = i.next();														// **
		    if((enemy.getY() - enemy.getSize() * 0.5) > screen_height) {
		    	base_health -= enemy.getPoints() * 2;
		    	i.remove();																		// update positions of enemies		    	
		    }else if(enemy.getHealth() <= 0) {
		    	if(enemy.getHealth() < -9) {
		    		createFragments(enemy.getX(), enemy.getY());		    	
			    	score = score + enemy.getPoints();
			    	createText(enemy.getX(), enemy.getY(), "+" + enemy.getPoints(), false);
			    	i.remove();	
		    	}else{
		    		createFragments(enemy.getX(), enemy.getY());		    	
			    	score = score + enemy.getPoints();
			    	createText(enemy.getX(), enemy.getY(), "+" + enemy.getPoints(), false);
			    	if(Math.random() < 0.1) createPickup(enemy.getX(), enemy.getY());
			    	i.remove();	
		    	}		    	
		    }else {
		    	if(enemy.getSpecial_enemy() > 0) 
		    		enemy.updateState(enemy.getSpecial_enemy(), height_ratio, width_ratio, player.getX());
		    	enemy.updatePosition();
		    	if(Math.random() < 0.001) createEnemyBullet(enemy.getX(), enemy.getY(), enemy.getSize());
		    }
		    
		}
		
		for(ListIterator<GameObject> i = fragments.listIterator(); i.hasNext(); ) {
			GameObject fragment = i.next();
			if(fragment.getHealth() <= 0) i.remove();
			else{
				fragment.updatePosition();
				fragment.setHealth(fragment.getHealth() - 1, false);
			}
		}
		
		for(ListIterator<GameObject> i = texts.listIterator(); i.hasNext(); ) {
			GameObject point = i.next();
			if(point.getHealth() <= 0) i.remove();
			else{
				point.updatePosition();
				point.setHealth(point.getHealth() - 1, false);
			}
		}
		
		for(ListIterator<GameObject> i = pickups.listIterator(); i.hasNext(); ) {
			GameObject pickup = i.next();
			if(pickup.getHealth() <= 0 || pickup.getY() > pickup.getSize() * 0.5 + screen_height) i.remove();
			else {
				pickup.updateState(0, height_ratio, width_ratio, player.getX());
				pickup.updatePosition();
			}
		}
		
	}
	
	private void checkCollisions(){
		ListIterator<GameObject> i, j;
		GameObject current_bullet = null, check_bullet, enemy;
		boolean bullet_bool = true, player_bool = false;
		
		mainloop: for(i = bullets.listIterator(); bullet_bool; ) {
			bullet_bool = i.hasNext();
			if(bullet_bool){
				current_bullet = i.next();
				j = bullets.listIterator(i.nextIndex());
				while(j.hasNext()) {					
					check_bullet = j.next();
				    if(isCollision(current_bullet, check_bullet)){
				    	
				    	current_bullet.setHealth(current_bullet.getHealth() - 1, false);
				    	check_bullet.setHealth(check_bullet.getHealth() - 1, false);
				    	continue mainloop;			    	
				    }	 
				}
			}
			
			for(j = enemies.listIterator(); j.hasNext(); ){
				enemy = j.next();
				if(bullet_bool){
					if(isCollision(current_bullet, enemy) && current_bullet.getY_speed() < 0) {						
						
						current_bullet.setHealth(current_bullet.getHealth() - 1, false);
						enemy.setHealth(enemy.getHealth() - 1, true);					
						
						continue mainloop;
					}
				}
				
				if(isCollision(enemy, player) && !player_bool && godmode <= 0){		// player collision and damage
					
					player.setHealth(player.getHealth() - 1, false);
					createFragments(enemy.getX(), enemy.getY());					
					if(vibrator_bool) vibrator.vibrate(PATTERN, -1);
					j.remove();
					godmode = 100;
					Log.d("player", "health: " + player.getHealth());
				}
			}
			player_bool = true;
			if(bullet_bool){
				if(isCollision(current_bullet, player) && godmode <= 0){			// player collision and damage
					
					player.setHealth(player.getHealth() - 1, false);
					current_bullet.setHealth(current_bullet.getHealth() - 1, false);
					if(vibrator_bool) vibrator.vibrate(PATTERN, -1);
					godmode = 100;
					Log.d("player", "health: " + player.getHealth());
				}
			}
		}
		
		int text_position = screen_height + 8 * border;
		for(i = pickups.listIterator(); i.hasNext(); ) {
			GameObject pickup = i.next();			
			if(isCollision(pickup, player)){				
				pickup.setHealth(0, false);
				createText(0, text_position, pickup.getText(), true);
				handlePickup(pickup.getPickup());
			}
		}
		
	}
	
	private boolean isCollision(GameObject object1, GameObject object2){
		return (Math.abs(object1.getX() - object2.getX()) < ((object1.getSize() + object2.getSize()) * 0.5)
		    	&& Math.abs(object1.getY() - object2.getY()) < ((object1.getSize() + object2.getSize()) * 0.5));
	}
	
	private void drawGame(){
		int ship_health_x = getScreen_width() - 4 * border;
		int base_health_x = ship_health_x + 2 * border;
		
		if(surface_holder.getSurface().isValid()){
			Canvas canvas = surface_holder.lockCanvas();
			//... actual drawing on canvas
			canvas.drawColor(Color.BLACK);
			
			if(godmode <= 0) drawObject(canvas, player);
			else if(godmode % 10 >= 5 && godmode % 10 < 10 && player.getHealth() > 0){
				drawObject(canvas, player);
				godmode--;
			}else if(godmode % 10 >= 0 && godmode % 10 < 5 && player.getHealth() > 0) godmode--;
				
			for(ListIterator<GameObject> i = enemies.listIterator(); i.hasNext(); ) {		// *
			    GameObject enemy = i.next();												// draw enemies
			    drawObject(canvas, enemy);													// *		
			}
			
			for(ListIterator<GameObject> i = bullets.listIterator(); i.hasNext(); ) {		// *
			    GameObject bullet = i.next();												// draw bullets
			    drawObject(canvas, bullet);													// *		
			}
			
			for(ListIterator<GameObject> i = fragments.listIterator(); i.hasNext(); ) {		// *
			    GameObject fragment = i.next();												// draw fragments
			    drawObject(canvas, fragment);												// *		
			}
			
			for(ListIterator<GameObject> i = texts.listIterator(); i.hasNext(); ) {				// *
			    GameObject text = i.next();														// draw texts
			   	canvas.drawText(text.getText(), text.getX(), text.getY(), text_paint);			// *		
			}
			
			for(ListIterator<GameObject> i = pickups.listIterator(); i.hasNext(); ) {				// *
			    GameObject pickup = i.next();														// draw pickups
			    drawObject(canvas, pickup);															// *			
			}		
						
			if(score > highscore_value && !new_hs){
				int text_position = screen_height + 8 * border;
				highscore_value = score;
				createText(0, text_position, "New Highscore!", true);
				new_hs = true;
			}else if(score > highscore_value) highscore_value = score;
			
			canvas.drawText("" + score, border, 6 * border, text_paint);				
													
			for(int i=0; i < base_health / 4; i++) {
				canvas.drawPoint(base_health_x, 2 * border, menu_paint);
				canvas.drawPoint(base_health_x, 3 * border, menu_paint);
				canvas.drawPoint(base_health_x, 4 * border, menu_paint);
				canvas.drawPoint(base_health_x, 5 * border, menu_paint);
				
				base_health_x -= border;
			}
			
			for(int i=0; i < player.getHealth(); i++){
				canvas.drawPoint(ship_health_x, 9 * border, text_paint);
				ship_health_x -= 6 * border;
			}		
 	      
			surface_holder.unlockCanvasAndPost(canvas);
		}
	}
	
	private void drawObject(Canvas canvas, GameObject object){
		canvas.drawPoint(object.getX(), object.getY(), object.getBorder_paint());
		canvas.drawPoint(object.getX(), object.getY(), object.getMain_paint());
	}
		
	private void createPlayer(){
		int start_x = (int) Math.round(getScreen_width() * 0.5);		
		border = (int) Math.round(getScreen_width() * 0.01);		
		int player_size = (int) Math.round(getScreen_width() * 0.1);
		int start_y = (int) Math.round(getScreen_height() - (player_size * 0.5 + border));
		
		player = new GameObject(start_x, start_y, border);		
		player.setHealth(3, false);
		player.setX_speed(0.0f);
		player.setY_speed(0.0f);
		player.setColor(Color.GREEN);
		player.setSize(player_size);
	}
	
	private void createPlayerBullet(){
		int start_x = (int) Math.round(player.getX() + player.getSize() * 0.5);
		int start_y = (int) Math.round(player.getY() - (player.getSize() * 0.5 + 1));
		int bullet_size = (int) Math.round(getScreen_width() * 0.01);
	    
		int x_speed;
		
		GameObject bullet;
		int offset = (int) Math.round(player.getSize() / (fire_mode + 1));
		
		for(int i=1; i<=fire_mode; i++) {
			bullet = new GameObject(start_x - i * offset, start_y, 0);
			bullet.setHealth(1, false);
			x_speed = FIRE_MODES[fire_mode - 1][i - 1];
			bullet.setX_speed(Math.round(x_speed * width_ratio));
			bullet.setY_speed((int) Math.round(-1 * (Math.sqrt((4*4) - (x_speed * x_speed))) * height_ratio));
			bullet.setColor(Color.WHITE);
			bullet.setSize(bullet_size);
			temp_bullets.add(bullet);
		}
		
	}
	
	private void createEnemyBullet(float start_x, float start_y, float size){
		int bullet_size = (int) Math.round(getScreen_width() * 0.01);
		start_y = (int) Math.round(start_y + size * 0.5);
		
		GameObject bullet = new GameObject(start_x, start_y, 0);
		bullet.setHealth(1, false);
		bullet.setX_speed(0);
		bullet.setY_speed(Math.round(5 * height_ratio));
		bullet.setColor(Color.WHITE);
		bullet.setSize(bullet_size);
		temp_bullets.add(bullet);
		
	}
	
	private void createEnemy() {
		double rand = Math.random();
		
		if(rand < 0.2f) createSmallEnemy();
		else if(rand < 0.4f) createMediumEnemy();
		else if(rand < 0.6f) createLargeEnemy();
		else if(rand < 0.7f) createSmallAgileEnemy();
		else if(rand < 0.8f) createSmallGrowingEnemy();
		else createMediumSuicideEnemy();
	}
	
	private void createSmallEnemy() {
		int enemy_size = (int) Math.round(16 * width_ratio);
		float start_x = Math.round((Math.random() * (getScreen_width() - enemy_size - 2 * border)) + enemy_size * 0.5 + border);
		float start_y = Math.round(0 - (enemy_size * 0.5 + 1));		
		
		GameObject enemy = new GameObject(start_x, start_y, border);
		enemy.setHealth((int) Math.round(Math.random() * 3 + 1), true);
		enemy.setX_speed(0.0f);
		enemy.setY_speed((float) ((1 + score / 100) + Math.random() * (score / 50)) * height_ratio);
		enemy.setSize(enemy_size);
		enemy.setPoints(3);
		enemy.setSpecial_enemy(0);
		enemies.add(enemy);
	}
	
	private void createMediumEnemy() {
		int enemy_size = (int) Math.round(32 * width_ratio);
		float start_x = Math.round((Math.random() * (getScreen_width() - enemy_size - 2 * border)) + enemy_size * 0.5 + border);
		float start_y = Math.round(0 - (enemy_size * 0.5 + 1));		
		
		GameObject enemy = new GameObject(start_x, start_y, border);
		enemy.setHealth((int) Math.round(Math.random() * 3 + 1), true);
		enemy.setX_speed(0.0f);
		enemy.setY_speed((float) ((1 + score / 100) + Math.random() * (score / 50)) * height_ratio);
		enemy.setSize(enemy_size);
		enemy.setPoints(2);
		enemy.setSpecial_enemy(0);
		enemies.add(enemy);
	}
	
	private void createLargeEnemy() {
		int enemy_size = (int) Math.round(48 * width_ratio);
		float start_x = Math.round((Math.random() * (getScreen_width() - enemy_size - 2 * border)) + enemy_size * 0.5 + border);
		float start_y = Math.round(0 - (enemy_size * 0.5 + 1));		
		
		GameObject enemy = new GameObject(start_x, start_y, border);
		enemy.setHealth((int) Math.round(Math.random() * 3 + 1), true);
		enemy.setX_speed(0.0f);
		enemy.setY_speed((float) ((1 + score / 100) + Math.random() * (score / 50)) * height_ratio);
		enemy.setSize(enemy_size);
		enemy.setPoints(1);
		enemy.setSpecial_enemy(0);
		enemies.add(enemy);
	}
	
	private void createSmallAgileEnemy() {
		int enemy_size = (int) Math.round(16 * width_ratio);
		float start_x = Math.round((Math.random() * (getScreen_width() - enemy_size - 2 * border - 80)) + enemy_size * 0.5 + border + 40 );
		float start_y = Math.round(0 - (enemy_size * 0.5 + 1));		
		
		GameObject enemy = new GameObject(start_x, start_y, border);
		enemy.setHealth((int) Math.round(Math.random() * 3 + 1), true);
		enemy.setX_speed(0.0f);
		enemy.setY_speed((float) ((1 + score / 100) + Math.random() * (score / 50)) * height_ratio);
		enemy.setSize(enemy_size);
		enemy.setPoints(3);
		enemy.setSpecial_enemy(1);
		enemies.add(enemy);
	}
	
	private void createSmallGrowingEnemy() {
		int enemy_size = (int) Math.round(16 * width_ratio);
		float start_x = Math.round((Math.random() * (getScreen_width() - enemy_size - 2 * border - 80)) + enemy_size * 0.5 + border + 40 );
		float start_y = Math.round(0 - (enemy_size * 0.5 + 1));		
		
		GameObject enemy = new GameObject(start_x, start_y, border);
		enemy.setHealth((int) Math.round(Math.random() * 3 + 1), true);
		enemy.setX_speed(0.0f);
		enemy.setY_speed((float) ((1 + score / 100) + Math.random() * (score / 50)) * height_ratio);
		enemy.setSize(enemy_size);
		enemy.setPoints(2);
		enemy.setSpecial_enemy(2);
		enemies.add(enemy);
	}
	
	private void createMediumSuicideEnemy() {
		int enemy_size = (int) Math.round(32 * width_ratio);
		float start_x = Math.round((Math.random() * (getScreen_width() - enemy_size - 2 * border - 80)) + enemy_size * 0.5 + border + 40 );
		float start_y = Math.round(0 - (enemy_size * 0.5 + 1));		
		
		GameObject enemy = new GameObject(start_x, start_y, border);
		enemy.setHealth((int) Math.round(Math.random() * 3 + 1), true);
		enemy.setX_speed(0.0f);
		enemy.setY_speed((float) ((1 + score / 200) + Math.random() * (score / 100)) * height_ratio);
		enemy.setSize(enemy_size);
		enemy.setPoints(2);
		enemy.setSpecial_enemy(3);
		enemies.add(enemy);
	}
	
	private void createFragments(float start_x, float start_y) {
		int fragment_size = (int) Math.round(2 * width_ratio);
		
		for(int iter=0; iter<5; iter++){
			GameObject fragment = new GameObject(start_x, start_y, 0);			
			float x_speed = (float) ((Math.random() * 8) - 4) * width_ratio;
			
			fragment.setX_speed(x_speed);
			fragment.setY_speed(Math.round((Math.sqrt((4*4) - (x_speed * x_speed))) * height_ratio));
			fragment.setHealth(20, false);
			fragment.setColor(Color.argb(224, 255, 255, 0));
			fragment.setSize(fragment_size);
			fragments.add(fragment);
    	}
    	for(int iter=0; iter<5; iter++){
    		GameObject fragment = new GameObject(start_x, start_y, 0);
    		fragment.setHealth(20, false);
    		float x_speed = (float) ((Math.random() * 8) - 4) * width_ratio;
    		
    		fragment.setX_speed(x_speed);
    		fragment.setY_speed(Math.round((-1 * Math.sqrt((4*4) - (x_speed * x_speed))) * height_ratio));
    		fragment.setColor(Color.argb(224, 255, 255, 0));
    		fragment.setSize(fragment_size);
    		fragments.add(fragment);
    	}
    	
	}
	
	private void createText(float start_x, float start_y, String text_value, boolean special) {
		// special for pickup and highscore text
		
		GameObject text = new GameObject(start_x, start_y, 0);
		text.setHealth(20, false);
		if(special) text.setHealth(150, false);
		text.setX_speed(0.0f);
		text.setY_speed(-4 * height_ratio);
		text.setText(text_value);
		
		
		texts.add(text);
	}
	
	private void createPickup(float start_x, float start_y) {
		int pickup_size = (int) Math.round(10 * width_ratio);
		int pickup_value = (int) Math.round(Math.random() * 3 + 1);
		
		GameObject pickup = new GameObject(start_x, start_y, 0);
		pickup.setX_speed(0);
		pickup.setY_speed(4 * height_ratio);
		
		pickup.setPickup(pickup_value);
		
		switch (pickup_value) {
			case 1:
				pickup.setText("Base Health +10");
				break;
			case 2:
				pickup.setText("Player Health +1");
				break;
			case 3:
				pickup.setText("BOOM!!!");
				break;
			case 4:
				pickup.setText("PEW PEW!!!");
				break;
	
			default:
				break;
		}
		
		pickup.setHealth(1, false);
		pickup.setColor(Color.BLUE);
		pickup.setSize(pickup_size);
		
		pickups.add(pickup);
	}
	
	private void handlePickup(int pickup) {
		switch (pickup) {
			case 1:
				if(base_health < 100) {				
					if(base_health > 90) base_health = 100;
					else base_health += 10;
				}
				break;
			case 2:
				if(player.getHealth() < 3) player.setHealth(player.getHealth() + 1, false);
				break;
			case 3:
				for(ListIterator<GameObject> i = enemies.listIterator(); i.hasNext(); ){
					GameObject enemy = i.next();
					enemy.setHealth(-10, true);
				}
				break;
			case 4:
				if(fire_mode < 5) fire_mode++;
				break;
	
			default:
				break;
		}
	}
	
	public void updatePlayerSpeed(float x_speed, float y_speed){		 
		player.setX_speed(Math.round(x_speed * width_ratio));
		player.setY_speed(Math.round(y_speed * height_ratio));
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public GameObject getPlayer() {
		return player;
	}

	public void setPlayer(GameObject player) {
		this.player = player;
	}

	public int getScreen_height() {
		return screen_height;
	}

	public void setScreen_height(int screen_height) {
		this.screen_height = screen_height;
	}

	public int getScreen_width() {
		return screen_width;
	}

	public void setScreen_width(int screen_width) {
		this.screen_width = screen_width;
	}

	public long getHighscore_value() {
		return highscore_value;
	}

	public void setHighscore_value(long highscore_value) {
		this.highscore_value = highscore_value;
	}

	public Vibrator getVibrator() {
		return vibrator;
	}

	public void setVibrator(Vibrator vibrator) {
		this.vibrator = vibrator;
	}

	public boolean isVibrator_bool() {
		return vibrator_bool;
	}

	public void setVibrator_bool(boolean vibrator_bool) {
		this.vibrator_bool = vibrator_bool;
	}

	public boolean isSound_bool() {
		return sound_bool;
	}

	public void setSound_bool(boolean sound_bool) {
		this.sound_bool = sound_bool;
	}
		
}
