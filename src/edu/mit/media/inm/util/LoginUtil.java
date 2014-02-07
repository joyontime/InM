package edu.mit.media.inm.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.NoteDataSource;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.http.GetIV;
import edu.mit.media.inm.prefs.PreferenceHandler;



public class LoginUtil{
	private MainActivity ctx;
	private PreferenceHandler ph;
	private String TAG = "LoginUtil";
	
	public LoginUtil(MainActivity ctx){
		this.ctx = ctx;	
		ph = new PreferenceHandler(ctx);
	}
	

	
	public void loginDialog(){
		LayoutInflater inflater = ctx.getLayoutInflater();
		final View login_view = inflater.inflate(R.layout.dialog_signin, null);
		
		AlertDialog.Builder login_dialog = new AlertDialog.Builder(ctx)
	    .setTitle(R.string.action_login)
	    .setView(login_view);
	    
	    login_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	ph.setPassword(((EditText)login_view.findViewById(R.id.login_password)).getText().toString());
	        	ph.setUsername(((EditText)login_view.findViewById(R.id.login_username)).getText().toString());
	        	pingServer();
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        }
	    }).show();
	}
	

	public void pingServer(){
		Log.d(TAG, "Starting update.");
		final GetIV iv_thread = new GetIV(0, ctx);
		iv_thread.execute();
	}
	

	public void clearAllDb(){
		ph.set_server_id("None");
		ph.set_IV(PreferenceHandler.default_IV);
		ph.set_now(0);
		ph.set_last_pinged();
    	ph.setPassword("");
    	ph.setUsername("");
		UserDataSource userdata = new UserDataSource(ctx);
		userdata.open();
		userdata.deleteAll();
		userdata.close();
		PlantDataSource plantdata = new PlantDataSource(ctx);
		plantdata.open();
		plantdata.deleteAll();
		plantdata.close();
		NoteDataSource notedata = new NoteDataSource(ctx);
		notedata.open();
		notedata.deleteAll();
		notedata.close();
	}
	
}