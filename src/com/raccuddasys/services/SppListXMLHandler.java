package com.raccuddasys.services;

import java.util.Vector;

import org.xml.sax.helpers.DefaultHandler;

import com.raccuddasys.MobiGuide;

public class SppListXMLHandler extends DefaultHandler{
	public static Vector sppTitles = new Vector();
	public static Vector sppSubTitles = new Vector();
	public static Vector sppScientific = new Vector();
	public static Vector sppTnailPath = new Vector();
	public static Vector sppNId = new Vector();
	
	boolean getTitle;
	boolean getSubTitle;
	boolean getScientific;
	boolean getPath;
	boolean getNid;
	boolean nidCounter;
	boolean titCounter;
	
	int counter;
		
	public SppListXMLHandler(){
		super();
		getTitle = false;
		getSubTitle = false;
		getScientific = false;
		getPath = false;
		getNid = false;
		counter = 0;
		nidCounter = false;
		titCounter = false;
	}
	public void characters(char[] ch, int start, int length) {
		//System.out.println("Characters: " + new String(ch, start, length));
		String toGet = new String(ch, start, length);
		if(getPath){
			sppTnailPath.addElement(MobiGuide.url+"/"+toGet);
			getPath = false;
		}
		if(toGet.equals("filepath")){
			getPath = true;
		}
		if(getNid){
			if(!nidCounter){
				sppNId.addElement(toGet);
				getNid = false;
				nidCounter = true;
			}
			
		}
		if(toGet.equals("nid")){
			if(!nidCounter){
				getNid = true;
			}
			else{
				nidCounter = false;
			}
			
		}
		if(getScientific){
			if(counter < 3){
				counter++;
			}
			else{
				sppScientific.addElement(toGet);
				getScientific = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_scientific_name")){
			getScientific = true;
			counter = 0;
		}
		if(getTitle){
			if(!titCounter){
				sppTitles.addElement(toGet);
				getTitle = false;
				titCounter = true;
			}
		}
		if(toGet.equals("title")){
			if(!titCounter){
				getTitle = true;
			}
			else{
				titCounter = false;
			}
		}
		if(getSubTitle){
			if(counter < 3){
				counter++;
			}
			else{
				sppSubTitles.addElement(toGet);
				getSubTitle = false;
				counter = 0;
			}
		}
		if(toGet.equals("field_sub_title")){
			getSubTitle = true;
			counter = 0;
		}
	}

}
