package com.itechart.photomap.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.itechart.photomap.Constants;
import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;
import com.itechart.photomap.utils.interfaces.AlertDialogApplyListener;
import com.itechart.photomap.utils.interfaces.AlertDialogWithEditTextListener;

public class Utils {
	public static int getDisplayWidth() {
		DisplayMetrics metrics = PhotoMap.getInstance().getResources().getDisplayMetrics();

		return metrics.widthPixels;
	}

	public static int getDisplayHeight() {
		DisplayMetrics metrics = PhotoMap.getInstance().getResources().getDisplayMetrics();

		return metrics.heightPixels;
	}

	public static void handleException(String message, Throwable t) {
		String exception = "";

		if (t != null) {
			exception = Log.getStackTraceString(t);
		}

		if (Constants.DEBUG_MODE) {
			Log.d("PhtoMap", message + " handledException: " + exception);
		}
	}

	public static void showAlertDialogWithEditText(Context context, String title, final AlertDialogWithEditTextListener listener) {
		final EditText input = new EditText(context);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(5, 2, 5, 2);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

		final AlertDialog alertDialog = new AlertDialog.Builder(context).setView(input).setTitle(title).setCancelable(false).setPositiveButton(context.getString(android.R.string.ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.dialogClosedWithString(input.getText().toString());
			}
		}).create();

		alertDialog.setCancelable(false);
		alertDialog.setTitle(title);

		alertDialog.show();
	}

	public static void showDilogApplyDialog(Context context, String message, String positiveButtonTitle, String negativeButtonTitle, final AlertDialogApplyListener listener) {
		LayoutInflater factory = LayoutInflater.from(context);
		LinearLayout alertDialog = (LinearLayout) factory.inflate(R.layout.alert_dialog_apply, null);

		final TextView titleTextView = (TextView) alertDialog.findViewById(R.id.alert_dialog_apply_text_view);
		titleTextView.setText(message);

		final Button positiveButton = (Button) alertDialog.findViewById(R.id.alert_dialog_apply_positive_button);
		positiveButton.setText(positiveButtonTitle);

		final Button negativeButton = (Button) alertDialog.findViewById(R.id.alert_dialog_apply_negative_button);
		negativeButton.setText(negativeButtonTitle);

		final AlertDialog dialog = new AlertDialog.Builder(context).setView(alertDialog).create();

		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.dialogCloseWithPositive();
				dialog.dismiss();
			}
		});

		negativeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.dialogCloseWithNegative();
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.show();
	}

	public static void saveBitmapToFile(Bitmap bitmap, File file) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
	}

	public static File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
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

	public static LatLng parseGeoTag(String latitude, String longitude, String latitudeRef, String longtitudeRef) {
		Float latitudeNumb = 0.0f, longitudeNumb = 0.0f;
		if ((latitude != null) && (latitudeRef != null) && (longitude != null) && (longtitudeRef != null)) {

			if (latitudeRef.equals("N")) {
				latitudeNumb = (convertToDegree(latitude));
			} else {
				latitudeNumb = (0 - convertToDegree(longitude));
			}

			if (longtitudeRef.equals("E")) {
				longitudeNumb = (convertToDegree(longitude));
			} else {
				longitudeNumb = (0 - convertToDegree(latitude));
			}
		}

		LatLng resultPoint = new LatLng(latitudeNumb, longitudeNumb);

		return resultPoint;
	}

	private static Float convertToDegree(String stringDMS) {
		Float result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
		Double D0 = Double.valueOf(stringD[0]);
		Double D1 = Double.valueOf(stringD[1]);
		Double FloatD = D0 / D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = Double.valueOf(stringM[0]);
		Double M1 = Double.valueOf(stringM[1]);
		Double FloatM = M0 / M1;

		String[] stringS = DMS[2].split("/", 2);
		Double S0 = Double.valueOf(stringS[0]);
		Double S1 = Double.valueOf(stringS[1]);
		Double FloatS = S0 / S1;

		result = Double.valueOf((FloatD + (FloatM / 60) + (FloatS / 3600))).floatValue();

		return result;

	};

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];

			for (;;) {
				int count = is.read(bytes, 0, buffer_size);

				if (count == -1) {
					break;
				}

				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {

		}
	}

	public static LatLng getGeotagFromPhotoFile(Photo photo) {
		LatLng pointCord = null;
		try {
			File photoFile = new File(photo.getFilePath());
			ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());

			String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

			pointCord = Utils.parseGeoTag(latitude, longitude, latitudeRef, longitudeRef);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return pointCord;
		}
		return pointCord;
	}
}
