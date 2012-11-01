package com.raccuddasys;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.FramePositioningControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixCanvas;
import org.kalmeo.kuix.util.Metrics;

import com.raccuddasys.services.SppXMLHandler;

public class VideoDisplay extends org.kalmeo.kuix.widget.Widget implements PlayerListener{
	public static final String MY_WIDGET_TAG = "videodisplay"; 

	private Player player;
	private FramePositioningControl fpc;
	private VideoControl videoControl;
	public VolumeControl volumeControl;
	KuixCanvas canvas = Kuix.getCanvas();
	int locationx, locationy = 45;
	final int PADDING = 80;
	public VideoDisplay() {
		super(MY_WIDGET_TAG);
		
	}
	
	public void initializePlayer() throws MediaException, IOException{
		player = Manager.createPlayer(SppXMLHandler.videoCall);
		player.realize();
		player.prefetch();
		player.addPlayerListener(this);
		player.addPlayerListener(VideoDisplayFrame.vid);
		//player.setLoopCount(3);
		videoControl = (VideoControl) player.getControl("VideoControl");
		volumeControl = (VolumeControl) player.getControl("VolumeControl");
		if(volumeControl != null){
			volumeControl.setLevel(40);
			//System.out.println("Volume control available");
		}
		fpc = (FramePositioningControl)player.getControl("javax.media.control.FramePositioningControl");
		if(videoControl != null){
			videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, canvas);
			int vidWidth = videoControl.getSourceWidth();
			int vidHeight  = videoControl.getSourceHeight();
			int finalWidth;
			int finalHeight;
			if(CategoryFrame.innerWidth <= CategoryFrame.innerHeight){
				finalWidth = CategoryFrame.innerWidth - PADDING;
				finalHeight = (vidHeight * finalWidth)/vidWidth;
			}else{
				finalHeight = CategoryFrame.innerHeight - PADDING;
				finalWidth = (vidWidth * finalHeight)/vidHeight;
			}
			//System.out.println("innerWidth:"+CategoryFrame.innerWidth+" innerHeight:"+CategoryFrame.innerHeight+" Width:"+finalWidth+" height:"+finalHeight);
			videoControl.setDisplaySize(finalWidth, finalHeight);
			locationx = (CategoryFrame.innerWidth  - finalWidth) /2;
			
			videoControl.setDisplayLocation(locationx, locationy);
			videoControl.setVisible(true);
		}
        if(fpc == null){
        	//player.start();
        	//player.stop();
        	player.setMediaTime(0);
        	canvas.repaint();
        }else{
        	fpc.seek(0);
        }       
	}
	public void startPlayer()throws MediaException{
		player.start();
		if(videoControl != null){
			videoControl.setVisible(true);
		}
	}
	public void pausePlayer()throws MediaException{
		player.stop();
		canvas.repaint();
	}
	public void stopPlayer()throws MediaException{
		player.stop();
		player.setMediaTime(0);
		canvas.repaint();
	}
	public void displayPlayer(boolean display){
		if(videoControl != null){
			videoControl.setVisible(display);
		}
	}
	public int getDuration(){
		int state;
		if(player == null){
			return -1;
		}
		else{
			state = player.getState();
			if(state == Player.CLOSED || state == Player.UNREALIZED){
				return -1;
			}
			long time = player.getDuration();
			if(time == Player.TIME_UNKNOWN){
				return -1;
			}
			return (int)time/1000000;
		}		
	}
	public Metrics getPreferredSize(int w) {
        Metrics metrics = new Metrics(this);
        metrics.width = videoControl.getDisplayWidth();
        metrics.height = videoControl.getDisplayHeight();
        return metrics;
    }
	public void closePlayer(){
		videoControl = null;
		volumeControl = null;
		canvas = null;
		player.close();
	}

	public void playerUpdate(Player player, String mediaEvent, Object medData) {
		if(mediaEvent.equals("endOfMedia")){
			try{
				player.stop();
				player.setMediaTime(0);
				canvas.repaint();
				//System.out.println("End of video in player");
			}catch(MediaException me){
				
			}
		}		
	}
}
