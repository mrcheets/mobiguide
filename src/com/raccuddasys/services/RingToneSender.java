package com.raccuddasys.services;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.TextField;

import com.raccuddasys.ImageDisplayFrame;
import com.raccuddasys.SpeciesFrame;

public class RingToneSender implements Runnable{
	PopupBox popUp;
	HttpConnection httpConnection;
	InputStream is;
	byte[] data;
	boolean messageSent = false;
	String ringMime;
	int length;
	
	public void run() {
		if(!MMSSender.addressAvailable || !MMSSender.sendMMS){
			//SpeciesFrame.addressField = new TextField();
			//SpeciesFrame.saveBox = new CheckBox();
			SpeciesFrame.infoBox = Kuix.showPopupBox("phonePopup.xml", null);
			if(MMSSender.addressAvailable || SpeciesFrame.address != null){
				//Load textfield from available records
				TextField tf = (TextField)SpeciesFrame.infoBox.getWidget("phoneNumber");
				tf.setText(SpeciesFrame.address);
			}
			if(!MMSSender.checkboxStatus){
				CheckBox cb = (CheckBox)SpeciesFrame.infoBox.getWidget("saveBox");
				cb.setSelected(false);
			}
		}
		else{
			if(!MMSSender.sendingMMS){
				MMSSender.sendingMMS = true;
				popUp = Kuix.alert(Kuix.getMessage("SENDRINGTONE"));
				MessageConnection conn = null;
				//System.out.println("Ringtone Path: "+SppXMLHandler.audioCall);
				int rc;
				if(SppXMLHandler.audioCall == null){
					popUp.setContent(Kuix.getMessage("NORINGTONE"));
					try{
						Thread.sleep(1000);
					}catch(InterruptedException ie){
		
					}
					MMSSender.sendingMMS = false;
				}
				else{
					try{
						httpConnection = (HttpConnection) Connector.open(SppXMLHandler.audioCall);
						rc = httpConnection.getResponseCode();
						if(rc != HttpConnection.HTTP_OK){
							throw new IOException();
						}
						rc = (int)httpConnection.getLength();
						ringMime = httpConnection.getType();
						is = httpConnection.openInputStream();
								
						conn = (MessageConnection) Connector.open("mms://"+SpeciesFrame.address);
						MultipartMessage mpMessage = (MultipartMessage)conn.newMessage(MessageConnection.MULTIPART_MESSAGE);
						mpMessage.setSubject((String)SppGalleryHandler.photoTitles.elementAt(ImageDisplayFrame.imgCurrent));
						mpMessage.addMessagePart(new MessagePart(is, ringMime, "id1", "Ringtone", null));
						conn.send(mpMessage);
						messageSent = true;
					}catch(IOException ioe){
						//ioe.printStackTrace();
						popUp.setContent(Kuix.getMessage("ERRORIO"));
					}catch(Exception e){
						//e.printStackTrace();
						popUp.setContent(Kuix.getMessage("ERRORIO"));
					}finally{
			
						try{
							if(conn != null){
								conn.close();
							}
							if(httpConnection != null){
								httpConnection.close();
							}
							if(is != null){
								is.close();
							}
						}catch(IOException ioe){
							//System.out.println("Closing connection failed");
							//ioe.printStackTrace();
							popUp.setContent(Kuix.getMessage("ERRORSTREAMS"));
						}
			
					}
					if(popUp !=null){
						popUp.remove();
					}
					try{
						Thread.sleep(100);
					}catch(InterruptedException ie){
						
					}
					if(messageSent){
						popUp.setContent(Kuix.getMessage("RINGSENT"));
					}else{
						popUp.setContent(Kuix.getMessage("ERRORTONESEND"));
					}
					try{
						Thread.sleep(1000);
					}catch(InterruptedException ie){
		
					}
					//popUp.remove();
					//popUp = null;
				}
			}
			
			
			if(messageSent){
				MMSSender.addressAvailable = false;
				MMSSender.sendMMS = false;
				MMSSender.fromRecord = false;
				SpeciesFrame.infoBox = null;
			}
		}
	}
}
