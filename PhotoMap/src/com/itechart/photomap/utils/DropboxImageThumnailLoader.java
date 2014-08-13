package com.itechart.photomap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI.ThumbFormat;
import com.dropbox.client2.DropboxAPI.ThumbSize;
import com.itechart.photomap.PhotoMap;

public class DropboxImageThumnailLoader {
	private MemoryCache memoryCache = new MemoryCache();
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private final List<PhotoToLoad> downloadQueue = new LinkedList<PhotoToLoad>();
	private Thread downloadThread;
	private Handler handler = new Handler();

	public DropboxImageThumnailLoader() {
	}

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);

		Bitmap bitmap = memoryCache.get(url);

		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(url, imageView);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		addToDownloadQueue(p);
	}

	private Bitmap getBitmapFromWeb(String url) {
		Bitmap bitmap = null;
		try {
			File f = File.createTempFile(DropboxImageThumnailLoader.class.getName(), "temp");
			try {
				OutputStream os = new FileOutputStream(f);

				PhotoMap.getInstance().getmApi().getThumbnail(url, os, ThumbSize.BESTFIT_640x480, ThumbFormat.JPEG, null);
				os.close();

				bitmap = decodeFile(f);
			} catch (Throwable ex) {
				if (f.exists()) {
					f.delete();
				}
				throw ex;
			}

			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();

			if (ex instanceof OutOfMemoryError) {
				memoryCache.clear();
			}

			return null;
		}
	}

	private Bitmap decodeFile(File f) {
		if (f.exists()) {
			try {
				FileInputStream stream1 = new FileInputStream(f);
				Bitmap bitmap = BitmapFactory.decodeStream(stream1, null, null);
				stream1.close();
				return bitmap;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	public void clearCache() {
		memoryCache.clear();
	}

	private void addToDownloadQueue(PhotoToLoad photoToLoad) {
		if (downloadThread == null) {
			downloadThread = new Thread(new PhotoDownloadTask(downloadQueue), "PhotoDownloadThread");
			downloadThread.start();
		}

		addToQueue(downloadQueue, photoToLoad);
	}

	private static void addToQueue(List<PhotoToLoad> queue, PhotoToLoad photoToLoad) {
		synchronized (queue) {
			queue.add(photoToLoad);

			queue.notifyAll();
		}
	}

	private class PhotoDownloadTask implements Runnable {
		public boolean isCanceled = false;
		private List<PhotoToLoad> queue;

		public PhotoDownloadTask(List<PhotoToLoad> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			while (!isCanceled) {
				try {
					PhotoToLoad photoToLoad;
					synchronized (queue) {
						if (queue.size() > 0) {
							photoToLoad = queue.get(0);
							queue.remove(0);
						} else {
							queue.wait();
							continue;
						}
					}

					download(photoToLoad);

				} catch (InterruptedException e) {
					Utils.handleException("PhotoDownloadTask", e);
				}
			}
		}

		private void download(PhotoToLoad photoToLoad) {
			try {
				Bitmap bmp = getBitmapFromWeb(photoToLoad.url);

				postResult(photoToLoad, bmp);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}

		private void postResult(final PhotoToLoad photoToLoad, final Bitmap rawBmp) {
			if (rawBmp != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						photoToLoad.imageView.setImageBitmap(rawBmp);
					}
				});
			}
		}
	}
}
