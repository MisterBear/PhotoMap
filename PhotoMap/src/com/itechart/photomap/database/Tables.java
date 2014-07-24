package com.itechart.photomap.database;

public enum Tables {
	PHOTO("PHOTO");
	
	private String value;
	
	private Tables(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
