package com.raccuddasys;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
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
import com.raccuddasys.services.SppListXMLHandler;

public class SpeciesListFrame implements Frame, Runnable{

	String category;
	SAXParser parser;
	HttpConnection httpConnection;
	SppListXMLHandler sppXMLListHandler;
	PopupBox popUp;
	PopupBox helpPopup;
	Screen screen;
	CategoryDataProvider cdProvider;
	ListDataProvider listDataProvider;
	InputStream is;
	int count = 0;
	boolean continueDownload = true;
	Picture pic;
	Loader imgLoad;
	static boolean stopped = false;
	
	int tnailWidth;
	int tnailHeight;
	
	public SpeciesListFrame(){
		super();
	}
	public SpeciesListFrame(String category){
		super();
		this.category = category;
		cdProvider = new CategoryDataProvider();
		
		
	}
	public void onAdded() {
		// Load the content from the XML file with Kuix.loadScreen static method
		screen = Kuix.loadScreen("speciesList.xml", cdProvider);

		// Set the application current screen
		screen.setCurrent();
		//CategoryFrame.innerWidth = screen.getInnerWidth();
		//CategoryFrame.innerHeight = screen.getInnerHeight();
		//System.out.println("Stopped:"+stopped);
		if(stopped){
			stopped = false;
			SppListXMLHandler.sppTitles.removeAllElements();
			SppListXMLHandler.sppNId.removeAllElements();
			SppListXMLHandler.sppSubTitles.removeAllElements();
			SppListXMLHandler.sppTnailPath.removeAllElements();
			SppListXMLHandler.sppScientific.removeAllElements();
			if(cdProvider !=null){
				cdProvider.removeAllItems("sppListData");
			}
		}
		//System.out.println(SppListXMLHandler.sppTitles.size());
		tnailWidth = (int)CategoryFrame.innerWidth/5;
		loadSpecies();
		
	}
	public void loadSpecies(){
		Thread getSpecies_T = new Thread(this);
		getSpecies_T.start();
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
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
		}
		if("retry".equals(identifier)){
			Thread getSpecies_T = new Thread(this);
			getSpecies_T.start();
			return false;
		}
		if ("categories".equals(identifier)) {
			// remove the current frame from the framehandler stack
			continueDownload = false;
			Loader.runLoader = false;
			SppListXMLHandler.sppTitles.removeAllElements();
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();

			// do not propagate the message through the rest of the frame stack
			return false;
		}
		if("stopAction".equals(identifier)){
			continueDownload = false;
			Loader.runLoader = false;
			stopped = true;
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();

			// do not propagate the message through the rest of the frame stack
			return false;
		}
		if("species".equals(identifier)){
			//System.out.println("sppId "+arguments[0]);
			try{
				/*ListItem chosen = (ListItem) arguments[0];
				Kuix.alert(chosen.getId());*/
				Integer.parseInt((String) arguments[0]);
				SpeciesFrame.sppCurrent = (String)arguments[0];
				//System.out.println("sppId "+arguments[0]);
				Kuix.getFrameHandler().pushFrame(new SpeciesFrame((String) arguments[0]));
			}catch(NumberFormatException nfe){
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
				Kuix.alert("Input error. Please give a valid species Id to go to");
			}
			return false;
		}
		if("help".equals(identifier)){
			helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
			TextArea message = (TextArea)helpPopup.getWidget("hlpMessage");
			message.setText(Kuix.getMessage("GENERALHELP"));
			return false;
		}
		if("closeHelp".equals(identifier)){
			helpPopup.remove();
			helpPopup = null;
			return false;
		}
		//Kuix.alert((String)identifier);
		return false;
	}

	public void onRemoved() {
		screen.cleanUp();
		screen = null;
		SppListXMLHandler.sppTitles.removeAllElements();
		SppListXMLHandler.sppNId.removeAllElements();
		SppListXMLHandler.sppSubTitles.removeAllElements();
		SppListXMLHandler.sppTnailPath.removeAllElements();
		SppListXMLHandler.sppScientific.removeAllElements();
		cdProvider = null;
		System.gc();
	}
	public void run() {
		int rc = 0;
		//System.out.println("Starting run");
		if(SppListXMLHandler.sppTitles.size() != 0){
			//Do nothing
		}
		else{
			try{
				//System.out.println("sppTitles size is 0");
				popUp = Kuix.showPopupBox("progressPopup.xml", null); //Kuix.alert(Kuix.getMessage("LOADSPPLIST"), KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "categories");
				TextArea msg = (TextArea)popUp.getWidget("txtMessage");
				msg.setText(Kuix.getMessage("LOADSPPLIST"));
				pic = (Picture)popUp.getWidget("picLoader");
				imgLoad = new Loader(pic);
				Thread imgLoop = new Thread(imgLoad);
				imgLoop.start();
				parser = SAXParserFactory.newInstance().newSAXParser();
				httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/category/"+category);
				rc = httpConnection.getResponseCode();
				if(rc != HttpConnection.HTTP_OK){
					throw new IOException();
				}
				//System.out.println("Connected to website, downloading stuff");
				is = httpConnection.openInputStream();
				sppXMLListHandler = new SppListXMLHandler();
				//System.out.println("Completed download parsing");
				parser.parse(is, sppXMLListHandler);
				//System.out.println("Completed parsing");
				int info = SppListXMLHandler.sppNId.size();
				//System.out.println("int Info"+ info);
				//System.out.println("Filling up dataProvider");
				for(int i = 0; i < info; i++){
					if(continueDownload){
						listDataProvider = new ListDataProvider();
						//System.out.println("Within for, i: "+i);
						listDataProvider.setSppId((String)SppListXMLHandler.sppNId.elementAt(i));
						//System.out.println("Set Id");
						listDataProvider.setSppScientific((String)SppListXMLHandler.sppScientific.elementAt(i));
						//System.out.println("Set scientific");
						listDataProvider.setSppSubTitle((String)SppListXMLHandler.sppSubTitles.elementAt(i));
						//System.out.println("Set subtitle");
						//listDataProvider.setSppThumbnail((String)SppXMLHandler.sppTnailPath.elementAt(i));
						listDataProvider.setSppTitle((String)SppListXMLHandler.sppTitles.elementAt(i));
						//System.out.println("Set title");
						listDataProvider.setVectorId(new Integer(i).toString());
						httpConnection.close();
						is.close();
						//System.out.println("Dataprovider filled, downloading thumbnail");
						int index = ((String)SppListXMLHandler.sppTnailPath.elementAt(i)).lastIndexOf('/')+1;
						String thumbNail = ((String)SppListXMLHandler.sppTnailPath.elementAt(i)).substring(index);
						httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/getspplistthumbnail/"+thumbNail+"/"+tnailWidth);
						rc = httpConnection.getResponseCode();
						if(rc != HttpConnection.HTTP_OK){
							throw new IOException();
						}
						is = httpConnection.openInputStream();
						Image sppImage = Image.createImage(is);
						/*int imgHeight = sppImage.getHeight();
						int imgWidth = sppImage.getWidth();
						int resizedHeight;
						int offsetX = 0;
						int offsetY = 0;
						int tempWidth = 0;
						//System.out.println("tnailWidth:"+tnailWidth+" imgWidth:"+imgWidth);
						if(imgWidth > tnailWidth){
							resizedHeight = (imgHeight*tnailWidth)/imgWidth;
							offsetX = (imgWidth - tnailWidth)/2;
							offsetY = (imgHeight - resizedHeight)/2;
							tempWidth = tnailWidth;
						}else{
							tempWidth = tnailWidth;
							tnailWidth = imgWidth;
							resizedHeight = imgHeight;
						}
						Image sppImageResized = Image.createImage(sppImage,offsetX, offsetY,tnailWidth,resizedHeight, 0);
						tnailWidth = tempWidth;*/
						//System.out.println("Set thumbnail");
						listDataProvider.setSppImage(sppImage);
		
						cdProvider.addItem("sppListData", listDataProvider);
					}
				}
				listDataProvider = null;
				//System.out.println("Data provider filled, info displayed");
				Loader.runLoader = false;
				popUp.remove();
				popUp = null;
				//Kuix.alert("Elements:"+info);
				if(continueDownload){
					if(info == 0){
						try{
							Thread.sleep(300);
						}catch(InterruptedException ie){
							
						}
						Kuix.alert(Kuix.getMessage("NOSPP"), KuixConstants.ALERT_OK , "categories", null);
						
					}
					//screen.invalidate();
				}
			}catch(Exception ioe){
				Loader.runLoader = false;
				popUp.remove();
				popUp = null;
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
				if(count >= 3){
					Kuix.alert(Kuix.getMessage("APPEXIT"), KuixConstants.ALERT_OK, "exit", null);
				}else{
					count++;
					Kuix.alert(Kuix.getMessage("ERRORSPPLIST"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "retry", "categories");
					SppListXMLHandler.sppTitles.removeAllElements();
					SppListXMLHandler.sppNId.removeAllElements();
					SppListXMLHandler.sppSubTitles.removeAllElements();
					SppListXMLHandler.sppTnailPath.removeAllElements();
					SppListXMLHandler.sppScientific.removeAllElements();
					cdProvider = null;
					System.out.println("Error here");
					ioe.printStackTrace();
				}
			}
			finally{
				try{
					if(is != null){
						is.close();
					}if(httpConnection != null){
						httpConnection.close();
					}
				}catch(IOException ioe){
					try{
						Thread.sleep(300);
					}catch(InterruptedException ie){
						
					}
					Kuix.alert(Kuix.getMessage("ERRORSTREAMS")); 
				}
			}
		}
		
	}

}
