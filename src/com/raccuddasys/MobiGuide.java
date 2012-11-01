package com.raccuddasys;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixMIDlet;
import org.kalmeo.kuix.widget.Desktop;

import com.raccuddasys.services.InfoFilter;

public class MobiGuide extends KuixMIDlet{
	public static String[] categories;
	public static String url;
	public static int[] catIds;
	public static String recordStore = "infoStore";

	public void initDesktopContent(Desktop arg0) {
		// TODO Auto-generated method stub
		url = this.getMIDlet().getAppProperty("mobiguide-url");
		if(getInfoStatus()){
			Kuix.getFrameHandler().pushFrame(new CategoryFrame());
		}
		else{
			Kuix.getFrameHandler().pushFrame(new InfoFrame());
		}
	}

	public void initDesktopStyles() {
		Kuix.loadCss("mobiguide.css");
	}

	public String getInitializationImageFile(){
		return "/img/splash2.png";
	}
	public String getInitializationMessage(){
		return Kuix.getMessage("INITSTRING");
	}
	public boolean getInfoStatus(){
		try{
			RecordStore recordStore = RecordStore.openRecordStore(MobiGuide.recordStore, true);
			InfoFilter ifil = new InfoFilter("hideInfoPage@@");
			RecordEnumeration re = recordStore.enumerateRecords(ifil, null, true);
			if(re.hasNextElement()){
				return true;
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}
}
