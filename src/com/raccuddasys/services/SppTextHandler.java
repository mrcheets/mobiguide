package com.raccuddasys.services;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.raccuddasys.SpeciesFrame;

public class SppTextHandler extends DefaultHandler{

	boolean getTitle = false;
	boolean getPText = false;
	boolean titlePresent = false;
	
	public static Vector idText = new Vector();
	public static Vector conservationText = new Vector();
	public static Vector factsText = new Vector();
	public static Vector bodyTextText = new Vector();
	
	Vector tempText = new Vector();
	String xmlText;
	
	public SppTextHandler(){
		super();
		if(SpeciesFrame.chooseText.equals("identification")){
			xmlText = SppXMLHandler.identification;
		}
		if(SpeciesFrame.chooseText.equals("conservation")){
			xmlText = SppXMLHandler.conservation;
		}
		if(SpeciesFrame.chooseText.equals("facts")){
			xmlText = SppXMLHandler.facts;
		}
		if(SpeciesFrame.chooseText.equals("bodyText")){
			xmlText = SppXMLHandler.bodyText;
		}
	}
	public void characters(char[] ch, int start, int length) {
		//System.out.println("Characters: "+new String(ch, start, length));
		String toGet = new String(ch, start, length);
		if(getTitle){
			//System.out.println("Getting title");
			if(tempText.size() == 0){
				//System.out.println("Getting title: Vector is empty");
				tempText.addElement(toGet);
			}
			else{
				//System.out.println("Getting title: Vector is not empty");
				if(SpeciesFrame.chooseText.equals("identification")){
					//System.out.println("Adding  id title");
					if(idText == null){
						//System.out.println("Handler's idText is null");
					}
					idText.addElement(tempText);
					//System.out.println("Added id title");
				}
				if(SpeciesFrame.chooseText.equals("conservation")){
					//System.out.println("Adding  conservation title");
					conservationText.addElement(tempText);
					//System.out.println("Added conservation title");
				}
				if(SpeciesFrame.chooseText.equals("facts")){
					//System.out.println("Adding facst title");
					factsText.addElement(tempText);
					//System.out.println("Added facst title");
				}
				if(SpeciesFrame.chooseText.equals("bodyText")){
					//System.out.println("Adding bodytext title");
					bodyTextText.addElement(tempText);
					//System.out.println("Added bodytext title");
				}	
				tempText = new Vector();
				tempText.addElement(toGet);
			}
			//System.out.println("Got title");
		}
		if(getPText){
			//System.out.println("Getting paragraph");
			if(tempText.size() == 0){
				tempText.addElement("");
			}
			tempText.addElement(toGet.trim());
			//System.out.println("Got paragraph");
		}
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		//System.out.println("Start element Uri: "+uri+", localName: "+localName+", QName: "+qName);
		//System.out.println("AttributeCount: "+ attributes.getLength());
		if(qName.equals("h2")){
			getTitle = true;
		}
		if(qName.equals("p")){
			getPText = true;
		}
	}
	public void endElement(String uri, String localName, String qName){
		//System.out.println("End element Uri: "+uri+", localName: "+localName+", QName: "+qName);
		if(qName.equals("h2")){
			getTitle = false;
		}
		if(qName.equals("p")){
			getPText = false;
		}
		if(qName.equals("text")){
			if(SpeciesFrame.chooseText.equals("identification")){
				idText.addElement(tempText);
			}
			if(SpeciesFrame.chooseText.equals("conservation")){
				conservationText.addElement(tempText);
			}
			if(SpeciesFrame.chooseText.equals("facts")){
				factsText.addElement(tempText);
			}
			if(SpeciesFrame.chooseText.equals("bodyText")){
				bodyTextText.addElement(tempText);
			}	
			
			tempText = new Vector();
			
			/*System.out.println("Sifted Texts");
			int size = idText.size();
			System.out.println("Identification");
			for(int i = 0; i < size; i++){
				Vector temp = (Vector)idText.elementAt(i);
				int size2 = temp.size();
				System.out.println("Paragraph"+i+" \n");
				for(int j = 0; j < size2; j++){
					if(j == 0){
						System.out.println("Heading: "+ temp.elementAt(j));
					}else{
						System.out.println(temp.elementAt(j));
					}
				}
			}
			size = conservationText.size();
			System.out.println("Conservation");
			for(int i = 0; i < size; i++){
				Vector temp = (Vector)conservationText.elementAt(i);
				int size2 = temp.size();
				System.out.println("Paragraph "+i+" \n");
				for(int j = 0; j < size2; j++){
					if(j == 0){
						System.out.println("Heading: "+ temp.elementAt(j));
					}else{
						System.out.println(temp.elementAt(j));
					}
				}
			}
			size = factsText.size();
			System.out.println("facts");
			for(int i = 0; i < size; i++){
				Vector temp = (Vector)factsText.elementAt(i);
				int size2 = temp.size();
				System.out.println("Paragraph"+i+" \n");
				for(int j = 0; j < size2; j++){
					if(j == 0){
						System.out.println("Heading: "+ temp.elementAt(j));
					}else{
						System.out.println(temp.elementAt(j));
					}
				}
			}
			size = bodyTextText.size();
			System.out.println("bodyText");
			for(int i = 0; i < size; i++){
				Vector temp = (Vector)bodyTextText.elementAt(i);
				int size2 = temp.size();
				System.out.println("Paragraph"+i+" \n");
				for(int j = 0; j < size2; j++){
					if(j == 0){
						System.out.println("Heading: "+ temp.elementAt(j));
					}else{
						System.out.println(temp.elementAt(j));
					}
				}
			}*/
		}
	}
}
