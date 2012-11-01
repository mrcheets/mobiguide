package com.raccuddasys.dataprovider;

import javax.microedition.lcdui.Image;

import org.kalmeo.kuix.core.model.DataProvider;

public class ListDataProvider extends DataProvider{
	private static final String PHOTO_PROPERTY = "photo";
	private static final String CATTITLE_PROPERTY = "CategoryTitle";
	private static final String CATDESC_PROPERTY = "CategoryDesc";
	private static final String CATID_PROPERTY = "CategoryId";
	
	private static final String SPPID_PROPERTY = "SpeciesId";
	private static final String SPPTNAIL_PROPERTY = "SpeciesTNail";
	private static final String SPPTITLE_PROPERTY = "SpeciesTitle";
	private static final String SPPSCIENTIFIC_PROPERTY = "SpeciesScientific";
	private static final String SPPSUBTITLE_PROPERTY = "SpeciesSubTitle";
	private static final String SPPIMAGE_PROPERTY = "SpeciesImage";
	private static final String VECTORID_PROPERTY = "VectorId";
	
	String photoUrl;
	String CatTitle;
	String CatDesc;
	String id;
	String subTitle;
	String thumbNail;
	String scientific;
	Image listImage;
	String vectorId;
	public void setPhotoUrl(String url){
		photoUrl = url;
	}
	public void setCatTitle(String title){
		CatTitle = title;
	}
	public void setCatDesc(String desc){
		CatDesc = desc;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public void setSppId(String id){
		this.id = id;
	}
	public void setSppTitle(String title){
		CatTitle = title;
	}
	public void setSppSubTitle(String subTitle){
		this.subTitle = subTitle;
	}
	public void setSppScientific(String scientific){
		this.scientific = scientific;
	}
	public void setSppThumbnail(String thumbnail){
		this.thumbNail = thumbnail;
	}
	public void setSppImage(Image image){
		this.listImage = image;
	}
	public void setVectorId(String id){
		vectorId = id;
	}
	protected Object getUserDefinedValue(String property) {
		if(PHOTO_PROPERTY.equals(property)){
			return photoUrl;
		}
		if(CATTITLE_PROPERTY.equals(property)){
			return CatTitle;
		}
		if(CATDESC_PROPERTY.equals(property)){
			return CatDesc;
		}
		if(CATID_PROPERTY.equals(property)){
			return id;
		}
		if(SPPID_PROPERTY.equals(property)){
			return id;
		}
		if(SPPTNAIL_PROPERTY.equals(property)){
			return thumbNail;
		}
		if(SPPTITLE_PROPERTY.equals(property)){
			return CatTitle;
		}
		if(SPPSCIENTIFIC_PROPERTY.equals(property)){
			return scientific;
		}
		if(SPPSUBTITLE_PROPERTY.equals(property)){
			return subTitle;
		}
		if(SPPIMAGE_PROPERTY.equals(property)){
			return listImage;
		}
		if(VECTORID_PROPERTY.equals(property)){
			return vectorId;
		}
		return null;
	}

}
