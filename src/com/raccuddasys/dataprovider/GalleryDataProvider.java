package com.raccuddasys.dataprovider;

import javax.microedition.lcdui.Image;

import org.kalmeo.kuix.core.model.DataProvider;


public class GalleryDataProvider extends DataProvider{
	private static final String PHOTOIMG_PROPERTY = "photo";
	private static final String PHOTOURL_PROPERTY = "fullPhotoUrl";
	String fullPhotoUrl;
	Image tnailPhoto;
	
	public void setPhotoUrl(String url){
		fullPhotoUrl = url;
	}
	public void setPhoto(Image photo){
		this.tnailPhoto = photo;
	}
	protected Object getUserDefinedValue(String property) {
		if(PHOTOIMG_PROPERTY.equals(property)){
			return tnailPhoto;
		}
		if(PHOTOURL_PROPERTY.equals(property)){
			return fullPhotoUrl;
		}
		return null;
	}

}
