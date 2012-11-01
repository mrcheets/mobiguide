package com.raccuddasys;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.util.frame.Frame;

import com.raccuddasys.dataprovider.CategoryDataProvider;
import com.raccuddasys.dataprovider.ListDataProvider;
import com.raccuddasys.services.Loader;
import com.raccuddasys.services.XMLHandler;

public class CategoryFrame implements Frame, Runnable{
	
	CategoryDataProvider cdProvider;
	ListDataProvider listDataProvider;
	SAXParser parser;
	HttpConnection httpConnection;
	XMLHandler xmlHandler;
	InputStream is;
	PopupBox popUp;
	PopupBox helpPopup;
	Screen screen;
	int count = 0;
	Picture pic;
	Loader imgLoad;
	boolean continueDownload = true;
	boolean firstTime = true;
	boolean stopped = false;
		
	static int innerWidth;
	static int innerHeight;
		
	public CategoryFrame(){
		super();
		cdProvider = new CategoryDataProvider();
		//Download xml via thread
		//Decode xml and put the values where needed
		/*for(int i = 0; i < 10; i++){
			listDataProvider = new ListDataProvider();
			listDataProvider.setCatDesc("description "+i);
			listDataProvider.setCatTitle("Title "+i);
			listDataProvider.setPhotoUrl("Photo "+i);
			listDataProvider.setId(i);
			cdProvider.addItem("listData", listDataProvider);
		}*/		
	}

