package edu.mit.media.inm.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class JSONUtil {
	private static String TAG = "JSONUtil";
	private JSONObject jsonObj;

    public JSONUtil(String JSONString) {
        try {
			this.jsonObj = new JSONObject(JSONString);
		} catch (JSONException e) {
			this.jsonObj = new JSONObject();
			e.printStackTrace();
		}
    }
    
    public String getPlantID() throws JSONException{
    	return this.jsonObj.getString("plant_id");
    }
    
    public String getServerID() throws JSONException{
    	return this.jsonObj.getString("server_id");
    }
    
    @Override
    public String toString(){
    	try {
			return jsonObj.toString(2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return "Failed.";
    }
}