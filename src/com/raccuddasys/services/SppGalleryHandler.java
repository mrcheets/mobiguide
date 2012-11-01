package com.raccuddasys.services;

import java.util.Vector;

import org.xml.sax.helpers.DefaultHandler;

import com.raccuddasys.MobiGuide;

public class SppGalleryHandler extends DefaultHandler{
	
	public static Vector tnailImagePaths;
	public static Vector fullImagePaths;
	public static Vector bodyText;
	public static Vector photoTitles;
	public static Vector tnailImages;
	boolean getTnailPath = false;
	boolean getImagePath = false;
	boolean getBodyText = false;
	boolean getPhotoTitle = false;
	
	public SppGalleryHandler(){
		super();
		tnailImagePaths = new Vector();
		fullImagePaths = new Vector();
		bodyText = new Vector();
		photoTitles = new Vector();
		tnailImages = new Vector();
	}
	public void characters(char[] ch, int start, int length) {
		//System.out.println("Characters: " + new String(ch, start, length));
		String toGet = new String(ch, start, length);
		if(getTnailPath){
			tnailImagePaths.addElement(MobiGuide.url+"/"+toGet);
			getTnailPath = false;
		}
		if(toGet.equals("thumbnail")){
			getTnailPath = true;
		}
		if(getPhotoTitle){
			photoTitles.addElement(toGet);
			getPhotoTitle = false;
		}
		if(toGet.equals("title")){
			getPhotoTitle = true;
		}
		if(getBodyText){
			bodyText.addElement(toGet);
			getBodyText = false;
		}
		if(toGet.equals("body_value")){
			getBodyText = true;
		}
		if(getImagePath){
			fullImagePaths.addElement(MobiGuide.url+"/"+toGet);
			getImagePath = false;
		}
		if(toGet.equals("_original")){
			getImagePath = true;
		}
	}

}
