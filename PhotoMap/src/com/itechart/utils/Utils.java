package com.itechart.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.itechart.Constants;
import com.itechart.utils.interfaces.AlertDialogListener;

public class Utils {
	public static void handleException(String message, Throwable t) {
		String exception = "";

		if (t != null) {
			exception = Log.getStackTraceString(t);
		}

		if (Constants.DEBUG_MODE) {
			Log.d("PhtoMap", message + " handledException: " + exception);
		}
	}

	public static void showAlertDialogWithEditText(Context context, String title, final AlertDialogListener listener) {
		final EditText input = new EditText(context);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(5, 2, 5, 2);
		input.setLayoutParams(lp);
		
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
										.setView(input)
										.setTitle(title)
										.setCancelable(false)
										.setPositiveButton(context.getString(android.R.string.ok), new OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												listener.dialogClosedWithString(input.getText().toString());
											}
										})
										.create();
		
		alertDialog.setCancelable(false);
		alertDialog.setTitle(title);
		
		alertDialog.show();
	}
	
	public static void saveBitmapToFile(Bitmap bitmap, File file) {
		FileOutputStream out = null;
		try {
		       out = new FileOutputStream(file);
		       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		       try{
		           out.close();
		       } catch(Throwable ignore) {}
		}
	}
	
	public static File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    return image;
	}
	
	public static String[] getStorageDirectories() {
		String DIR_SEPORATOR = "/";
		// Final set of paths
		final Set<String> rv = new HashSet<String>();
		// Primary physical SD-CARD (not emulated)
		final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
		// All Secondary SD-CARDs (all exclude primary) separated by ":"
		final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
		// Primary emulated SD-CARD
		final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
			// Device has physical external storage; use plain paths.
			if (TextUtils.isEmpty(rawExternalStorage)) {
				// EXTERNAL_STORAGE undefined; falling back to default.
				rv.add("/storage/sdcard0");
			} else {
				rv.add(rawExternalStorage);
			}
		} else {
			// Device has emulated storage; external storage paths should have
			// userId burned into them.
			final String rawUserId;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
				rawUserId = "";
			} else {
				final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
				final String[] folders = path.split(DIR_SEPORATOR);
				final String lastFolder = folders[folders.length - 1];
				boolean isDigit = false;
				try {
					Integer.valueOf(lastFolder);
					isDigit = true;
				} catch (NumberFormatException ignored) {
				}
				rawUserId = isDigit ? lastFolder : "";
			}
			// /storage/emulated/0[1,2,...]
			if (TextUtils.isEmpty(rawUserId)) {
				rv.add(rawEmulatedStorageTarget);
			} else {
				rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
			}
		}
		// Add all secondary storages
		if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
			// All Secondary SD-CARDs splited into array
			final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
			Collections.addAll(rv, rawSecondaryStorages);
		}
		return rv.toArray(new String[rv.size()]);
	}
}
