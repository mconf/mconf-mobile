package org.mconf.bbb.android;

import org.sipdroid.media.RtpStreamReceiver;
import org.sipdroid.media.RtpStreamSender;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AudioControlDialog extends Dialog {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(AudioControlDialog.class);
	private Context context;

	public AudioControlDialog(Context context) {
		super(context);
		this.context = context;

		this.setContentView(R.layout.audio_config);
		this.setTitle(R.string.audio_config);
		this.setCancelable(true);
		
		final AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	final int setVolFlags = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
				AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;
    	
		// \TODO check if there's a way to change the microphone volume
//		final SeekBar mic_volume = (SeekBar) dialog.findViewById(R.id.mic_volume);
//		mic_volume.setMax(manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
//		mic_volume.setProgress(manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
//		mic_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {					
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//			}				
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0);
//			}
//		});
		
    	// the mic gain should be 0, 0.1, 0.2 or 1.0
		final int multiplicator = 10;
		final SeekBar mic_gain = (SeekBar) this.findViewById(R.id.mic_gain);
		mic_gain.setMax(3); // four states
		switch (Float.valueOf(Settings.getMicGain() * multiplicator).intValue()) {
			case 1:
				mic_gain.setProgress(0);
				break;
			case 2:
				mic_gain.setProgress(1);
				break;
			case 0:
				mic_gain.setProgress(2);
				break;
			case 10:
				mic_gain.setProgress(3);
				break;
		}
		mic_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Editor editor = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();
				double value = 0;
				switch (seekBar.getProgress()) {
					case 0:
						value = 0.1;
						break;
					case 1:
						value = 0.2;
						break;
					case 2:
						value = 0.0;
						break;
					case 3:
						value = 1.0;
						break;
				}
				editor.putString(Settings.PREF_MICGAIN, "" + value);
				editor.commit();
				RtpStreamSender.changed = true;
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
		
		final SeekBar speaker_volume = (SeekBar) this.findViewById(R.id.speaker_volume);
		speaker_volume.setMax(manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		speaker_volume.setProgress(manager.getStreamVolume(AudioManager.STREAM_MUSIC));
		speaker_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), setVolFlags);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
		
		final SeekBar earphone_volume = (SeekBar) this.findViewById(R.id.earphone_volume);
		earphone_volume.setMax(manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
		earphone_volume.setProgress(manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
		earphone_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, seekBar.getProgress(), setVolFlags);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});		
		
		final Button close = (Button) this.findViewById(R.id.audio_control_close);
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioControlDialog.this.cancel();
			}
		});
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
    		case KeyEvent.KEYCODE_VOLUME_DOWN:
    		case KeyEvent.KEYCODE_VOLUME_UP:
    			final AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    			
    			if (RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL) {
        			manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamVolume(AudioManager.STREAM_MUSIC) + (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN? -1: +1), 0);
        			final SeekBar speaker_volume = (SeekBar) this.findViewById(R.id.speaker_volume);
        			speaker_volume.setProgress(manager.getStreamVolume(AudioManager.STREAM_MUSIC));
    			} else {
        			manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) + (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN? -1: +1), 0);
        			final SeekBar earphone_volume = (SeekBar) this.findViewById(R.id.earphone_volume);
        			earphone_volume.setProgress(manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
    			}
    			
				return true;
    		default:
    	    	return super.onKeyDown(keyCode, event);
    	}    		
    }

	
}
