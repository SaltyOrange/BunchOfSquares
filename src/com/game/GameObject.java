package com.game;

import android.graphics.Color;
import android.graphics.Paint;

public class GameObject {
	// TODO fix private variables
	private float x_speed;		// speed of object on x axis
	private float y_speed;		// speed of object on y axis
	private int health; 		// health of object
	private float x;				// x position of object
	private float y;				// y position of object
	private Paint main_paint; 	// main paint, contains color and size of object
	private Paint border_paint;	
	private String text;
	private int special_enemy;
	private int points;
	private int pickup;			// pickup object type (1 - base health, 2 - ship health, 3 - destroy all, 4 - fire mode)
	int border, counter;
	boolean state_bool;
	
	public GameObject(float x, float y, int border) {
		setX(x);
		setY(y);
		counter = 10;
		main_paint = new Paint();
		border_paint = new Paint();
		border_paint.setColor(Color.WHITE);
		if(border % 2 == 0) this.border = border;
		else this.border = border + 1;
	}

	public void updatePosition(){
		setX(getX() + getX_speed());
		setY(getY() + getY_speed());
	}
	
	public void updateState(int type, float height_ratio, float width_ratio, float player_x) {
		if(type == 0) {
			float hsv[] = new float[3];				
			Color.colorToHSV(main_paint.getColor(), hsv);		
			
			if(counter == 30) state_bool = true;
			if(counter == 0) state_bool = false;
			if(state_bool) {
				hsv[1] = hsv[1] - (float) 0.03;
				int color = Color.HSVToColor(hsv);
				main_paint.setColor(color);
				main_paint.setStrokeWidth(main_paint.getStrokeWidth() + (float) 0.5 * width_ratio);
				counter--;
			}else {
				hsv[1] = hsv[1] + (float) 0.03;
				int color = Color.HSVToColor(hsv);
				main_paint.setColor(color);
				main_paint.setStrokeWidth(main_paint.getStrokeWidth() - (float) 0.5 * width_ratio);
				counter++;
			}
		}
		if(type == 1) {
			if(counter == 20) {
				setX_speed(2 * width_ratio);
				state_bool = true;
			}
			if(counter == 0) {
				setX_speed(-2 * width_ratio);
				state_bool = false;
			}
			if(state_bool) counter--;
			else counter++;
		}
		if(type == 2) {
			if(counter == 20) state_bool = true;
			if(counter == 0) state_bool = false;		
			if(state_bool) {
				main_paint.setStrokeWidth(main_paint.getStrokeWidth() + 1.6f * width_ratio);
				border_paint.setStrokeWidth(border_paint.getStrokeWidth() + 2.0f * width_ratio);
				counter--;
			}else {
				main_paint.setStrokeWidth(main_paint.getStrokeWidth() - 1.6f * width_ratio);
				border_paint.setStrokeWidth(border_paint.getStrokeWidth() - 2.0f * width_ratio);
				counter++;
			}
		}
		if(type == 3) {
			if(x != player_x) x_speed = (player_x - x)/32;
		}
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health, boolean set_color) {
		this.health = health;
		if(set_color) {
			if(health <= 1) main_paint.setColor(Color.rgb(255, 192, 0));
			else if(health == 2) main_paint.setColor(Color.rgb(255, 128, 0));
			else if(health == 3) main_paint.setColor(Color.rgb(255, 64, 0));
			else if(health == 4) main_paint.setColor(Color.rgb(255, 0, 1));
		}
		
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Paint getMain_paint() {
		return main_paint;
	}

	public void setMain_paint(Paint paint) {
		this.main_paint = paint;
	}	
	
	public Paint getBorder_paint() {
		return border_paint;
	}

	public void setBorder_paint(Paint paint) {
		this.border_paint = paint;
	}	
	
	public float getSize() {
		return border_paint.getStrokeWidth();
	}
	
	public void setSize(float size) {
		border_paint.setStrokeWidth(size);
		main_paint.setStrokeWidth(size - border);
	}
	
	public int getColor() {
		return main_paint.getColor();
	}
	
	public void setColor(int color) {
		main_paint.setColor(color);
	}

	public float getX_speed() {
		return x_speed;
	}

	public void setX_speed(float x_speed) {
		this.x_speed = x_speed;
	}	

	public float getY_speed() {
		return y_speed;
	}

	public void setY_speed(float y_speed) {
		this.y_speed = y_speed;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getPickup() {
		return pickup;
	}

	public void setPickup(int pickup) {
		this.pickup = pickup;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSpecial_enemy() {
		return special_enemy;
	}

	public void setSpecial_enemy(int special_enemy) {
		this.special_enemy = special_enemy;
	}
	
}
