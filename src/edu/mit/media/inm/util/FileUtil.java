package edu.mit.media.inm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtil{
	public static String TAG = "FileUtil";
	public static void copyFile(Context ctx, Uri inputPath, Uri outputPath) {

	    InputStream in = null;
	    OutputStream out = null;
	    try {

	        //create output directory if it doesn't exist
	        File dir = new File (outputPath.toString()); 
	        if (!dir.exists())
	        {
	            dir.mkdirs();
	        }

  
	        ContentResolver content = ctx.getContentResolver();
	        in = content.openInputStream(inputPath);
	        out = content.openOutputStream(outputPath);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;

	            // write the output file (You have now copied the file)
	            out.flush();
	        out.close();
	        out = null;        

	    }  catch (FileNotFoundException fnfe1) {
	        Log.e(TAG, fnfe1.getMessage());
	    }
	            catch (Exception e) {
	        Log.e(TAG, e.getMessage());
	    }
	}
}