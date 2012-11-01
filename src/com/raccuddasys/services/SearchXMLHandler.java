package com.raccuddasys.services;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SearchXMLHandler extends DefaultHandler{
	public static Vector sppIds;
	public static Vector sppTitles;
	public static Vector snippets;
	
	String tempLink;
	
	boolean getLink = false;
	boolean getTitle = false;
	//boolean getSnippet = false;
	boolean getType = false;
	boolean isMobileInfo = false;
	boolean positiveResults = true;
	
	public SearchXMLHandler(){
		super();
		sppIds = new Vector();
		sppTitles = new Vector();
	}
	public void characters(char[] ch, int start, int length) {
		String toGet = new String(ch, start, length);
		if(getLink && positiveResults){
			tempLink = toGet;
			getLink = false;
		}
		if(toGet.equals("link") && positiveResults){
			getLink = true;
			
			//All link characters will signify the beginning of new section of data. Thus, set
			//isMobileInfo to false at each link instance
			isMobileInfo = false;  
		}
		if(getType && positiveResults){
			if(toGet.equals("MobileInfo")){
				isMobileInfo = true;
			}			
		}
		if(toGet.equals("type") && positiveResults){
			getType = true;
		}
		if(isMobileInfo && positiveResults){
			//Do all the getting in here
			//System.out.println("Characters: " +toGet);
			if(getTitle){
				//Just remove the id from tempLink and put it in sppIds vector
				//System.out.println("Ading to sppIds");
				SearchXMLHandler.sppIds.addElement(tempLink.substring(tempLink.lastIndexOf('/')+1));
				//System.out.println(tempLink.substring(tempLink.lastIndexOf('/')+1));
				//Put the title into the sppTitles vector
				//System.out.println("Ading to sppTitles");
				SearchXMLHandler.sppTitles.addElement(toGet);
				//System.out.println(toGet);
				getTitle = false;
			}
			if(toGet.equals("title") && positiveResults){
				getTitle = true;
			}			
		}
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		//Set isMobileInfo to true where relevant
		//System.out.println("Uri: "+uri+" localName: "+localName+" QName: "+qName);
		if(qName.equals("fault")){
			positiveResults = false;
		}
		
		
	}
	public void endElement(String uri, String localName, String qName){
		//Set isMobileInfo to false when relevant
		
	}
}
