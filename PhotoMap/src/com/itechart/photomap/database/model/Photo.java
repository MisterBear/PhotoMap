package com.itechart.photomap.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "photo")
public class Photo implements Parcelable{
	public static final String PHOTO_NAME_FIELD_NAME = "photoName";
	public static final String CREATE_DATE_FIELD_NAME = "createDate";
	public static final String FILE_PATH_FIELD_NAME = "filePath";
	public static final String IS_UPLOADED_FIELD_NAME = "isUploaded";
	public static final String LATITUDE_FIELD_NAME = "latitude";
	public static final String LONGITUDE_FIELD_NAME = "longitude";
	
	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField(dataType = DataType.STRING)
	private String photoName;
	@DatabaseField(dataType = DataType.LONG)
	private long createDate;
	@DatabaseField(dataType = DataType.STRING)
	private String filePath;
	@DatabaseField(dataType = DataType.BOOLEAN_OBJ)
	private Boolean isUploaded;
	@DatabaseField(dataType = DataType.FLOAT_OBJ)
	private Float latitude;
	;@DatabaseField(dataType = DataType.FLOAT_OBJ)
	private Float longitude;
	
	public Photo() {
		this.isUploaded = false;
	}
	
	public Photo(String photoName, String filePath, long createDate, Float latitude, Float longitude) {
		this.photoName = photoName;
		this.filePath = filePath;
		this.createDate = createDate;		
		this.latitude = latitude;
		this.longitude = longitude;
		this.isUploaded = false;
	}
	
	public Photo(Parcel in) {
		photoName = in.readString();
		createDate = in.readLong();
		filePath = in.readString();
		latitude = in.readFloat();
		longitude = in.readFloat();
		isUploaded = in.readByte() == 1;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getPhotoName() {
		return photoName;
	}
	
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	
	public long getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Boolean getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(Boolean isUploaded) {
		this.isUploaded = isUploaded;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(photoName);
		dest.writeLong(createDate);
		dest.writeString(filePath);
		dest.writeFloat(latitude);
		dest.writeFloat(longitude);
		dest.writeByte((byte)(isUploaded ? 1 : 0));
	}
	
	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
		public Photo createFromParcel(Parcel in) {
			return new Photo(in);
		}

		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};

}
