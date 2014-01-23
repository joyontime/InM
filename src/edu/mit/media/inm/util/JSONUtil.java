package edu.mit.media.inm.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class JSONUtil {
	private static String TAG = "JSONUtil";
	private JSONObject jsonObj;

    public JSONUtil(String JSONString) throws JSONException {
        Log.d(TAG, "Parsing: " + JSONString);
        this.jsonObj = new JSONObject(JSONString);
    }
    
    public String getPlantID() throws JSONException{
    	return this.jsonObj.getString("plant_id");
    }
    
    public String getServerID() throws JSONException{
    	return this.jsonObj.getString("server_id");
    }
}