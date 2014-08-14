package com.itechart.photomap;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    private Context context;
    private SharedPreferences sharedPreferences;
    private Boolean showUploadDialog;    
    public Settings(Context context) {
        this.context = context;
        initialize();
    }
    
   	private void initialize() {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        showUploadDialog = sharedPreferences.getBoolean(Constants.SHARED_PREFERENCE_SHOW_UPLOAD_DIALOG, false);
    }
   	
	public boolean isShowUploadDialog() {
		return showUploadDialog;
	}

	public void setshowUploadDialog(boolean showUploadDialog) {
		this.showUploadDialog = showUploadDialog;
		sharedPreferences.edit().putBoolean(Constants.SHARED_PREFERENCE_SHOW_UPLOAD_DIALOG, this.showUploadDialog).commit();		
	}
}
