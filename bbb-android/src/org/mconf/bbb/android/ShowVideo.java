/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.IVideoListener;
import org.mconf.bbb.RtmpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.io.flv.FlvAtom;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.BytesRead;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.jboss.netty.channel.Channel;


public class ShowVideo extends Activity implements IVideoListener,SurfaceHolder.Callback {
	private static final Logger log = LoggerFactory.getLogger(ShowVideo.class);
//	FileChannel out;
//	File file;
//	FileOutputStream fos;
	
//    LocalSocket localSocket;
//LocalServerSocket lss;
//InputStream fis = null;
//OutputStream fos = null;
//MediaPlayer mediaPlayer;
//LocalSocket localSocket2;
	private Context context;
	File temporaryFile;
	FileOutputStream out;
	private int totalKbRead = 0;
	byte buf[] = new byte[16384];
	long bytesRead;
	long bytesReadLastSent; 
	int bytesReadWindow = 2500000;
	int totalBytesRead = 0, incrementalBytesRead = 0;
	private MediaPlayer	mediaPlayer;
	private static final int INTIAL_KB_BUFFER =  96*10/8;//assume 96kbps*10secs/8bits per byte
	private int counter = 0;
	// Create Handler to call View updates on the main UI thread.
	private final Handler handler = new Handler();
	private SurfaceHolder holderx;
	private boolean canStart = false;
	private VideoWriter writer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		log.debug("onCreate");
		Client.bbb.addListener(this);	

		//3 alternatives were tested in this file.
		//Alternative 1: temporary files
		this.context = getApplicationContext();
		
		setContentView(R.layout.show_video);
		SurfaceView surface = (SurfaceView)findViewById(R.id.surface);
		SurfaceHolder holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//holder.setFixedSize(176,144);
				
		
	
		temporaryFile = new File(context.getCacheDir(),"temporaryVideo.mp4");
		// Just in case a prior deletion failed because our code crashed or something, we also delete any previously 
		// downloaded file to ensure we start fresh.  If you use this code, always delete 
		// no longer used downloads else you'll quickly fill up your hard disk memory.  Of course, you can also 
		// store any previously downloaded file in a separate data cache for instant replay if you wanted as well.
		if (temporaryFile.exists()) {
			temporaryFile.delete();
		}
		
