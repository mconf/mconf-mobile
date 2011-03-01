package org.mconf.bbb.android;

import org.sipdroid.media.RtpStreamReceiver;
import org.sipdroid.media.RtpStreamSender;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AudioControlDialog extends Dialog {

	public AudioControlDialog(Context context) {
		super(context);

		this.setContentView(R.layout.audio_config);
		this.setTitle(R.string.audio_config);
		this.setCancelable(true);
		
		final AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		// \TODO verificar se existe volume do microfone realmente
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
		
		final int multiplicator = 100;
		final SeekBar mic_gain = (SeekBar) this.findViewById(R.id.mic_gain);
		mic_gain.setMax(multiplicator);
		mic_gain.setProgress(Float.valueOf(Settings.getMicGain() * multiplicator).intValue());
		mic_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Editor editor = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();
				editor.putString(Settings.PREF_MICGAIN, "" + (progress / (float) multiplicator));
				editor.commit();
				RtpStreamSender.changed = true;
			}
		});
		
		final int streamType = (RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL? AudioManager.STREAM_MUSIC: AudioManager.STREAM_VOICE_CALL);
		
		final SeekBar speaker_volume = (SeekBar) this.findViewById(R.id.speaker_volume);
		final TextView label = (TextView) this.findViewById(R.id.label_speaker_volume);
		label.setText(streamType == AudioManager.STREAM_MUSIC? R.string.speaker_volume: R.string.earphone_volume);
		speaker_volume.setMax(manager.getStreamMaxVolume(streamType));
		speaker_volume.setProgress(manager.getStreamVolume(streamType));
		speaker_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				manager.setStreamVolume(streamType, progress, 0);
			}
		});
	}

}
