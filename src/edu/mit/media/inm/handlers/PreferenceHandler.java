package edu.mit.media.inm.handlers;

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
	
	public long now(){
		return prefs.getLong("now", 0);
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

	public void set_last_pinged(){
		Editor editor = prefs.edit();
		editor.putLong("last_pinged", prefs.getLong("now", 0));
		editor.apply();
	}

	public void set_now(long ts){
		Editor editor = prefs.edit();
		editor.putLong("now", ts);
		editor.apply();
	}
	
	public void setUsername(String user){
		Editor editor = prefs.edit();
		editor.putString("username_preference", user);
		editor.apply();
	}
	
	public void setPassword(String pass){
		Editor editor = prefs.edit();
		editor.putString("password_preference", pass);
		editor.apply();
	}

	public String POTD_neut(){
		return prefs.getString("potd_neut", "");
	}
	public String POTD_sad(){
		return prefs.getString("potd_sad", "");
	}
	public String POTD_happy(){
		return prefs.getString("potd_happy", "");
	}

	public void set_POTD(String neut, String happy, String sad){
		Editor editor = prefs.edit();
		editor.putString("potd_neut", neut);
		editor.putString("potd_happy", happy);
		editor.putString("potd_sad", sad);
		editor.apply();
	}
}