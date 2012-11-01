package com.raccuddasys;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;

public class CategoryLandingFrame implements Frame{

	Screen screen;
	PopupBox helpPopup;
	public void onAdded() {
		screen = Kuix.loadScreen("landing.xml", null);

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
		if("search".equals(identifier)){
			Kuix.getFrameHandler().pushFrame(new SearchFrame());
			return false;
		}
		if("catList".equals(identifier)){
			Kuix.getFrameHandler().removeFrame(this);
			Kuix.getFrameHandler().getTopFrame().onAdded();
			return false;
		}
		if("help".equals(identifier)){
			helpPopup = Kuix.showPopupBox("helpPopup.xml", null);
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
		
	}

}
