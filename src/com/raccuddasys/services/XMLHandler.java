package com.raccuddasys.services;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler{

	public static Vector catNames;
	public static Vector catDesc;
	public static Vector catId;
	boolean getName;
	boolean getTid;
	boolean getDesc;
	public XMLHandler(){
		super();
		catNames = new Vector();
		catDesc = new Vector();
		catId = new Vector();
		getName  = false;
		getTid = false;
		getDesc = false;
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		if(qName.equals("member")){
			//catNames.addElement("Member");
			//catDesc.addElement("Description");
			//catId.addElement("CatId");
			//System.out.println("Uri: "+uri+"\nLocalName: "+localName+"\nQName: "+qName);
		}			
	}
	public void characters(char[] ch, int start, int length) {
	    //System.out.println("Characters: " + new String(ch, start, length));
		String toGet = new String(ch, start, length);
		if(getName){
			catNames.addElement(toGet);
			getName = false;
			//System.out.println("Booleans "+getName+" "+getDesc+" "+getTid);
		}
		if(toGet.equals("name")){
			getName = true;
		}
		if(getDesc){
			catDesc.addElement(toGet);
			getDesc = false;
			//System.out.println("Booleans "+getName+" "+getDesc+" "+getTid);
		}
		if(toGet.equals("description")){
			getDesc = true;
		}
		if(getTid){
			catId.addElement(toGet);
			getTid = false;
			//System.out.println("Booleans "+getName+" "+getDesc+" "+getTid);
		}
		if(toGet.equals("tid")){
			getTid = true;
		}
	}
}
