package com.raccuddasys.services;

import javax.microedition.rms.RecordFilter;

public class InfoFilter implements RecordFilter{
	String info;
	
	public InfoFilter(String info){
		this.info = info;
	}

	public boolean matches(byte[] candidate) {
		if(candidate.length == 0){
			return false;
		}
		String myInfo = new String(candidate);
		return myInfo.startsWith(info);
	}

}
