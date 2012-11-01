package com.raccuddasys.services;

import javax.microedition.lcdui.Image;

import org.kalmeo.kuix.widget.Picture;

public class Loader implements Runnable{

	static Image[] animation = new Image[12];
	Picture currentPic;
	public static boolean runLoader;
	static boolean loadImages = true;
	public Loader(Picture picture){
		currentPic = picture;
		runLoader = true;
		if(loadImages){
			int j;
			for(int i = 0; i < 12; i++){
				j = i + 1;
				animation[i] = new Picture().setSource("loader/"+j+".png").getImage();
				//System.out.println(i);
			}
			loadImages = false;
		}
	}
	public void run() {
		// TODO Auto-generated method stub
		int count = 0;
		while(runLoader){
			//currentPic.setSource("loader/"+count+".png");
			currentPic.setImage(animation[count]);
			try{
				Thread.sleep(83);
				//System.out.println("Count:"+count);
			}catch(InterruptedException ie){
				
			}
			if(count < 11){
				++count;
			}else{
				count = 0;
			}
			//System.out.println("Count:"+count);
		}
		
	}

}
