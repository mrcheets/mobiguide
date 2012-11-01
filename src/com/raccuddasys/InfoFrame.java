package com.raccuddasys;

import javax.microedition.rms.RecordStore;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;

public class InfoFrame implements Frame{
	Screen screen;

	public void onAdded() {
		// Load the content from the XML file with Kuix.loadScreen static method
		screen = Kuix.loadScreen("info.xml", null);

		// Set the application current screen
		screen.setCurrent();
		
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
		if("categories".equals(identifier)){
			CheckBox cb = (CheckBox)screen.getWidget("hideInfo");
			//System.out.println(cb.isSelected());
			if(cb.isSelected()){
				saveHideInfo();
			}
			Kuix.getFrameHandler().pushFrame(new CategoryFrame());
		}
		return false;
	}

	public void onRemoved() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean saveHideInfo(){
		try{
			RecordStore recordStore = RecordStore.openRecordStore(MobiGuide.recordStore, true);
			String status = "hideInfoPage@@";
			byte[] phNumber = status.getBytes();
			recordStore.addRecord(phNumber, 0, phNumber.length);
			return true;
		}catch(Exception e){
			return false;
		}
	}

}
