package edu.mit.media.inm.prefs;

import edu.mit.media.inm.http.GetIV;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;



public class PreferenceHandler{
	private SharedPreferences prefs;
	
	public static final String default_IV = "abcdef1234567890";

	public PreferenceHandler(Context ctx){
		this.prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public String username(){
		return prefs.getString("username_preference", "None");
	}

	public String password(){
		return prefs.getString("password_preference", "None");
	}

	public String IV(){
		return prefs.getString("IV_preference", default_IV);
	}
	

	public String server_id(){
		return prefs.getString("server_id_preference","None");
	}

	public boolean prompt(){
		return prefs.getBoolean("prompt_preference", true);
	}
	
	public long last_pinged(){
		return prefs.getLong("last_pinged", 0);
	}

	public void set_IV(String IV){
		Editor editor = prefs.edit();
		editor.putString("IV_preference", IV);
		editor.apply();
	}

	public void set_server_id(String server_id){
		Editor editor = prefs.edit();
		editor.putString("server_id_preference", server_id);
		editor.apply();
	}
	
	public void set_last_pinged(long ts){
		Editor editor = prefs.edit();
		editor.putLong("last_pinged", ts);
		editor.apply();
	}
}