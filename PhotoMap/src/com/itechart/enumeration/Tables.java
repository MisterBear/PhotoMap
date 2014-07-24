package com.itechart.enumeration;

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
