package com.raccuddasys;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.List;
import org.kalmeo.kuix.widget.ListItem;
import org.kalmeo.kuix.widget.Menu;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.ScrollPane;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.kuix.widget.TextField;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.frame.Frame;

import com.raccuddasys.services.Loader;
import com.raccuddasys.services.SppGalleryHandler;
import com.raccuddasys.services.SppListXMLHandler;
import com.raccuddasys.services.SppTextHandler;
import com.raccuddasys.services.SppXMLHandler;

public class SpeciesFrame implements Frame, Runnable{

	Screen screen;
	public static String command;
	public static String label;
	PopupBox popUp;
	PopupBox helpPopup;
	String sppId;
	InputStream is;
	//HttpConnection httpConnection;
	SppXMLHandler sppXMLHandler;
	SppGalleryHandler sppGalleryHandler;
	SAXParser parser;
	Menu firstMenu;
	Menu secondMenu;
	int count = 0;
	int vectorId;
	int tnailWidth;
	ScrollPane scrollPane;
	Widget lineBreak;
	Picture pic;
	Loader imgLoad;
				
	public static String sppCurrent;
	
	//public static boolean fromGallery = false;
	boolean firstRun = true;
	boolean firstShow = true;
	boolean continueDownload = true;
	public static String chooseText;
	public static String address;
	public static TextField addressField;
	public static CheckBox saveBox;
	public static PopupBox infoBox;
	boolean onMain = false;
			