		try {
			out = new FileOutputStream(temporaryFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		//end of Alternative 1

		//Alternative 2: Reuse flazr FlvWriter class to write the video data to a file
//		writer = new VideoWriter("/sdcard/videowriter.3gp");
		//end of alternative 2
		
		//Alternative 3: Local socket with MediaPlayer		   		
//		localSocket = new LocalSocket();
//		try {
//			lss = new LocalServerSocket("TEST");		
//			localSocket.connect(new LocalSocketAddress("TEST"));
//			localSocket = lss.accept();
//			//lss.close();
//			localSocket.setReceiveBufferSize(500000);		
//			localSocket.setSendBufferSize(500000);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//		try {
//			//fis = localSocket.getInputStream();
//			fos = localSocket.getOutputStream();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
//		
//		mediaPlayer = new MediaPlayer();
//		try {
//			mediaPlayer.setDataSource(localSocket.getFileDescriptor());
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			log.debug("ERROR 1");
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			log.debug("ERROR 2");
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			log.debug("ERROR 3");
//			e.printStackTrace();
//		}
//		try {
//			mediaPlayer.prepare();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		//end of alternative 3    
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	
	@Override
	protected void onDestroy() {
		log.debug("onDestroy");
		
		//Alternative 3:
//		try {
//			fos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		end of alternative 3		

		Client.bbb.removeListener(this);
//		bbb.disconnect();
//
//		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		notificationManager.cancelAll();	
//
////		unregisterReceiver(receiver);
//
		super.onDestroy();
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//TODO Gian implement the onKeyDown for ShowVideo.java
//    	if (keyCode == KeyEvent.KEYCODE_BACK) {
//    		Intent intent = new Intent(SEND_TO_BACK);
//    		sendBroadcast(intent);
//    		log.debug("KEYCODE_BACK");
//    		moveTaskToBack(true);
//    		return true;
//    	}    		
    	return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onVideo(final RtmpMessage message, Channel channel) {
		//alternative 3:
//		try {
//			fos.write(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//end of alternative 3

	//alternative 2:	
		writer.write(message);
        bytesRead += message.getHeader().getSize();
        if((bytesRead - bytesReadLastSent) > bytesReadWindow) {
            log.debug("sending bytes read ack {}", bytesRead);
            bytesReadLastSent = bytesRead;
            channel.write(new BytesRead(bytesRead));
        }
//end of alternative 2

//alternative 1:
//		int numread = message.length;   
//        if (numread <= 0){   
//               
//        } else {
//	        try {
//				//out.write(message, 0, numread);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	        totalBytesRead += numread;
//	        incrementalBytesRead += numread;
//	        totalKbRead = totalBytesRead/1000;

//	        	testMediaBuffer();

//	//       	fireDataLoadUpdate();
//        }
//end of alternative 1		

	}
	
	
	//all the functions below belong to the alternative 1
	/**
     *  Move the file in oldLocation to newLocation.
     */
	public void moveFile(File	oldLocation, File	newLocation)
	throws IOException {

		if ( oldLocation.exists( )) {
			BufferedInputStream  reader = new BufferedInputStream( new FileInputStream(oldLocation) );
			BufferedOutputStream  writer = new BufferedOutputStream( new FileOutputStream(newLocation, false));
            try {
		        byte[]  buff = new byte[8192];
		        int numChars;
		        while ( (numChars = reader.read(  buff, 0, buff.length ) ) != -1) {
		        	writer.write( buff, 0, numChars );
      		    }
            } catch( IOException ex ) {
				throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
            } finally {
                try {
                    if ( reader != null ){                    	
                    	writer.close();
                        reader.close();
                    }
                } catch( IOException ex ){
				    Log.e(getClass().getName(),"Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() ); 
				}
            }
        } else {
			throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() );
        }
	}
	
	private MediaPlayer createMediaPlayer(File mediaFile)
    throws IOException {
    	MediaPlayer mPlayer = new MediaPlayer();
    	mPlayer.setOnErrorListener(
				new MediaPlayer.OnErrorListener() {
			        public boolean onError(MediaPlayer mp, int what, int extra) {
			        	Log.e(getClass().getName(), "Error in MediaPlayer: (" + what +") with extra (" +extra +")" );
			    		return false;
			        }
			    });

		//  It appears that for security/permission reasons, it is better to pass a FileDescriptor rather than a direct path to the File.
		//  Also I have seen errors such as "PVMFErrNotSupported" and "Prepare failed.: status=0x1" if a file path String is passed to
		//  setDataSource().  So unless otherwise noted, we use a FileDescriptor here.
		FileInputStream fis = new FileInputStream(mediaFile);
		try {
			mPlayer.setDisplay(holderx);
		} catch (IllegalArgumentException e) {
			log.debug("MEDIA_PLAYER", e.getMessage());
		} catch (IllegalStateException e){
			log.debug("MEDIA_PLAYER", e.getMessage());
		}
		mPlayer.setDataSource(fis.getFD());
//		mPlayer.setDataSource("/sdcard/testevideooficial.mp4");
		mPlayer.prepare();
		return mPlayer;
    }
	
	private void startMediaPlayer() {
        try {   
        	File bufferedFile = new File(context.getCacheDir(),"playingMedia" + (counter++) + ".mp4");
        	
        	// We double buffer the data to avoid potential read/write errors that could happen if the 
        	// download thread attempted to write at the same time the MediaPlayer was trying to read.
        	// For example, we can't guarantee that the MediaPlayer won't open a file for playing and leave it locked while 
        	// the media is playing.  This would permanently deadlock the file download.  To avoid such a deadloack, 
        	// we move the currently loaded data to a temporary buffer file that we start playing while the remaining 
        	// data downloads.  
        	moveFile(temporaryFile,bufferedFile);
    		
        	Log.e(getClass().getName(),"Buffered File path: " + bufferedFile.getAbsolutePath());
        	Log.e(getClass().getName(),"Buffered File length: " + bufferedFile.length()+"");
        	
        	mediaPlayer = createMediaPlayer(bufferedFile);
        	
    		// We have pre-loaded enough content and started the MediaPlayer so update the buttons & progress meters.
	    	mediaPlayer.start();
//	    	startPlayProgressUpdater();        	
//			playButton.setEnabled(true);
        } catch (IOException e) {
        	Log.e(getClass().getName(), "Error initializing the MediaPlayer.", e);
        	return;
        }   
    }
	
	/**
     * Transfer buffered data to the MediaPlayer.
     * NOTE: Interacting with a MediaPlayer on a non-main UI thread can cause thread-lock and crashes so 
     * this method should always be called using a Handler.
     */  
    private void transferBufferToMediaPlayer() {
	    try {
	    	// First determine if we need to restart the player after transferring data...e.g. perhaps the user pressed pause
	    	boolean wasPlaying = mediaPlayer.isPlaying();
	    	int curPosition = mediaPlayer.getCurrentPosition();
	    	
	    	// Copy the currently downloaded content to a new buffered File.  Store the old File for deleting later. 
	    	File oldBufferedFile = new File(context.getCacheDir(),"playingMedia" + counter + ".mp4");
	    	File bufferedFile = new File(context.getCacheDir(),"playingMedia" + (counter++) + ".mp4");

	    	//  This may be the last buffered File so ask that it be delete on exit.  If it's already deleted, then this won't mean anything.  If you want to 
	    	// keep and track fully downloaded files for later use, write caching code and please send me a copy.
	    	bufferedFile.deleteOnExit();   
	    	moveFile(temporaryFile,bufferedFile);

	    	// Pause the current player now as we are about to create and start a new one.  So far (Android v1.5),
	    	// this always happens so quickly that the user never realized we've stopped the player and started a new one
	    	mediaPlayer.pause();

	    	// Create a new MediaPlayer rather than try to re-prepare the prior one.
        	mediaPlayer = createMediaPlayer(bufferedFile);
    		mediaPlayer.seekTo(curPosition);
    		
    		//  Restart if at end of prior buffered content or mediaPlayer was previously playing.  
    		//	NOTE:  We test for < 1second of data because the media player can stop when there is still
        	//  a few milliseconds of data left to play
    		boolean atEndOfFile = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000;
        	if (wasPlaying || atEndOfFile){
        		mediaPlayer.start();
        	}

	    	// Lastly delete the previously playing buffered File as it's no longer needed.
	    	oldBufferedFile.delete();
	    	
	    }catch (Exception e) {
	    	Log.e(getClass().getName(), "Error updating to newly loaded content.", e);            		
		}
    }

	/**
     * Test whether we need to transfer buffered data to the MediaPlayer.
     * Interacting with MediaPlayer on non-main UI thread can causes crashes to so perform this using a Handler.
     */  
    private void  testMediaBuffer() {
	    Runnable updater = new Runnable() {
	        public void run() {
	            if (mediaPlayer == null) {
	            	//  Only create the MediaPlayer once we have the minimum buffered data
	            	if ( totalKbRead >= INTIAL_KB_BUFFER) {
	            		try {
	            			canStart = true;
	            			startMediaPlayer();
	            		} catch (Exception e) {
	            			Log.e(getClass().getName(), "Error copying buffered content.", e);    			
	            		}
	            	}
	            } else if ( mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000 ){ 
	            	//  NOTE:  The media player has stopped at the end so transfer any existing buffered data
	            	//  We test for < 1second of data because the media player can stop when there is still
	            	//  a few milliseconds of data left to play
	            	transferBufferToMediaPlayer();
	            }
	        }
	    };
	    handler.post(updater);
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		holderx = holder;
//		try {
//			mediaPlayer.setDisplay(holder);
//		} catch (IllegalArgumentException e) {
//			log.debug("MEDIA_PLAYER", e.getMessage());
//		} catch (IllegalStateException e){
//			log.debug("MEDIA_PLAYER", e.getMessage());
//		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//alternative 2
		writer.close();
		log.debug("bytes read = {}", bytesRead);
		//end of alternative 2
		//alternative 1
		//mediaPlayer.release();
		//end of alternative 1
	}
	
}
