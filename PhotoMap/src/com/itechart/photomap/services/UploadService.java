package com.itechart.photomap.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.itechart.photomap.Constants;
import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.database.model.Photo;

public class UploadService extends IntentService {
	private DropboxAPI<AndroidAuthSession> mApi;
	private static final String tempFolderPath = "/temp";
	public static final String photosFolderPath = "/Photos/";

	public UploadService() {
		super(UploadService.class.getName());
	}

	@Override
	public void onCreate() {
		mApi = PhotoMap.getInstance().getmApi();

		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			ArrayList<Photo> photos = new ArrayList<Photo>(PhotoMap.getInstance().getPhotoMapDAO().queryAllUnuploaded());

			for (Photo photo : photos) {
				File file = new File(photo.getFilePath());

				if (!file.exists()) {
					PhotoMap.getInstance().getPhotoMapDAO().delete(photo);

					continue;
				}

				Bitmap bitmap = BitmapFactory.decodeFile(photo.getFilePath());

				bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
				File temp = File.createTempFile("pht", "temp");

				FileOutputStream out = new FileOutputStream(temp);
				bitmap.compress(CompressFormat.JPEG, 100, out);

				out.flush();
				out.close();

				InputStream in = new FileInputStream(temp);

				String path = tempFolderPath + "/" + file.getName();
				UploadRequest mRequest = mApi.putFileOverwriteRequest(path, in, temp.length(), null);

				if (mRequest != null) {
					mRequest.upload();
				}

				temp.delete();
				in.close();
			}

			for (Photo photo : photos) {
				File file = new File(photo.getFilePath());

				try {
					mApi.copy(tempFolderPath + "/" + file.getName(), photosFolderPath + file.getName());

					mApi.delete(tempFolderPath + "/" + file.getName());
				} catch (DropboxServerException e) {
				}
				photo.setIsUploaded(true);

				PhotoMap.getInstance().getPhotoMapDAO().update(photo);
			}
			mApi.delete(tempFolderPath);

			sendBroadcast(new Intent(Constants.BROADCAST_ACTION_FINISH_UPLOAD));
		} catch (DropboxUnlinkedException e) {
		} catch (DropboxFileSizeException e) {
		} catch (DropboxPartialFileException e) {
		} catch (DropboxIOException e) {
		} catch (DropboxParseException e) {
		} catch (DropboxException e) {
		} catch (IOException e) {

		} catch (SQLException e) {
		}
	}
}
