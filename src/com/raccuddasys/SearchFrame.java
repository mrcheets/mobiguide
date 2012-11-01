package com.raccuddasys;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.List;
import org.kalmeo.kuix.widget.ListItem;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.kuix.widget.TextField;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.frame.Frame;

import com.raccuddasys.services.Loader;
import com.raccuddasys.services.SearchXMLHandler;
import com.raccuddasys.services.SppListXMLHandler;

public class SearchFrame implements Frame, Runnable{

	String errorMessage;
	PopupBox popUp;
	SAXParser parser;
	HttpConnection httpConnection;
	String srchString;
	SearchXMLHandler sxh;
	InputStream is;
	int count = 0;
	List searchList;
	Widget lineBreak;
	Picture pic;
	Loader imgLoad;
	PopupBox helpPopup;
	protected Screen screen;
	
	public void onAdded() {
		// Load the content from the XML file with Kuix.loadScreen static method
		screen = Kuix.loadScreen("search.xml", null);

		// Set the application current screen
		screen.setCurrent();
		searchList = (List)screen.getWidget("searchlist");
		lineBreak = new Widget(KuixConstants.BREAK_WIDGET_TAG);
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		if ("back".equals(identifier)) {
			// remove the current frame from the framehandler stack
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();

			// do not propagate the message through the rest of the frame stack
			return false;
		}
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
		if ("search".equals(identifier)) {
			// get the midlet instance to invoke the Destroy() method
			srchString = ((TextField)screen.getWidget("searchkey")).getText().trim();
			Thread search_T = new Thread(this);
			search_T.start();
			//System.out.println("Search: "+srchString);
			return false;
		}
		if("species".equals(identifier)){
			//System.out.println("sppId "+arguments[0]);
			try{
				/*ListItem chosen = (ListItem) arguments[0];
				Kuix.alert(chosen.getId());*/
				Integer.parseInt((String) arguments[0]);
				SpeciesFrame.sppCurrent = (String)arguments[0];
				int infoId = SearchXMLHandler.sppIds.indexOf((String) arguments[0]);
				SppListXMLHandler.sppTitles.addElement((String) SearchXMLHandler.sppTitles.elementAt(infoId));
				SppListXMLHandler.sppNId.addElement((String) arguments[0]);
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
		if("retry".equals(identifier)){
			Thread search_T = new Thread(this);
			search_T.start();
			return false;
		}
		if("help".equals(identifier)){
			helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
			helpPopup.setObjectAttribute("style", "style=\"align:fill-center;layout:tablelayout;layout-data:sld(left,"+(CategoryFrame.innerWidth-80)+","+(CategoryFrame.innerHeight-80)+",)\"");
			TextArea message = (TextArea)helpPopup.getWidget("hlpMessage");
			message.setText(Kuix.getMessage("SEARCHHELP"));			
			return false;
		}
		if("closeHelp".equals(identifier)){
			helpPopup.remove();
			helpPopup = null;
			return false;
		}
		
		return false;
	}

	public void onRemoved() {
		// destroy the screen
        screen.cleanUp();
		// unreference the screen object to free the memory
        screen = null;
        if(SppListXMLHandler.sppTitles != null){
        	SppListXMLHandler.sppTitles.removeAllElements();
        }
        if(SppListXMLHandler.sppNId != null){
        	SppListXMLHandler.sppNId.removeAllElements();
        }
        if(SearchXMLHandler.sppIds != null){
        	SearchXMLHandler.sppIds = null;
        }
        if(SearchXMLHandler.sppTitles != null){
        	SearchXMLHandler.sppTitles = null;
        }		
        System.gc();
	}
	
	public boolean validateString(){
		if(srchString.equals("")){
			errorMessage = Kuix.getMessage("EMPTYSTRING");
			return false;
		}
		return true;
	}
	public void loadList(int location){
		ListItem li = new ListItem();
		li.setAttribute("style", "layout:tablelayout");
		if(location > -1){
			li.setOnAction("species("+(String)SearchXMLHandler.sppIds.elementAt(location)+")");
			Text titleText = new Text();
			titleText.setStyleClass("listtitle");
			titleText.setText((String)SearchXMLHandler.sppTitles.elementAt(location));
			//TextArea ta = new TextArea();
			//ta.setStyleClass("listinfo");
			//ta.setText((String)SearchXMLHandler.snippets.elementAt(location));
			li.add(titleText);
			//li.add(lineBreak);
			//li.add(ta);
		}else{
			Text titleText = new Text();
			titleText.setStyleClass("listtitle");
			titleText.setText(Kuix.getMessage("NORESULTS")+" \""+srchString+"\"");
			li.add(titleText);
		}
		searchList.add(li);
		
	}

	public void run() {
		if(!validateString()){
			Kuix.alert(errorMessage);
		}
		else{
			int rc = 0;
			try{
				popUp = Kuix.showPopupBox("progressPopup.xml", null);
				TextArea myText = (TextArea)popUp.getWidget("txtMessage");
				myText.setText(Kuix.getMessage("LOADSEARCH"));
				pic = (Picture)popUp.getWidget("picLoader");
				imgLoad = new Loader(pic);
				Thread imgLoop = new Thread(imgLoad);
				imgLoop.start();
				//System.out.println("This passed");
				parser = SAXParserFactory.newInstance().newSAXParser();
				httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/search/"+srchString);
				sxh = new SearchXMLHandler();
				
				rc = httpConnection.getResponseCode();
				if(rc != HttpConnection.HTTP_OK){
					throw new IOException();
				}
				is = httpConnection.openInputStream();
				parser.parse(is, sxh);
				searchList.removeAllItems();
				int infoList = SearchXMLHandler.sppIds.size();
				if(infoList <= 0){
					loadList(-1);
				}else{
					for(int i = 0; i < infoList; i++){
						loadList(i);
					}
				}
			}catch(Exception ioe){
				Loader.runLoader = false;
				popUp.remove();
				popUp = null;
				if(count >= 3){
					count = 0; 
					try{
						Thread.sleep(100);
					}catch(InterruptedException ie){
						
					}
					Kuix.alert(Kuix.getMessage("SEARCHBACK"), KuixConstants.ALERT_OK, "back", null);
				}else{
					count++;
					Kuix.alert(Kuix.getMessage("ERRORSEARCH"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "retry", "back");
					SppListXMLHandler.sppTitles.removeAllElements();
					SppListXMLHandler.sppNId.removeAllElements();
					SearchXMLHandler.sppIds.removeAllElements();
					SearchXMLHandler.sppTitles.removeAllElements();
					//ioe.printStackTrace();
				}
			}finally{
				try{
					if(is != null){
						is.close();
					}if(httpConnection != null){
						httpConnection.close();
					}
					System.gc();
				}catch(IOException ioe){
					Kuix.alert("Error closing http and input streams"); 
				}
			}
			if(popUp != null){
				Loader.runLoader = false;
				popUp.remove();
				popUp = null;
			}
			
		}		
	}

}
