package edu.mit.media.inm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import edu.mit.media.inm.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtil {
	public static String TAG = "FileUtil";

	public static void copyFile(Context ctx, Uri inputPath, Uri outputPath) {

		InputStream in = null;
		OutputStream out = null;
		try {

			// create output directory if it doesn't exist
			File dir = new File(outputPath.toString());
			if (!dir.exists()) {
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

		} catch (FileNotFoundException fnfe1) {
			Log.e(TAG, fnfe1.getMessage());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Attempts to return a drawable for the file given. If not found, returns a
	 * confused face instead.
	 * 
	 * @param ctx
	 * @param filename
	 * @return
	 */
	public static Drawable openImage(Context ctx, String filename) {
		File image = new File(ctx.getExternalFilesDir(null) + File.separator
				+ "InM_photos" + File.separator + filename);
		InputStream ims;

		try {
			ims = new FileInputStream(image);
		} catch (FileNotFoundException e) {
			return ctx.getResources().getDrawable(R.drawable.smiley_confused);
		}
		return Drawable.createFromStream(ims, null);
	}

	/**
	 * Used for scaling down bitmaps before loading into memory.
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	/**
	 * Get downsampled bitmap
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(Context ctx,
			String filename, int reqWidth, int reqHeight) {
		String filepath = ctx.getExternalFilesDir(null) + File.separator
				+ "InM_photos" + File.separator + filename;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filepath, options);
	}

	/**
	 * Get downsampled bitmap
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Context ctx,
			int res_id, int reqWidth, int reqHeight) {
		Resources res = ctx.getResources();

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, res_id, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, res_id, options);
	}

	public static void saveBitmap(String filename, Bitmap bmp) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveScaledBitmap(Context ctx, Uri photo_uri) {
		Bitmap bmp = FileUtil.decodeSampledBitmapFromFile(ctx,
				photo_uri.getLastPathSegment(), 400, 200);
		try {
			ContentResolver content = ctx.getContentResolver();
			OutputStream out = content.openOutputStream(photo_uri);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}