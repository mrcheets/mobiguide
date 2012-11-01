package com.raccuddasys;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.ScrollPane;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.kuix.widget.TextField;
import org.kalmeo.util.frame.Frame;

import com.raccuddasys.services.InfoFilter;
import com.raccuddasys.services.Loader;
import com.raccuddasys.services.MMSSender;
import com.raccuddasys.services.SppGalleryHandler;

public class ImageDisplayFrame implements Frame, Runnable{

	Screen screen;
	String command;
	PopupBox popUp;
	ScrollPane scrollPane;
	public static int imgCurrent;
	//Image displayImage;
	HttpConnection httpConnection;
	InputStream is;
	Picture picture;
	TextArea titleText;
	TextArea infoText;
	Picture pic;
	Loader imgLoad;
	PopupBox helpPopup;
	boolean continueDownload = true;
	final int PADDING = 80;
	boolean firstLoad = true;
	
	public void onAdded() {
		screen = Kuix.loadScreen("imgDisplay.xml", null);

		// Set the application current screen
		screen.setCurrent();
		scrollPane = (ScrollPane)Kuix.getCanvas().getDesktop().getWidget("imgScrollPane");	
		picture = new Picture();
		infoText = new TextArea();
		infoText.setStyleClass("infotxt");
		titleText = new TextArea();
		titleText.setStyleClass("headings");
		infoText.setStyled(true);
		titleText.setStyled(true);
		TextArea breadcrumb = new TextArea();
		breadcrumb.setStyleClass("breadcrumb");
		breadcrumb.setText(Kuix.getMessage("HOME")+" > "+Kuix.getMessage("SPPLIST")+" > "+Kuix.getMessage("SPP")+" > "+Kuix.getMessage("SPP_GALLERY"));
		scrollPane.add(breadcrumb);
		scrollPane.add(titleText);
		scrollPane.add(picture);
		scrollPane.add(infoText);
		command = "next";
		imgCurrent--;
		Thread loadImage_T = new Thread(this);
		loadImage_T.start();
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		CategoryFrame.innerWidth = screen.getInnerWidth();
		CategoryFrame.innerHeight = screen.getInnerHeight();
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
		if ("previous".equals(identifier)) {
			command = "previous";
			Thread loadPrevImage_T = new Thread(this);
			loadPrevImage_T.start();
			return false;
		}
		if ("download".equals(identifier)) {
			command = "download";
			Thread loadPrevImage_T = new Thread(this);
			loadPrevImage_T.start();
			return false;
		}
		if ("back".equals(identifier) || "stopAction".equals(identifier)) {
			continueDownload = false;
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		/*if ("identification".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.fromGallery = true;
			SpeciesFrame.command = "identification";
			SpeciesFrame.label = "identification traits";
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if ("conservation".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.fromGallery = true;
			SpeciesFrame.command = "conservation";
			SpeciesFrame.label = "conservation factors";
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if ("facts".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.fromGallery = true;
			SpeciesFrame.command = "facts";
			SpeciesFrame.label = "species facts";
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if ("call".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.tempCommand = SpeciesFrame.command;
			SpeciesFrame.fromGallery = true;
			SpeciesFrame.command = "call";
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if ("nextSpp".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.tempCommand = SpeciesFrame.command;
			SpeciesFrame.fromGallery = true;
			SpeciesFrame.command = "nextSpp";
			//System.out.println("Within ImageDisplay. command: "+SpeciesFrame.command+" TempCommand:"+SpeciesFrame.tempCommand);
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if ("prevSpp".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.tempCommand = SpeciesFrame.command;
			SpeciesFrame.command = "prevSpp";
			SpeciesFrame.fromGallery = true;
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if ("sppList".equals(identifier)) {
			continueDownload = false;
			SpeciesFrame.tempCommand = SpeciesFrame.command;
			SpeciesFrame.fromGallery = true;
			SpeciesFrame.command = "sppList";
			//System.out.println("Within ImageDisplay. command: "+SpeciesFrame.command+" TempCommand:"+SpeciesFrame.tempCommand);
			Kuix.getFrameHandler().removeFrame(this);

			// and display the main screen
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}*/
		if ("next".equals(identifier)) {
			command = "next";
			Thread loadNextImage_T = new Thread(this);
			loadNextImage_T.start();
			return false;
		}
		if("insertAddress".equals(identifier)){
			TextField tf = (TextField)SpeciesFrame.infoBox.getWidget("phoneNumber");
			String number = tf.getText();
			SpeciesFrame.address = number;
			CheckBox save = (CheckBox)SpeciesFrame.infoBox.getWidget("saveBox");
			MMSSender.checkboxStatus = save.isSelected();
			if(validateNumber(number)){
				MMSSender.addressAvailable = true;
				MMSSender.sendMMS = true;
				if(MMSSender.checkboxStatus){
					saveNumber(number, MMSSender.checkboxStatus);
				}
			}
						
			Thread sendMMS_T = new Thread(new MMSSender(CategoryFrame.innerWidth, CategoryFrame.innerHeight));
			sendMMS_T.start();
			return false;
		}
		if("help".equals(identifier)){
			helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
			TextArea message = (TextArea)helpPopup.getWidget("hlpMessage");
			message.setText(Kuix.getMessage("IMAGEHELP"));
			return false;
		}
		if("closeHelp".equals(identifier)){
			helpPopup.remove();
			helpPopup = null;
			return false;
		}
		return false;
	}
	
	public boolean validateNumber(String number){
		if(SpeciesFrame.address !=null){
			SpeciesFrame.address = SpeciesFrame.address.trim();
			if(!SpeciesFrame.address.equals("")){
				if(SpeciesFrame.address.indexOf("+") > 0){
					return false;
				}else{
					return true;
				}
			}
		}
		//Kuix.alert(Kuix.getMessage("VALIDPHONE"));
		return false;
	}
	public boolean saveNumber(String number, boolean checkBoxStatus){
		try{
			RecordStore recordStore = RecordStore.openRecordStore(MobiGuide.recordStore, true);
			InfoFilter ifil = new InfoFilter("phone");
			RecordEnumeration re = recordStore.enumerateRecords(ifil, null, true);
			String fullRecord = "phone"+number;
			if(checkBoxStatus){
				if(re.hasNextElement()){
					int id = re.nextRecordId();
					byte[] phNumber = fullRecord.getBytes();
					recordStore.setRecord(id, phNumber, 0, phNumber.length);
				}else{
					byte[] phNumber = fullRecord.getBytes();
					recordStore.addRecord(phNumber, 0, phNumber.length);
				}
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	public String getNumber(){
		try{
			RecordStore recordStore = RecordStore.openRecordStore(MobiGuide.recordStore, true);
			InfoFilter ifil = new InfoFilter("phone");
			RecordEnumeration re = recordStore.enumerateRecords(ifil, null, true);
			byte[] raw;
			if(re.hasNextElement()){
				raw = re.nextRecord();
				String phone = new String(raw);
				return phone.substring(5);
			}
		}catch(Exception e){
			return null;
		}
		return null;
	}

	public void onRemoved() {
		// TODO Auto-generated method stub
		MMSSender.addressAvailable = false;
		MMSSender.sendMMS = false;
		MMSSender.checkboxStatus = true;
		System.gc();
	}

	private Image resizeImage(Image src){
		//System.out.println("InnerWidth: "+CategoryFrame.innerWidth+ " InnerHeight: "+CategoryFrame.innerHeight);
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		Image resizedImage;
		
		if(srcWidth <= CategoryFrame.innerWidth - PADDING){
			//System.out.println("Retunr original");
			return src;
		}else{
			//System.out.println("Resized image in method resizeImage");
			int finWidth = CategoryFrame.innerWidth - PADDING;
			int finHeight = (srcHeight * finWidth) /srcWidth;
			Image temp = Image.createImage(finWidth, srcHeight);
			Graphics g = temp.getGraphics();
			int ratio = (srcWidth << 16)/finWidth;
			int pos = ratio/2;
			for(int x = 0; x < finWidth; x++){
				g.setClip(x, 0, 1, srcHeight);
				g.drawImage(src, x-(pos >> 16), 0, Graphics.LEFT | Graphics.TOP);
				pos +=ratio;
			}
			resizedImage = Image.createImage(finWidth, finHeight);
			g = resizedImage.getGraphics();
			ratio = (srcHeight <<  16)/finHeight;
			pos = ratio/2;
			for(int y = 0; y < finHeight; y++){
				g.setClip(0, y, finWidth, 1);
				g.drawImage(temp, 0, y-(pos >> 16), Graphics.LEFT|Graphics.TOP);
				pos += ratio;
			}
		}
		
		return resizedImage;
	}
	public void run() {
		//System.out.println("Run started");
		//DataInputStream in = null;
		popUp = Kuix.showPopupBox("progressPopup.xml", null); //Kuix.alert("Loading "+command+" image", KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "back");
		TextArea msg = (TextArea)popUp.getWidget("txtMessage");
		msg.setText(Kuix.getMessage("LOADIMAGE"));
		pic = (Picture)popUp.getWidget("picLoader");
		imgLoad = new Loader(pic);
		Thread imgLoop = new Thread(imgLoad);
		imgLoop.start();
		//popUp = Kuix.alert("Loading image....");
		boolean loadImage = false;
		int rc;
		if(command.equals("next")){
			if(SppGalleryHandler.fullImagePaths != null){
				++imgCurrent;
				int imgCount = SppGalleryHandler.fullImagePaths.size();
				if(imgCurrent > (imgCount - 1)){
					imgCurrent = imgCount -  1;
					Kuix.alert(Kuix.getMessage("LASTPHOTO"));
				}
				else{
					loadImage = true;
				}
			}			
		}
		if(command.equals("previous")){
			if(SppGalleryHandler.fullImagePaths != null){
				--imgCurrent;
				if(imgCurrent < 0){
					imgCurrent = 0;
					Kuix.alert(Kuix.getMessage("FIRSTPHOTO"));
				}
				else{
					loadImage = true;
				}
			}
		}
		if(firstLoad){ //if its the first time
			firstLoad = false;
			loadImage = true;
		}
		if(loadImage){
			//System.out.println("Loading Image");
			
			try{
				if(continueDownload){
					//Kuix.alert("get url download");
					int index = ((String)SppGalleryHandler.fullImagePaths.elementAt(imgCurrent)).lastIndexOf('/')+1;
					String imagePath = ((String)SppGalleryHandler.fullImagePaths.elementAt(imgCurrent)).substring(index);
					//System.out.println("Image: "+imagePath);
					httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/getimage/"+imagePath+"/"+CategoryFrame.innerWidth+"/"+CategoryFrame.innerHeight+"/"+PADDING);
					//httpConnection = (HttpConnection) Connector.open((String)SppGalleryHandler.fullImagePaths.elementAt(imgCurrent));
					//System.out.println("loading image: "+(String)SppGalleryHandler.fullImagePaths.elementAt(imgCurrent));
					//System.out.println("LoadImage: "+loadImage);
					//Kuix.alert("connection got");
					rc = httpConnection.getResponseCode();
					if(rc != HttpConnection.HTTP_OK){
						throw new IOException();
					}
					//Kuix.alert("connection ok");
					//System.out.println("Done getting url");
					is = httpConnection.openInputStream();
					
					//Kuix.alert("input stream opened: "+is.available());
					/*int length = (int)httpConnection.getLength();
					byte[] data = null;
					if (length != -1) {
					data = new byte[length];
						in = new DataInputStream(httpConnection.openInputStream());
						in.readFully(data);
					}
					else {
						// If content length is not given, read in chunks.
						int chunkSize = 512;
						int contindex = 0;
						int readLength = 0;
						in = new DataInputStream(httpConnection.openInputStream());
						data = new byte[chunkSize];
						do {
							if (data.length < contindex + chunkSize) {
								byte[] newData = new byte[contindex + chunkSize];
								System.arraycopy(data, 0, newData, 0, data.length);
								data = newData;
							}
							readLength = in.read(data, contindex, chunkSize);
							contindex += readLength;
						} while (readLength == chunkSize);
						length = contindex;
					}*/
					//Image displayImage = DirectUtils.createImage(data, 0, length);
					//Image displayImage = Image.createImage(data, 0, length);
					Image displayImage = Image.createImage(is);
					//Kuix.alert("image downloaded");
					//System.out.println("Input stream is no errors");
					//System.out.println("Image size: "+displayImage.getHeight()+"X"+displayImage.getWidth());
					picture.removeAll();
					picture.setFrameHeight(displayImage.getHeight());
					picture.setFrameWidth(displayImage.getWidth());
					picture.setImage(resizeImage(displayImage));
					//Kuix.alert("image ok");
					//System.out.println("Image resized");
					infoText.setText((String)SppGalleryHandler.bodyText.elementAt(imgCurrent));
					titleText.setText((String) SppGalleryHandler.photoTitles.elementAt(imgCurrent));
					//System.out.println("Everything ok");
					displayImage = null;
					//data = null;
				}
			}catch(IOException ioe){
				if(command.equals("next")){
					--imgCurrent;
				}else{
					++imgCurrent;
				}
				Kuix.alert(Kuix.getMessage("ERRORNET"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, command, "back");
				//ioe.printStackTrace();
				//Kuix.alert(ioe.toString());
			}catch(OutOfMemoryError ime){
				if(popUp!=null){
					popUp.remove();
					popUp = null;
				}
				Kuix.alert(Kuix.getMessage("ERRORMEM"), KuixConstants.ALERT_OK, "exit", null);
			}catch(Exception e){
				if(popUp!=null){
					popUp.remove();
					popUp = null;
				}
				Kuix.alert(Kuix.getMessage("ERRORUK"), KuixConstants.ALERT_OK, "exit", null);
			}finally{
				//System.out.println("Closing streams");
				try{
					//if(in != null){
					//	in.close();
					//}
					if(is != null){
						is.close();
						//System.out.println("inputsrteam is closed");
					}if(httpConnection != null){
						httpConnection.close();
						//System.out.println("Httpconnection closed");
					}
					System.gc();
				}catch(IOException ioe){
					Kuix.alert(Kuix.getMessage("ERRORSTREAMS")); 
				}
			}
		}
		
		if(command.equals("download")){
			//popUp.setContent("Sending image to phone");
			//try{
			//	Thread.sleep(300);
			//}catch(InterruptedException ie){
				
			//}
			//Get address from recordstore
			if((SpeciesFrame.address = getNumber()) != null){
				MMSSender.addressAvailable = true;
				MMSSender.fromRecord = true;
			}
			Thread sendSMS_T = new Thread(new MMSSender(CategoryFrame.innerWidth, CategoryFrame.innerHeight));
			sendSMS_T.start();
		}
		
		Loader.runLoader = false;
		try{
			Thread.sleep(300);
		}catch(InterruptedException ie){
			
		}
		if(popUp != null){
			popUp.remove();
			popUp = null;
		}
		screen.invalidate();
	}

}