	public void onAdded() {
		// Load the content from the XML file with Kuix.loadScreen static method
		screen = Kuix.loadScreen("category.xml", cdProvider);
		
		// Set the application current screen
		screen.setCurrent();
		continueDownload = true;
		if(stopped){
			firstTime = true;
			stopped = false;
			XMLHandler.catNames = null;
		}
		if(firstTime){
			getCategories();
		}
		//System.out.println("Firsttime:"+ firstTime);
	}
	public void getCategories(){
		Thread getCats_T = new Thread(this);
		getCats_T.start();
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		innerWidth = screen.getInnerWidth();
		innerHeight = screen.getInnerHeight();
		
		//System.out.println("Inner Height:"+innerHeight+"\nInnerWidth:"+innerWidth);
		if ("exitConfirm".equals(identifier)) {
			// display a popup message
			Kuix.alert(Kuix.getMessage("EXIT_CONFIRM"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "exit", null);
			return false;
		}
		if ("exit".equals(identifier)) {
			// get the midlet instance to invoke the Destroy() method
			MobiGuide.getDefault().destroyImpl();
			//if the event has been processed, we return 'false' to avoid event forwarding to other frames
			return false;
		}
		if("search".equals(identifier)){
			Kuix.getFrameHandler().pushFrame(new SearchFrame());
			return false;
		}
		if("retry".equals(identifier)){
			Thread getCats_T = new Thread(this);
			getCats_T.start();
			//for(int i = 0; i < count; i++){
			//	System.out.println("System restarting");
			//}
			return false;
		}
		if("specieslist".equals(identifier)){
			try{
				Integer.parseInt((String) arguments[0]);
				Kuix.getFrameHandler().pushFrame(new SpeciesListFrame((String)arguments[0]));
			}catch(NumberFormatException nfe){
				Kuix.alert(Kuix.getMessage("ERRORLISTID"));
			}
		}
		if("stopAction".equals(identifier)){
			continueDownload = false;
			Loader.runLoader = false;
			//XMLHandler.catNames = null;
			stopped = true;
			imgLoad = null;
			Kuix.getFrameHandler().pushFrame(new CategoryLandingFrame());
		}
		if("help".equals(identifier)){
			helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
			TextArea message = (TextArea)helpPopup.getWidget("hlpMessage");
			message.setText(Kuix.getMessage("GENERALHELP"));
		}
		if("closeHelp".equals(identifier)){
			helpPopup.remove();
			helpPopup = null;
		}
		/*try{
			Integer.parseInt((String) identifier);
			Kuix.getFrameHandler().pushFrame(new SpeciesListFrame((String)identifier));
		}catch(NumberFormatException nfe){
			Kuix.alert("Input error. Wrong Category Id");
		}*/
		//Kuix.alert((String)identifier);
		
		return false;
	}

	public void onRemoved() {
		// TODO Auto-generated method stub
		
	}
	public void run(){
		//System.out.println("Categor frame run");
		int rc = 0;
		if(XMLHandler.catNames != null){
			//Do nothing
			//System.out.println("doing Nothing");
		}		
		else{
			//Download the xml
			try{
				popUp = Kuix.showPopupBox("progressPopup.xml", null); //Kuix.alert(Kuix.getMessage("DNLDCAT"), KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "stopDload");
				TextArea myText = (TextArea)popUp.getWidget("txtMessage");
				pic = (Picture)popUp.getWidget("picLoader");
				imgLoad = new Loader(pic);
				Thread imgLoop = new Thread(imgLoad);
				imgLoop.start();
				myText.setText(Kuix.getMessage("DNLDCAT"));
				//System.out.println("Thread started");
				if(continueDownload){
					parser = SAXParserFactory.newInstance().newSAXParser();
					httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/categories");
					rc = httpConnection.getResponseCode();
					if(rc != HttpConnection.HTTP_OK){
						popUp.remove();
						try{
							Thread.sleep(300);
						}catch(InterruptedException ie){
							
						}
						throw new IOException();
					}
				}
				if(continueDownload){
					is = httpConnection.openInputStream();
					//It is important to start this here and not earlier
					//This is to prevent it from being initialise yet there is no connection
					//Then there will be a problem when thread restarts 
					xmlHandler = new XMLHandler();
					parser.parse(is, xmlHandler);
				
					int info = XMLHandler.catNames.size();
				
					for(int i = 0; i < info; i++){
						if(continueDownload){
							listDataProvider = new ListDataProvider();
							listDataProvider.setCatDesc((String)XMLHandler.catDesc.elementAt(i));
							listDataProvider.setCatTitle((String)XMLHandler.catNames.elementAt(i));
							listDataProvider.setId((String)XMLHandler.catId.elementAt(i));
							cdProvider.addItem("listData", listDataProvider);
						}
					}
					listDataProvider = null;
				}
				//Kuix.getCanvas().revalidateAsSoonAsPossible();
				//ScrollPane scroll = (ScrollPane)Kuix.getCanvas().getDesktop().getWidget("catScrollPane");
				//scroll.setAutoScroll(true);
				Loader.runLoader = false;
				popUp.remove();
				popUp = null;
				firstTime = false;
				//Kuix.alert("Elements:"+info);
			}catch(IOException ioe){
				Loader.runLoader = false;
				popUp.remove();
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
				if(count == 3){
					count = 0;
					Kuix.alert(Kuix.getMessage("ERRORAPP"), KuixConstants.ALERT_OK, "stopAction", null);
				}
				else{
					count++;
					Kuix.alert(Kuix.getMessage("ERRORINTNT"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "retry", "stopAction");
				}
				//ioe.printStackTrace();
				
			}catch(Exception e){
				Loader.runLoader = false;
				popUp.remove();
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
				Kuix.alert(Kuix.getMessage("CONNAPPEXIT"), KuixConstants.ALERT_OK, "exit", null);
				//Kuix.alert(e.toString());
			}finally{
				//System.out.println("System exceptions");
				if(popUp != null){
					Loader.runLoader = false;
					popUp.remove();
					popUp = null;
				}
				try{
					if(is != null){
						is.close();
					}if(httpConnection != null){
						httpConnection.close();
					}
				}catch(IOException ioe){
					Kuix.alert(Kuix.getMessage("ERRORSTREAMS")); 
				}
			}
		}
		
	}
}
