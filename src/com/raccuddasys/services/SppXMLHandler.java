package com.raccuddasys.services;

import org.xml.sax.helpers.DefaultHandler;

import com.raccuddasys.MobiGuide;

public class SppXMLHandler extends DefaultHandler{
	public static String sppTitle;
	public static String subTitle;
	public static String scientific;
	public static String tnailPath;
	public static String albumTid;
	public static String identification;
	public static String conservation;
	public static String facts;
	public static String call;
	public static String bodyText;
	public static String videoCall;
	public static String audioCall;
	
	//public static InputStream video;
	
	boolean getTitle = false;
	boolean getSubTitle = false;
	boolean getTNailPath = false;
	boolean getScientific = false;
	boolean getAlbumId = false;
	boolean getIdentification = false;
	boolean getConservation = false;
	boolean getFacts = false;
	boolean getBody = false;
	boolean getVideoCall = false;
	boolean titCounter = false;
	boolean getFilePath = false;
	boolean getAudioCall = false;
	boolean getAudioPath = false;
	
	int counter = 0;
	
	
	public void characters(char[] ch, int start, int length) {
		//System.out.println("Characters: " + new String(ch, start, length));
		String toGet = new String(ch, start, length);
		if(getTitle){
			if(!titCounter){
				sppTitle = toGet;
				getTitle = false;
				titCounter = true;
			}
			if(toGet.equals("title")){
				if(!titCounter){
					getTitle = true;
				}
				else{
					titCounter = false;
				}
			}
		}
		if(getSubTitle){
			if(counter < 3){
				counter++;
			}
			else{
				subTitle = toGet;
				getSubTitle = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_sub_title")){
			getSubTitle = true;
			counter = 0;
		}
		if(getScientific){
			if(counter < 3){
				counter++;
			}
			else{
				scientific = toGet;
				getScientific = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_scientific_name")){
			getScientific = true;
			counter = 0;
		}
		if(getAlbumId){
			if(counter < 3){
				counter++;
			}
			else{
				albumTid = toGet;
				getAlbumId = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_album_tid")){
			getAlbumId = true;
			counter = 0;
		}
		if(getIdentification){
			if(counter < 3){
				counter++;
			}
			else{
				identification = toGet;
				getIdentification = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_identification")){
			getIdentification = true;
			counter = 0;
		}
		if(getConservation){
			if(counter < 3){
				counter++;
			}
			else{
				conservation = toGet;
				getConservation = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_conservation")){
			getConservation = true;
			counter = 0;
		}
		if(getFacts){
			if(counter < 3){
				counter++;
			}
			else{
				facts = toGet;
				getFacts = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_interesting_facts")){
			getFacts = true;
			counter = 0;
		}
		if(getBody){
			bodyText = toGet;
			getBody = false;
		}
		if(toGet.equals("body_value")){
			getBody = true;
		}
		if(getTNailPath){
			if(getFilePath){
				tnailPath = toGet;
				getFilePath = false;
				getTNailPath = false;
			}
		}
		if(toGet.equals("field_thumb_nail")){
			getTNailPath = true;
		}
		if(getVideoCall){
			if(getFilePath){
				videoCall = MobiGuide.url+"/"+toGet;
				getFilePath = false;
				getVideoCall = false;
			}
		}
		if(toGet.equals("field_video_call")){
			getVideoCall = true;
		}
		if(toGet.equals("filepath")){
			getFilePath = true;
		}
		if(getAudioCall){
			if(getAudioPath){
				audioCall = MobiGuide.url+"/"+toGet;
				getAudioPath = false;
				getAudioCall = false;
			}
		}
		if(toGet.equals("field_audio_call")){
			getAudioCall = true;
		}
		if(toGet.equals("filepath") && getAudioCall){
			getAudioPath = true;
		}
	}

}