	public SpeciesFrame(){
		super();
	}
	public SpeciesFrame(String sppId){
		super();
		this.sppId = sppId;
		vectorId = SppListXMLHandler.sppNId.indexOf(sppId);
		tnailWidth = (int)CategoryFrame.innerWidth/5;
		//Thread firstRun_T = new Thread(this);
		//firstRun_T.start();
	}
	public void onAdded() {
		screen = Kuix.loadScreen("species.xml", null);

		// Set the application current screen
		screen.setCurrent();
		
		lineBreak = new Widget(KuixConstants.BREAK_WIDGET_TAG);
		/*if(firstRun){
			
		}else{
			//This is called when we destroy imageDisplay and Video display frames
			//SInce first run is false, we give the direction to load gallery
			//command = "gallery";
			//Strike that, we load anything that command has instead of defaulting to gallery
			Thread loadSpp_T = new Thread(this);
			loadSpp_T.start();
		}*/
		firstMenu = screen.getFirstMenu();
		secondMenu = screen.getSecondMenu();
		//System.out.println("OnAction: "+secondMenu.getOnAction());
		//System.out.println(secondMenu);
		
		Thread loadSpp_T = new Thread(this);
		loadSpp_T.start();
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
		if("loadMain".equals(identifier)){
			if(onMain == false){
				onMain = true;
				command = "mainSppPage";
				label = Kuix.getMessage("MAINPAGE"); //"Main Page";
			}else{
				command = "sppList";
			}
			
			Thread loadMainPage_T = new Thread(this);
			loadMainPage_T.start();
			return false;
		}
		if("search".equals(identifier)){
			Kuix.getFrameHandler().pushFrame(new SearchFrame());
			return false;
		}
		if("sppList".equals(identifier) || "stopAction".equals(identifier)){
			continueDownload = false;
			Loader.runLoader = false;
			Kuix.getFrameHandler().removeFrame(this);
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if("geninfo".equals(identifier)){
			onMain = false;
			command = "geninfo";
			label = Kuix.getMessage("GENINFO"); //"general information";
			Thread loadGeneralInfo_T = new Thread(this);
			loadGeneralInfo_T.start();
			return false;
		}
		if("gallery".equals(identifier)){
			//screen.removeAll();
			onMain = false;
			command = "gallery";
			label = "gallery";
			Thread loadGallery_T = new Thread(this);
			loadGallery_T.start();
			
			return false;
		}
		if("identification".equals(identifier)){
			//screen.removeAll();
			onMain = false;
			command = "identification";
			label = Kuix.getMessage("IDTRAITS"); //"identification traits";
			Thread loadIdentification_T = new Thread(this);
			loadIdentification_T.start();
			return false;
		}
		if("conservation".equals(identifier)){
			//screen.removeAll();
			onMain = false;
			command = "conservation";
			label = Kuix.getMessage("CONSFACTORS"); //"conservation factors";
			Thread loadConservation_T = new Thread(this);
			loadConservation_T.start();
			return false;
		}
		if("facts".equals(identifier)){
			//screen.removeAll();
			onMain = false;
			command = "facts";
			label = Kuix.getMessage("FACTS"); //"species facts";
			Thread loadFacts_T = new Thread(this);
			loadFacts_T.start();
			return false;
		}
		if("call".equals(identifier)){
			onMain = false;
			if(SppXMLHandler.videoCall == null){
				Kuix.alert(Kuix.getMessage("SORRYNOCALL"));
				//System.out.println("No call");
			}
			else{
				//System.out.println("There is a call");
				firstShow = true;
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
				popUp = Kuix.alert(Kuix.getMessage("ASKDOWNLOAD"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "reallyCall", null);
			}
			//System.out.println("Call called");
			return false;
		}
		if("reallyCall".equals(identifier)){
			try{
				Thread.sleep(300);
			}catch(InterruptedException ie){
				
			}
			popUp.remove();
			popUp = null;
			Kuix.getFrameHandler().pushFrame(new VideoDisplayFrame());
		}
		if("prevSpp".equals(identifier)){
			onMain = false;
			
			if(vectorId > 0){
				command = "";
				vectorId--;
				sppId = (String)SppListXMLHandler.sppNId.elementAt(vectorId);
				firstRun = true;
				label = "Previous species";
				SppTextHandler.idText.removeAllElements();
				SppTextHandler.conservationText.removeAllElements();
				SppTextHandler.factsText.removeAllElements();
				SppTextHandler.bodyTextText.removeAllElements();
				SppXMLHandler.videoCall = null;
				SppXMLHandler.audioCall = null;
				Thread loadPrevSpp_T = new Thread(this);
				loadPrevSpp_T.start();
			}
			else{
				Kuix.alert(Kuix.getMessage("FIRSTSPP"));
				/*if(fromGallery){
					fromGallery = false;
					command = tempCommand;
					Thread loadPrevSpp_T = new Thread(this);
					loadPrevSpp_T.start();
				}*/
			}
			return false;
		}
		if("nextSpp".equals(identifier)){
			onMain = false;
			int maxSpp = SppListXMLHandler.sppNId.size(); 
			if(vectorId > -1 && vectorId < maxSpp - 1){
				command = "";
				vectorId++;
				sppId = (String)SppListXMLHandler.sppNId.elementAt(vectorId);
				firstRun = true;
				label = "next species";
				SppTextHandler.idText.removeAllElements();
				SppTextHandler.conservationText.removeAllElements();
				SppTextHandler.factsText.removeAllElements();
				SppTextHandler.bodyTextText.removeAllElements();
				SppXMLHandler.videoCall = null;
				SppXMLHandler.audioCall = null;
				Thread loadNextSpp_T = new Thread(this);
				loadNextSpp_T.start();
				
			}else{
				Kuix.alert(Kuix.getMessage("LASTSPP"));
				/*if(fromGallery){
					fromGallery = false;
					command = tempCommand;
					Thread loadNextSpp_T = new Thread(this);
					loadNextSpp_T.start();
				}*/
			}
			return false;
		}
		if("photo".equals(identifier)){
			onMain = false;
			firstShow = false;
			//System.out.println((String)SppGalleryHandler.fullImagePaths.elementAt(Integer.parseInt((String)arguments[0])));
			//Kuix.alert("Ih: "+screen.getInnerHeight()+"X Iw: "+screen.getInnerWidth());
			try{
				ImageDisplayFrame.imgCurrent = Integer.parseInt((String)arguments[0]);
				Kuix.getFrameHandler().pushFrame(new ImageDisplayFrame());
			}
			catch(NumberFormatException nfe){
				Kuix.alert("Cannot start gallery. Error in image id");
			}
			return false;
		}
		if("retry".equals(identifier)){
			Thread firstRun_T = new Thread(this);
			firstRun_T.start();
		}
		if("help".equals(identifier)){
			helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
			helpPopup.setObjectAttribute("style", "style=\"align:fill-center;layout:tablelayout;layout-data:sld(left,"+(CategoryFrame.innerWidth-80)+","+(CategoryFrame.innerHeight-80)+",)\"");
			TextArea message = (TextArea)helpPopup.getWidget("hlpMessage");
			message.setText(Kuix.getMessage("SPPHELP"));
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
		screen.cleanUp();
		screen = null;
		SppXMLHandler.sppTitle = null;
		SppXMLHandler.subTitle = null;
		SppXMLHandler.scientific = null;
		SppXMLHandler.tnailPath = null;
		SppXMLHandler.albumTid = null;
		SppXMLHandler.identification = null;
		SppXMLHandler.conservation = null;
		SppXMLHandler.facts = null;
		SppXMLHandler.call = null;
		SppXMLHandler.bodyText = null;
		SppXMLHandler.videoCall = null;
		SppXMLHandler.audioCall = null;
		SppGalleryHandler.tnailImagePaths.removeAllElements();
		SppGalleryHandler.fullImagePaths.removeAllElements();
		SppGalleryHandler.bodyText.removeAllElements();
		SppGalleryHandler.photoTitles.removeAllElements();
		SppGalleryHandler.tnailImages.removeAllElements();
		//scrollPane = null;
		SppTextHandler.idText.removeAllElements();
		SppTextHandler.conservationText.removeAllElements();
		SppTextHandler.factsText.removeAllElements();
		SppTextHandler.bodyTextText.removeAllElements();
		//sppXMLHandler = null;
		//sppGalleryHandler = null;
		//parser = null;
		//firstRun = true;
		//lineBreak = null;
		System.gc();
		
	}
	
	/*======
	 * In all the next Functions:
	 * 1. Clear the screen widget screen.removeAll();
	 * 2. Clear the dataprovider and reload it (loadXml()?)
	 * 3. Use new thread
	 */
	public void loadMainSppPage(){
		screen = Kuix.loadScreen("species.xml", null);
		screen.setCurrent();
		scrollPane= new ScrollPane();
		scrollPane.setShowScrollBar(true);
		scrollPane.setStyleClass("containerStyle");
		Kuix.loadMenuContent(screen.getFirstMenu(),"rightMenuMainSpp.xml", null);
		List list = new List();
		list.setId("mainsppPageList");
		ListItem genInfo = new ListItem();
		genInfo.setStyleClass("species");
		genInfo.setOnAction("geninfo");
		TextArea textGenInfo = new TextArea();
		textGenInfo.setText(Kuix.getMessage("SPP_GENINFO"));
		genInfo.add(textGenInfo);
		list.add(genInfo);
		ListItem id = new ListItem();
		id.setStyleClass("species");
		id.setOnAction("identification");
		TextArea textArea = new TextArea();
		textArea.setText(Kuix.getMessage("SPP_ID"));
		id.add(textArea);
		list.add(id);
		ListItem cons = new ListItem();
		cons.setStyleClass("species");
		cons.setOnAction("conservation");
		TextArea textCons = new TextArea();
		textCons.setText(Kuix.getMessage("SPP_CONSERVATION"));
		cons.add(textCons);
		list.add(cons);
		ListItem gallery = new ListItem();
		gallery.setStyleClass("species");
		gallery.setOnAction("gallery");
		TextArea textGallery = new TextArea();
		textGallery.setText(Kuix.getMessage("SPP_GALLERY"));
		gallery.add(textGallery);
		list.add(gallery);
		ListItem facts = new ListItem();
		facts.setStyleClass("species");
		facts.setOnAction("facts");
		TextArea textFacts = new TextArea();
		textFacts.setText(Kuix.getMessage("SPP_INTERESTING"));
		facts.add(textFacts);
		list.add(facts);
		ListItem video = new ListItem();
		video.setStyleClass("species");
		video.setOnAction("call");
		TextArea textVideo = new TextArea();
		textVideo.setText(Kuix.getMessage("SPP_CALL"));
		video.add(textVideo);
		list.add(video);
		TextArea title = new TextArea();
		title.setStyleClass("title");
		title.setText((String)SppListXMLHandler.sppTitles.elementAt(vectorId));
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP"));
		scrollPane.add(breadcrumb);
		scrollPane.add(title);
		scrollPane.add(list);
		screen.add(scrollPane);		
	}
	
	public void loadGallery(){
		/*TextArea txt = new TextArea();
		txt.setStyled(true);
		String mambos = SppXMLHandler.albumTid+"<br/>";
		mambos+=SppXMLHandler.tnailPath+"<br/>";
		mambos+=SppXMLHandler.bodyText+"<br/>";
		mambos+=SppXMLHandler.conservation+"<br/>";
		mambos+=SppXMLHandler.videoCall;
		txt.setText("Loaded Gallery<br/> "+mambos);*/
		//System.out.println("Loading gallery");
		
		//screen.cleanUpChildren();
		//screen.removeAll();
		screen = Kuix.loadScreen("species.xml", null);
		screen.setCurrent();
		scrollPane= new ScrollPane();
		scrollPane.setShowScrollBar(true);
		scrollPane.setStyleClass("containerStyle");
		Kuix.loadMenuContent(screen.getFirstMenu(),"rightMenuSpp.xml", null);
		//screen.add(list);
		//screen.removeAll();
		/*GridLayout gridLayout = new GridLayout(3,3);
		gridLayout.doLayout(list);*/
		
		popUp = Kuix.alert(Kuix.getMessage("LOAD")+" "+label);
		List list = new List();
		list.setId("myList");
		//list.setStyleClass("gridStyle");
		int images = SppGalleryHandler.tnailImagePaths.size();
		for(int i = 0; i < images; i++){
			ListItem item = new ListItem();
			item.setId(new Integer(i).toString());
			Picture picture = new Picture();
			TextArea textArea = new TextArea();
			textArea.setText((String)SppGalleryHandler.photoTitles.elementAt(i));
			textArea.setEnabled(false);
			picture.setAttribute("padding", "5");
			picture.setImage((Image)SppGalleryHandler.tnailImages.elementAt(i));
			item.setOnAction("photo("+i+")");
			
			item.add(picture);
			item.add(textArea);
			list.add(item);
		}
		TextArea title = new TextArea();
		title.setStyleClass("spptitle");
		title.setText((String)SppListXMLHandler.sppTitles.elementAt(vectorId)+": "+Kuix.getMessage("SPP_GALLERY"));
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP")+">"+Kuix.getMessage("SPP_GALLERY"));
		scrollPane.add(breadcrumb);
		scrollPane.add(title);
		scrollPane.add(list);
		screen.add(scrollPane);
		try{
			Thread.sleep(300);
		}catch(InterruptedException is){
			
		}
		Loader.runLoader = false;
		popUp.remove();
		popUp = null;
		firstShow = true;
		screen.invalidate();

		
	}
	public void loadGenInfo(){
		screen = Kuix.loadScreen("species.xml", null);
		screen.setCurrent();
		scrollPane = new ScrollPane();
		scrollPane.setShowScrollBar(true);
		scrollPane.setStyleClass("containerStyle");
		screen.add(scrollPane);
		TextArea title = new TextArea();
		title.setStyleClass("spptitle");
		title.setText((String)SppListXMLHandler.sppTitles.elementAt(vectorId)+": "+Kuix.getMessage("SPP_GENINFO"));
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP")+">"+Kuix.getMessage("SPP_GENINFO"));
		scrollPane.add(breadcrumb);
		scrollPane.add(title);
		Kuix.loadMenuContent(screen.getFirstMenu(),"rightMenuSpp.xml", null);
		popUp = Kuix.alert(Kuix.getMessage("LOAD")+" "+label);
		if(SppTextHandler.bodyTextText != null){
			int count = SppTextHandler.bodyTextText.size();
			for(int i = 0; i < count; i++){
				Vector temp = (Vector)SppTextHandler.bodyTextText.elementAt(i);
				if(temp != null){
					int elemCount = temp.size();
					for(int j = 0; j < elemCount; j++){
						TextArea textArea = new TextArea();
						String tempString = (String)temp.elementAt(j);
						if(!tempString.equals("")){
							textArea.setText(tempString);
							if(j == 0){
								scrollPane.add(lineBreak);
								textArea.setStyleClass("headings");
							}else{
								textArea.setStyleClass("infotxt");
							}
							scrollPane.add(textArea);
							scrollPane.add(lineBreak);
						}
					}
				}
			}
		}
		try{
			Thread.sleep(300);
		}catch(InterruptedException is){
			
		}
		//Loader.runLoader = false;
		popUp.remove();
		popUp = null;
		screen.invalidate();
	}
	public void loadConservation(){
		//TextArea txt = new TextArea();
		//scrollPane = new ScrollPane();
		screen = Kuix.loadScreen("species.xml", null);
		screen.setCurrent();
		scrollPane = new ScrollPane();
		scrollPane.setShowScrollBar(true);
		scrollPane.setStyleClass("containerStyle");
		screen.add(scrollPane);
		TextArea title = new TextArea();
		title.setStyleClass("spptitle");
		title.setText((String)SppListXMLHandler.sppTitles.elementAt(vectorId)+": "+Kuix.getMessage("SPP_CONSERVATION"));
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP")+">"+Kuix.getMessage("SPP_CONSERVATION"));
		scrollPane.add(breadcrumb);
		scrollPane.add(title);
		Kuix.loadMenuContent(screen.getFirstMenu(),"rightMenuSpp.xml", null);
		popUp = Kuix.alert(Kuix.getMessage("LOAD")+" "+label);
		//txt.setEnabled(false);
		//txt.setStyled(true);
		//txt.setText(SppXMLHandler.conservation);
		if(SppTextHandler.conservationText != null){
			int count = SppTextHandler.conservationText.size();
			for(int i = 0; i < count; i++){
				Vector temp = (Vector)SppTextHandler.conservationText.elementAt(i);
				if(temp != null){
					int elemCount = temp.size();
					for(int j = 0; j < elemCount; j++){
						TextArea textArea = new TextArea();
						String tempString = (String)temp.elementAt(j);
						if(!tempString.equals("")){
							textArea.setText(tempString);
							if(j == 0){
								scrollPane.add(lineBreak);
								textArea.setStyleClass("headings");
							}else{
								textArea.setStyleClass("infotxt");
							}
							scrollPane.add(textArea);
							scrollPane.add(lineBreak);
						}
					}
				}
			}
		}
		try{
			Thread.sleep(300);
		}catch(InterruptedException is){
			
		}
		Loader.runLoader = false;
		popUp.remove();
		popUp = null;
		screen.invalidate();
		
	}
	public void loadInterestingFacts(){
		//TextArea txt = new TextArea();
		//txt.setEnabled(false);
		//txt.setStyled(true);
		//txt.setText(SppXMLHandler.facts);
		
		screen = Kuix.loadScreen("species.xml", null);
		screen.setCurrent();
		scrollPane = new ScrollPane();
		scrollPane.setShowScrollBar(true);
		scrollPane.setStyleClass("containerStyle");
		screen.add(scrollPane);
		TextArea title = new TextArea();
		title.setStyleClass("spptitle");
		title.setText((String)SppListXMLHandler.sppTitles.elementAt(vectorId)+": "+Kuix.getMessage("SPP_INTERESTING"));
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP")+">"+Kuix.getMessage("SPP_INTERESTING"));
		scrollPane.add(breadcrumb);
		scrollPane.add(title);
		Kuix.loadMenuContent(screen.getFirstMenu(),"rightMenuSpp.xml", null);
		popUp = Kuix.alert(Kuix.getMessage("LOAD")+" "+label);
		if(SppTextHandler.factsText != null){
			int count = SppTextHandler.factsText.size();
			for(int i = 0; i < count; i++){
				Vector temp = (Vector)SppTextHandler.factsText.elementAt(i);
				if(temp != null){
					int elemCount = temp.size();
					for(int j = 0; j < elemCount; j++){
						TextArea textArea = new TextArea();
						String tempString = (String)temp.elementAt(j);
						if(!tempString.equals("")){
							textArea.setText(tempString);
							if(j == 0){
								scrollPane.add(lineBreak);
								textArea.setStyleClass("headings");
							}else{
								textArea.setStyleClass("infotxt");
							}
							scrollPane.add(textArea);
							scrollPane.add(lineBreak);
						}
					}
				}
			}
		}
		
		try{
			Thread.sleep(300);
		}catch(InterruptedException is){
			
		}
		popUp.remove();
		popUp = null;
		screen.invalidate();
		
	}
	public void loadIdentification(){
		//TextArea txt = new TextArea();
		//txt.setEnabled(false);
		//txt.setStyled(true);
		//txt.setText(SppXMLHandler.identification);
		screen = Kuix.loadScreen("species.xml", null);
		screen.setCurrent();
		//screen.add(scrollPane);
		Kuix.loadMenuContent(screen.getFirstMenu(),"rightMenuSpp.xml", null);
		//loadedNormalMenu = true;
		
		scrollPane = new ScrollPane();
		scrollPane.setShowScrollBar(true);
		scrollPane.setStyleClass("containerStyle");
		//screen.removeAll();
		screen.add(scrollPane);
		popUp = Kuix.alert(Kuix.getMessage("LOAD")+" "+label);
		TextArea title = new TextArea();
		title.setStyleClass("spptitle");
		title.setText((String)SppListXMLHandler.sppTitles.elementAt(vectorId)+": "+Kuix.getMessage("SPP_ID"));
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP")+">"+Kuix.getMessage("SPP_ID"));
		scrollPane.add(breadcrumb);
		scrollPane.add(title);
		if(SppTextHandler.idText != null){
			int count = SppTextHandler.idText.size();
			//System.out.println("Count: "+count);
			for(int i = 0; i < count; i++){
				//System.out.println("Var i: "+i);
				Vector temp = (Vector)SppTextHandler.idText.elementAt(i);
				if(temp != null){
					int elemCount = temp.size();
					//System.out.println("ElemCount: "+elemCount);
					for(int j = 0; j < elemCount; j++){
						//System.out.println("Var j: "+j);
						TextArea textArea = new TextArea();
						String tempString = (String)temp.elementAt(j);
						if(!tempString.equals("")){
							//System.out.println(tempString);
							textArea.setText(tempString);
							if(j == 0){
								scrollPane.add(lineBreak);
								textArea.setStyleClass("headings");
							}else{
								textArea.setStyleClass("infotxt");
							}
							scrollPane.add(textArea);
							//System.out.println("Added text area: "+textArea.getText());
							scrollPane.add(lineBreak);
							
						}
					}
				}
			}
		}
		//System.out.println("Out of the loop");
		try{
			Thread.sleep(300);
		}catch(InterruptedException is){
			
		}
		
		popUp.remove();
		popUp = null;
		screen.invalidate();
		
	}
	
	public void run() {
		//System.out.println("Starting run");
		HttpConnection httpConnection;
		if(firstRun){
			//System.out.println("First run");
			int rc = 0;
			try{				
				String message = Kuix.getMessage("LOAD");
				message += " "+Kuix.getMessage("SPP");
				popUp = Kuix.showPopupBox("progressPopup.xml", null);//Kuix.alert(message+"....", KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "sppList");
				TextArea msg = (TextArea) popUp.getWidget("txtMessage");
				msg.setText(message+"....");
				pic = (Picture)popUp.getWidget("picLoader");
				imgLoad = new Loader(pic);
				Thread imgLoop = new Thread(imgLoad);
				imgLoop.start();
				//System.out.println("Image loop started");
				parser = SAXParserFactory.newInstance().newSAXParser();
				httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/species/"+sppId);
				sppXMLHandler = new SppXMLHandler();
				rc = httpConnection.getResponseCode();
				if(rc != HttpConnection.HTTP_OK){
					throw new IOException();
				}
				is = httpConnection.openInputStream();
				parser.parse(is, sppXMLHandler);
				if(httpConnection != null){
					httpConnection.close();
				}
				if(is != null){
					is.close();
				}
				//System.out.println("Downloaded and parsed speceis xml");
				//popUp.remove();
				//popUp = null;
				//popUp = Kuix.alert(Kuix.getMessage("FORMATING"), KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "sppList");
				msg.setText(Kuix.getMessage("FORMATING"));
				//System.out.println("Unformatted text\nBodyTExt:"+SppXMLHandler.bodyText);
				//System.out.println("Conservation:"+SppXMLHandler.conservation);
				//System.out.println("Identification:"+SppXMLHandler.identification);
				//System.out.println("Facts:"+SppXMLHandler.facts);
				//String formated = "";
				//format identification
				//System.out.println("Unformatted id: "+SppXMLHandler.identification);
				//int index = SppXMLHandler.identification.indexOf("&lt;");
				
				/*while(index != -1){
					formated = SppXMLHandler.identification.substring(0, index);
					formated += SppXMLHandler.identification.substring(index+4);
					SppXMLHandler.identification = formated;
					index = SppXMLHandler.identification.indexOf("&lt;");
				}
				
				index = SppXMLHandler.identification.indexOf("&gt;");
				while(index != -1){
					formated = SppXMLHandler.identification.substring(0, index);
					formated += SppXMLHandler.identification.substring(index+4);
					SppXMLHandler.identification = formated;
					index = SppXMLHandler.identification.indexOf("&gt;");
				}
				System.out.println("Formatted id: "+SppXMLHandler.identification);*/
				String xmlId = "<text>"+SppXMLHandler.identification+"</text>";
				ByteArrayInputStream bis = new ByteArrayInputStream(xmlId.getBytes());
				chooseText = "identification"; //Always put thos before initializing SppTextHandler
				SppTextHandler sppText = new SppTextHandler();
				parser.parse(bis, sppText);
				
				//sppText = null;
				//System.out.println("Fromatted text");
				
				//format conservation
				xmlId = "<text>"+SppXMLHandler.conservation+"</text>";
				bis = new ByteArrayInputStream(xmlId.getBytes());
				chooseText = "conservation";
				//sppText = new SppTextHandler();
				parser.parse(bis, sppText);
				
				//sppText = null;					
				
				//format facts
				xmlId = "<text>"+SppXMLHandler.facts+"</text>";
				bis = new ByteArrayInputStream(xmlId.getBytes());
				chooseText = "facts";
				//sppText = new SppTextHandler();
				parser.parse(bis, sppText);
				//sppText = null;					
					
				//format body text
				xmlId = "<text>"+SppXMLHandler.bodyText+"</text>";
				bis = new ByteArrayInputStream(xmlId.getBytes());
				chooseText = "bodyText";
				//sppText = new SppTextHandler();
				parser.parse(bis, sppText);
				
				sppText = null;
					
				//formated = null;
				//popUp.remove();
				//popUp = null;
				//System.out.println("All Vectors filled. Displaying info");
				/*System.out.println("Identification");
				int idCount = SppTextHandler.idText.size();
				for(int k = 0; k < idCount; k++){
					Vector temp = (Vector)SppTextHandler.idText.elementAt(k);
					int size = temp.size();
					for(int p = 0; p < size; p++){
						System.out.println(temp.elementAt(p));
					}
				}
				//System.out.println("\nConservation");
				idCount = SppTextHandler.conservationText.size();
				for(int k = 0; k < idCount; k++){
					Vector temp = (Vector)SppTextHandler.conservationText.elementAt(k);
					int size = temp.size();
					for(int p = 0; p < size; p++){
						System.out.println(temp.elementAt(p));
					}
				}
				System.out.println("\nFacts");
				idCount = SppTextHandler.factsText.size();
				for(int k = 0; k < idCount; k++){
					Vector temp = (Vector)SppTextHandler.factsText.elementAt(k);
					int size = temp.size();
					for(int p = 0; p < size; p++){
						System.out.println(temp.elementAt(p));
					}
				}*/
				//System.out.println("Parsed and formaatted text");
				message=Kuix.getMessage("GALLERYTNAILS");
				//popUp = Kuix.alert(message+"....", KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "sppList");
				msg.setText(message+"....");
				
				//Load images
				int album = 0;
				album = Integer.parseInt(SppXMLHandler.albumTid);
				
				if(album  > 0){
					//System.out.println("Loading Images in album "+MobiGuide.url+"/mobile/gallery/"+SppXMLHandler.albumTid);
					httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/gallery/"+SppXMLHandler.albumTid);
					//System.out.println("Httpconnection open:"+httpConnection);
					sppGalleryHandler = new SppGalleryHandler();
					if(httpConnection == null){
						throw new IOException();
					}
					rc = httpConnection.getResponseCode();
					//System.out.println("Httpconnection response code: "+rc);
					if(rc != HttpConnection.HTTP_OK){
						//System.out.println("Throwin this!");
						throw new IOException();
					}
					is = httpConnection.openInputStream();
					parser.parse(is, sppGalleryHandler);
					//System.out.println("Downloaded thumbnail image paths Images");
					int count = SppGalleryHandler.tnailImagePaths.size();
					if(httpConnection != null){
						httpConnection.close();
					}
					if(is != null){
						is.close();
					}
					for(int k = 0; k < count; k++){
						if(continueDownload){
							int index = ((String)SppGalleryHandler.tnailImagePaths.elementAt(k)).lastIndexOf('/')+1;
							String thumbNail = ((String)SppGalleryHandler.tnailImagePaths.elementAt(k)).substring(index);
							//System.out.println("Thumbnail: "+thumbNail);
							httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/getthumbnail/"+thumbNail+"/"+tnailWidth);
							rc = httpConnection.getResponseCode();
							if(rc != HttpConnection.HTTP_OK){
								throw new IOException();
							}
							is = httpConnection.openInputStream();
							Image sppTNailImage = Image.createImage(is);
							/*int imgHeight = sppTNailImage.getHeight();
							int imgWidth = sppTNailImage.getWidth();
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
							Image sppTnailResized = Image.createImage(sppTNailImage,offsetX, offsetY,tnailWidth,resizedHeight, 0);
							tnailWidth = tempWidth;*/
							SppGalleryHandler.tnailImages.addElement(sppTNailImage);
							//SppGalleryHandler.tnailImages.addElement(sppTNailImage);
							sppTNailImage = null;
						}
						if(httpConnection != null){
							httpConnection.close();
						}
						if(is != null){
							is.close();
						}
					}
					//System.out.println("Parsed and added images");
					Loader.runLoader = false;
					popUp.remove();
					popUp = null;
					//message = Kuix.getMessage("LOAD_VOICE_VIDEO");
					
					//popUp = Kuix.alert(message+"....");
					
					//Load voice video
					//if(SppXMLHandler.videoCall != null){
						//httpConnection = (HttpConnection) Connector.open(SppXMLHandler.videoCall);
						//rc = httpConnection.getResponseCode();
						//if(rc != HttpConnection.HTTP_OK){
						//	throw new IOException();
						//}
						//SppXMLHandler.video = httpConnection.openInputStream();
					//}
					//popUp.remove();
					//popUp = null;
					
				}
				firstRun = false;
				//screen.removeAll();
				//onMessage("identification", null);
				if(continueDownload){
					onMessage("loadMain", null);
				}
				
			}
			catch(Exception ioe){
				Loader.runLoader= false;
				popUp.remove();
				popUp = null;
				if(count >= 3){
					try{
						Thread.sleep(100);
					}catch(InterruptedException ie){
						
					}
					Kuix.alert(Kuix.getMessage("APPEXIT"), KuixConstants.ALERT_OK, "exit", null);
				}else{
					count++;
					Kuix.alert(Kuix.getMessage("ERRORSPP"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "retry", "sppList");
					SppGalleryHandler.tnailImagePaths.removeAllElements();
					SppGalleryHandler.fullImagePaths.removeAllElements();
					SppGalleryHandler.bodyText.removeAllElements();
					SppGalleryHandler.photoTitles.removeAllElements();
					SppGalleryHandler.tnailImages.removeAllElements();
					//scrollPane = null;
					SppTextHandler.idText.removeAllElements();
					SppTextHandler.conservationText.removeAllElements();
					SppTextHandler.factsText.removeAllElements();
					SppTextHandler.bodyTextText.removeAllElements();
					//ioe.printStackTrace();
				}
				//ioe.printStackTrace();
			}
			finally{
				try{
					if(is != null){
						is.close();
					}//if(httpConnection != null){
						//httpConnection.close();
					//}
					System.gc();
				}catch(IOException ioe){
					Kuix.alert(Kuix.getMessage("ERRORSTREAMS"));
				}
			}
			
		}
		else{
			//System.out.println("Not first time");
			//popUp = Kuix.alert("Loading "+label);
			//System.out.println("Command: "+command);
			if(command.equals("gallery")){
				//System.out.println("Loading gallery");
				loadGallery();
			}
			if(command.equals("geninfo")){
				//System.out.println("Identification");
				loadGenInfo();
				//if(firstShow || fromGallery){
				if(firstShow){
					//System.out.println("Called twice");
					loadGenInfo();
					firstShow = false;
					//fromGallery = false;
				}
			}
			if(command.equals("identification")){
				//System.out.println("Identification");
				loadIdentification();
				//if(firstShow || fromGallery){
				if(firstShow){
					//System.out.println("Called twice");
					loadIdentification();
					firstShow = false;
					//fromGallery = false;
				}
			}
			if(command.equals("conservation")){
				//System.out.println("conservation");
				loadConservation();
				//if(firstShow || fromGallery){
				if(firstShow){
					loadConservation();
					firstShow = false;
					//fromGallery = false;
				}
			}
			if(command.equals("facts")){
				//System.out.println("Loading facts");
				loadInterestingFacts();
				//if(firstShow || fromGallery){
				if(firstShow){
					loadInterestingFacts();
					firstShow = false;
					//fromGallery = false;
				}
			}
			if(command.equals("mainSppPage")){
				loadMainSppPage();
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
				screen.invalidate();
			}
			if(command.equals("nextSpp")/* && fromGallery*/){
				onMessage("nextSpp", null);
			}
			if(command.equals("prevSpp")/* && fromGallery*/){
				onMessage("prevSpp", null);
			}
			if(command.equals("call")/* && fromGallery*/){
				onMessage("call", null);
				//fromGallery = false;
				//System.out.println("Call now");
			}
			if(command.equals("sppList")){
				onMessage("sppList", null);
				//fromGallery = false;
			}
			
			//fromGallery = false;
		}
		
		if(popUp != null && !command.equals("call")){
			Loader.runLoader = false;
			popUp.remove();
			popUp = null;
			//screen.invalidate();
		}
		//System.out.println("Thread ended");
	}
}
