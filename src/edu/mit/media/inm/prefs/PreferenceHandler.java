package edu.mit.media.inm.prefs;

import edu.mit.media.inm.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;



public class PreferenceHandler{
	private SharedPreferences prefs;
	private Context ctx;

	public PreferenceHandler(Context ctx){
		this.ctx = ctx;
		this.prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public String username(){
		return prefs.getString("username_preference", "None");
	}

	public String password(){
		return prefs.getString("password_preference", "None");
	}
	
	public String IV(){
		return prefs.getString("IV_preference", "abcdef1234567890");
	}

	public boolean prompt(){
		return prefs.getBoolean("prompt_preference", true);
	}

	public long last_pinged(){
		return prefs.getLong("last_pinged", 0);
	}
	
	public void update_last_pinged(long ts){
		Editor editor = prefs.edit();
		editor.putLong("last_pinged", ts);
		editor.apply();
	}
}