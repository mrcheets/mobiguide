package com.raccuddasys;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;

public class LoginFrame implements Frame{

	public void onAdded() {
		// Load the content from the XML file with Kuix.loadScreen static method
		Screen screen = Kuix.loadScreen("login.xml", null);

		// Set the application current screen
		screen.setCurrent();
		
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		if ("about".equals(identifier)) {
			// display a popup message
			Kuix.alert(Kuix.getMessage("CREDITS"), KuixConstants.ALERT_OK);
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
		if ("signin".equals(identifier)) {
			//Validate the user input
			//Send info to server
			//If user logs in successfully, take him to InfoFrame
			//Else, show him the error alert and possible way forward
			Kuix.getFrameHandler().pushFrame(new InfoFrame());
			return false;
		}
		Kuix.alert((String)identifier);
		/*if ("showDynamic".equals(identifier)) {
			// push next frame
			Kuix.getFrameHandler().pushFrame(new DynamicFrame());
			return false;
		}*/
		return false;
	}

	public void onRemoved() {
		// TODO Auto-generated method stub
		
	}

}
