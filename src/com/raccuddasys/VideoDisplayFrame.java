package com.raccuddasys;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.KuixConverter;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.Gauge;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.ScrollPane;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.kuix.widget.TextField;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.frame.Frame;

import com.raccuddasys.services.InfoFilter;
import com.raccuddasys.services.Loader;
import com.raccuddasys.services.MMSSender;
import com.raccuddasys.services.RingToneSender;

public class VideoDisplayFrame implements Frame, Runnable, PlayerListener{

	Screen screen;
	VideoDisplay videoDisplay;
	PopupBox popUp;
	PopupBox helpPopup;
	int count = 0;
	String command;
	Gauge vidGauge;
	boolean timerThread = false;
	boolean playVideo = false;
	int increment = 0;
	int currentTime = 0;
	final int GAUGESIZE = 4100;
	int videoDuration;
	Picture pic;
	Loader imgLoad;
	TextArea infoUpdate;
	static VideoDisplayFrame vid = null;
	public void onAdded() {
		// TODO Auto-generated method stub
		screen = Kuix.loadScreen("videodisplay.xml", null);
		screen.setCurrent();
		//videoDisplay = (VideoDisplay)Kuix.getCanvas().getDesktop().getWidget("myVideoDisplay");
		videoDisplay = new VideoDisplay();
		command = "videoRun";
		vid = this;
		Thread loadVideo_T = new Thread(this);
		loadVideo_T.start();
		
	}
	
	
	protected KuixConverter createNewConverterInstance(){
		return new KuixConverter(){
			public Widget convertWidgetTag(String tag){
				if(VideoDisplay.MY_WIDGET_TAG.equals(tag)){
					return new VideoDisplay();
				}
				return super.convertWidgetTag(tag);
				
			}
		};
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		if ("back".equals(identifier) || "stopAction".equals(identifier)) {
			// remove the current frame from the framehandler stack
			videoDisplay.closePlayer();
			Loader.runLoader = false;
			if(popUp != null){ //If someone cancels download
				popUp.remove();
				popUp = null;
				try{
					Thread.sleep(300);
				}catch(InterruptedException ie){
					
				}
			}
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
		if("stop".equals(identifier)){
			try{
				videoDisplay.stopPlayer();
				if(videoDuration > -1){
					vidGauge.setValue(0);
				}
				timerThread = false;
				//playVideo = false;
				currentTime = 0;
				Kuix.loadMenuContent(screen.getFirstMenu(),"playMenu.xml", null);
				if(infoUpdate != null){
					infoUpdate.setText(Kuix.getMessage("VIDRDY"));
				}
			}catch(Exception ie){
				Kuix.alert(Kuix.getMessage("ERRORPLAY"));
			}
		}
		if("pause".equals(identifier)){
			try{
				videoDisplay.pausePlayer();
				timerThread = false;
				//playVideo = false;
				Kuix.loadMenuContent(screen.getFirstMenu(),"playMenu.xml", null);
				if(infoUpdate != null){
					infoUpdate.setText(Kuix.getMessage("PAUSED"));
				}
				--currentTime;
			}catch(Exception ie){
				Kuix.alert(Kuix.getMessage("ERRORPLAY"));
			}
		}
		if("play".equals(identifier)){
			try{
				Kuix.loadMenuContent(screen.getFirstMenu(),"pauseMenu.xml", null);
				timerThread = true;
				if(infoUpdate != null){
					infoUpdate.setText(Kuix.getMessage("PLAYING"));
				}
				Thread timer_T = new Thread(this);
				timer_T.start();
				try{
					Thread.sleep(100);
				}catch(InterruptedException ie){
					
				}
				
				//videoDisplay.startPlayer();
			}catch(Exception ie){
				Kuix.alert(Kuix.getMessage("ERRORPLAY"));
			}
		}
		if("downloadVoice".equals(identifier)){
			try{
				videoDisplay.pausePlayer();
				videoDisplay.displayPlayer(false);
				Kuix.loadMenuContent(screen.getFirstMenu(),"playMenu.xml", null);
			}catch(Exception ie){
				Kuix.alert(Kuix.getMessage("ERRORPLAY"));
			}
			command = "downloadCall";
			Thread downloadCall_T = new Thread(this);
			downloadCall_T.start();
		}
		if("cancel".equals(identifier)){
			videoDisplay.displayPlayer(true);
			//System.out.println("Canceled");
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
			Thread sendMMS_T = new Thread(new RingToneSender());
			sendMMS_T.start();
			return false;
		}
		if("help".equals(identifier)){
			try{
				videoDisplay.pausePlayer();
				videoDisplay.displayPlayer(false);
				timerThread = false;
				helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
				TextArea message = (TextArea)helpPopup.getWidget("hlpMessage");
				message.setText(Kuix.getMessage("VIDHELP"));
			}catch(Exception ie){
				Kuix.alert(Kuix.getMessage("ERRORPLAY"));
			}
			return false;
		}
		if("closeHelp".equals(identifier)){
			helpPopup.remove();
			helpPopup = null;
			videoDisplay.displayPlayer(true);
			return false;
		}
		if("addVolume".equals(identifier)){
			//System.out.println("adding volume");
			if(videoDisplay != null){
				if(videoDisplay.volumeControl !=null){
					int volume = videoDisplay.volumeControl.getLevel();
					if(volume < 100){
						//System.out.println("Current volume: "+volume);
						videoDisplay.volumeControl.setLevel(volume + 10);
					}
				}
			}
			return false;
		}
		if("decVolume".equals(identifier)){
			//System.out.println("reducing volume");
			if(videoDisplay != null){
				if(videoDisplay.volumeControl !=null){
					int volume = videoDisplay.volumeControl.getLevel();
					if(volume > 0){
						//System.out.println("Current volume: "+volume);
						videoDisplay.volumeControl.setLevel(volume - 10);
					}
				}
			}
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
		vid = null;
		System.gc();
	}
	
	public void playerUpdate(Player player, String mediaEvent, Object medData) {
		if(mediaEvent.equals("endOfMedia")){
			//Kuix.alert("Got to end of media");
			Kuix.loadMenuContent(screen.getFirstMenu(),"playMenu.xml", null);
			timerThread = false;
			vidGauge.setValue(0);
			currentTime = 0;
			//System.out.println("End of video");
			if(infoUpdate != null){
				infoUpdate.setText(Kuix.getMessage("VIDRDY"));
			}
			
		}
		
	}

	public void run() {
		if(command.equals("videoRun") && !timerThread){
			popUp = Kuix.showPopupBox("progressPopup.xml", null);//Kuix.alert(Kuix.getMessage("LOADVIDEO"), KuixConstants.ALERT_OK | KuixConstants.ALERT_CANCEL, null, "back");
			TextArea msg = (TextArea) popUp.getWidget("txtMessage");
			msg.setText(Kuix.getMessage("LOADVIDEO"));
			pic = (Picture)popUp.getWidget("picLoader");
			imgLoad = new Loader(pic);
			Thread imgLoop = new Thread(imgLoad);
			imgLoop.start();
			try{
				//CategoryFrame.innerWidth = screen.getInnerWidth();
				//CategoryFrame.innerHeight = screen.getInnerHeight();
				videoDisplay.initializePlayer();
				videoDisplay.setStyleClass("videowidget");
				infoUpdate = new TextArea();
				infoUpdate.setStyleClass("infoupdate");
				infoUpdate.setText(Kuix.getMessage("VIDRDY"));
				ScrollPane scrollPane = new ScrollPane();
				scrollPane.setShowScrollBar(true);
				scrollPane.setStyleClass("containertable");
				if(popUp !=null){
					Loader.runLoader = false;
					popUp.remove();
					popUp = null;
				}
				scrollPane.add(infoUpdate);
				scrollPane.add(videoDisplay);
				vidGauge = new Gauge();
				videoDisplay.add(new Widget(KuixConstants.BREAK_WIDGET_TAG));
				scrollPane.add(vidGauge);
				/*Button left = new Button();
				//left.setShortcuts(KuixConstants.KUIX_KEY_LEFT, KuixConstants.KEY_PRESSED_EVENT_TYPE);
				left.setShortcuts("left", KuixConstants.KEY_PRESSED_EVENT_TYPE);
				left.setOnAction("decVolume");
				Button right = new Button();
				//right.setShortcutKeyCodes(KuixConstants.KUIX_KEY_RIGHT, KuixConstants.KEY_PRESSED_EVENT_TYPE);
				right.setShortcuts("right", KuixConstants.KEY_PRESSED_EVENT_TYPE);
				right.setOnAction("addVolume");
				scrollPane.add(left);
				scrollPane.add(right);*/
				screen.add(scrollPane);
				screen.bringToFront(screen.getFirstMenu());
				screen.bringToFront(screen.getSecondMenu());
				vidGauge.setFocusable(false);
				vidGauge.setValue(0);
				videoDuration = videoDisplay.getDuration();
				//System.out.println("Duration:"+videoDuration);
				if(videoDuration == -1){
					vidGauge.setValue(GAUGESIZE);
				}else{
					increment = GAUGESIZE/videoDuration;
				}
				
			}catch(Exception e){
				if(count >= 3){
					Kuix.alert(Kuix.getMessage("VIDEOBACK"), KuixConstants.ALERT_OK, "back", null);
				}else{
					count++;
					Kuix.alert(Kuix.getMessage("ERRORVIDEO"), KuixConstants.ALERT_YES | KuixConstants.ALERT_NO, "retry", "back");
					//e.printStackTrace();
					
				}
			}finally{
				if(popUp !=null){
					Loader.runLoader = false;
					popUp.remove();
					popUp = null;
				}
			}
		}
		if(command.equals("downloadCall") && !timerThread){
			Thread sendRingTone_T = new Thread(new RingToneSender());
			sendRingTone_T.start();
		}
		if(playVideo){
			if(videoDisplay != null){
				try{
					videoDisplay.startPlayer();
				}catch(Exception ie){
					Kuix.alert(Kuix.getMessage("ERRORPLAY"));
				}
			}
		}
		if(timerThread){
			if(!playVideo){
				playVideo = true;
				Thread video_T = new Thread(this);
				video_T.start();
			}
			while(timerThread){
				try{
					if(videoDuration > -1){
						vidGauge.setValue(currentTime * increment);
						++currentTime;
						Thread.sleep(1000);
						//System.out.println("CurrentTime:"+currentTime+" Increment:"+currentTime * increment);
					}
					if(currentTime > videoDuration){
						timerThread = false;
						//Kuix.alert("Ending the timer thread");
					}
					
				}catch(InterruptedException ie){
					timerThread = false;
				}
			}
		}
		
	}

}
