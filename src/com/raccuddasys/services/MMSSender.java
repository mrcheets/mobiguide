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
import com.raccuddasys.MobiGuide;
import com.raccuddasys.SpeciesFrame;

public class MMSSender implements Runnable{

	PopupBox popUp;
	HttpConnection httpConnection;
	InputStream is;
	byte[] data;
	boolean messageSent = false;
	String imgMime;
	int length;
	int width, height;
	public static boolean addressAvailable = false;
	public static boolean sendMMS = false;
	public static boolean checkboxStatus = true;
	public static boolean sendingMMS = false;
	public static boolean fromRecord = false;
	
	public MMSSender(int width, int height){
		this.width = width;
		this.height = height;		
	}
		
	public void run() {
		if(!addressAvailable || !sendMMS){
			SpeciesFrame.addressField = new TextField();
			SpeciesFrame.saveBox = new CheckBox();
			SpeciesFrame.infoBox = Kuix.showPopupBox("phonePopup.xml", null);
			if(addressAvailable || SpeciesFrame.address != null){
				//Load textfield from available records
				TextField tf = (TextField)SpeciesFrame.infoBox.getWidget("phoneNumber");
				tf.setText(SpeciesFrame.address);
			}
			if(!checkboxStatus){
				CheckBox cb = (CheckBox)SpeciesFrame.infoBox.getWidget("saveBox");
				cb.setSelected(false);
			}
			//System.out.println("sendingMMS:"+sendingMMS);
		}
		else{
			if(!sendingMMS){
				popUp = Kuix.alert(Kuix.getMessage("IMGSEND")); //popUp = Kuix.alert(Kuix.getMessage("IMGSEND"), KuixConstants.ALERT_OK, "insertAddress", null);
				sendingMMS = true;
				MessageConnection conn = null;
				int rc;
				try{
					int index = ((String)SppGalleryHandler.fullImagePaths.elementAt(ImageDisplayFrame.imgCurrent)).lastIndexOf('/')+1;
					String imagePath = ((String)SppGalleryHandler.fullImagePaths.elementAt(ImageDisplayFrame.imgCurrent)).substring(index);
					//httpConnection = (HttpConnection) Connector.open((String)SppGalleryHandler.fullImagePaths.elementAt(ImageDisplayFrame.imgCurrent));
					httpConnection = (HttpConnection) Connector.open(MobiGuide.url+"/mobile/getmmsimage/"+imagePath+"/"+width+"/"+height);
					rc = httpConnection.getResponseCode();
					if(rc != HttpConnection.HTTP_OK){
						throw new IOException();
					}
					rc = (int)httpConnection.getLength();
					imgMime = httpConnection.getType();
					is = httpConnection.openInputStream();
					/*if(rc != -1){
						System.out.println("Known length of image. Mime: "+imgMime);
						data = new byte[rc];
						is = new DataInputStream(httpConnection.openInputStream());
						is.readFully(data);
					}
					else{
						System.out.println("Image length not known. Mime: "+imgMime);
						int chunkSize = 512;
						int index = 0;
						int readLength = 0;
						is = new DataInputStream(httpConnection.openInputStream());
						byte[] data2 = new byte[chunkSize];
						do{
							if(data2.length < index + chunkSize){
								byte[] newData = new byte[index + chunkSize];
								System.arraycopy(data2, 0, newData, 0, data2.length);
								data2 = newData;
							}
							readLength = is.read(data2, index, chunkSize);
							index += readLength;
						}while(readLength == chunkSize);
						length = index;
						data = new byte[length];
						System.arraycopy(data2, 0, data, 0, length);
					}*/
			
					conn = (MessageConnection) Connector.open("mms://"+SpeciesFrame.address);
					MultipartMessage mpMessage = (MultipartMessage)conn.newMessage(MessageConnection.MULTIPART_MESSAGE);
					mpMessage.setSubject((String)SppGalleryHandler.photoTitles.elementAt(ImageDisplayFrame.imgCurrent));
					mpMessage.addMessagePart(new MessagePart(is, imgMime, "id1", "location", null));
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
					}catch(Exception e){
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
					popUp = Kuix.alert(Kuix.getMessage("IMGSENT"));
				}else{
					popUp = Kuix.alert(Kuix.getMessage("ERRORIMGSEND"));
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
			addressAvailable = false;
			sendMMS = false;
			sendingMMS = false;
			fromRecord = false;
			SpeciesFrame.infoBox = null;
		}		
	}

	

}
