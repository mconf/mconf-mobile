package org.mconf.bbb.android.voip;

import java.util.Enumeration;
import java.util.Vector;

import org.mconf.bbb.android.R;
import org.sipdroid.codecs.Codec;
import org.sipdroid.codecs.Codecs;
import org.sipdroid.media.JAudioLauncher;
import org.sipdroid.media.MediaLauncher;
import org.sipdroid.media.RtpStreamReceiver;
import org.sipdroid.net.KeepAliveSip;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.UserAgentProfile;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Sipdroid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoolu.net.IpAddress;
import org.zoolu.sdp.AttributeField;
import org.zoolu.sdp.MediaDescriptor;
import org.zoolu.sdp.MediaField;
import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.call.ExtendedCall;
import org.zoolu.sip.call.ExtendedCallListener;
import org.zoolu.sip.call.SdpTools;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.LogLevel;
import org.zoolu.tools.Parser;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.widget.Toast;

public class VoiceModule implements ExtendedCallListener {
	private static final Logger log = LoggerFactory.getLogger(VoiceModule.class);

	protected SipProvider sip_provider;
	protected UserAgentProfile user_profile;
	protected ExtendedCall call;
	protected SessionDescriptor local_sdp;
	protected KeepAliveSip keep_alive;

	private MediaLauncher audio_app = null;

	private int speakerMode;

	private boolean mute;

	final private Context context;

	public VoiceModule(Context context, String username, String url) {
		this.context = context;
		
		SipStack.init();
		Sipdroid.release = false;
		Receiver.call_state = UserAgent.UA_STATE_IDLE;
		
		SipStack.debug_level = LogLevel.LOWER;
		SipStack.max_retransmission_timeout = 4000;
		SipStack.default_transport_protocols = new String[1];
		SipStack.default_transport_protocols[0] = "udp";
		
		SipStack.ua_info = "BigBlueButton/" + Build.MODEL;
		SipStack.server_info = SipStack.ua_info;
		
		user_profile = new UserAgentProfile();
		user_profile.username = username;
		user_profile.passwd = "";
		user_profile.realm = url.replace("http://", "");
		user_profile.realm_orig = user_profile.realm;
		user_profile.from_url = username + "@" + user_profile.realm;
	
		IpAddress.setLocalIpAddress();
		sip_provider = new SipProvider(IpAddress.localIpAddress, 0);
		user_profile.contact_url = getContactURL(user_profile.username, sip_provider);
		user_profile.initContactAddress(sip_provider);
				
		keep_alive = new KeepAliveSip(sip_provider, 100000);
	}
	
	public void call(String number) {
		if (isOnCall())
			return;
		
		local_sdp = new SessionDescriptor(user_profile.from_url,
				sip_provider.getViaAddress());
		
		Vector<String> avpvec = new Vector<String>();
		Vector<AttributeField> afvec = new Vector<AttributeField>();
		// offer all known codecs
		for (int i : Codecs.getCodecs()) {
			Codec codec = Codecs.get(i);
			if (i == 0) codec.init();
			avpvec.add(String.valueOf(i));
			if (codec.number() == 9)
				afvec.add(new AttributeField("rtpmap", String.format("%d %s/%d", i, codec.userName(), 8000))); // kludge for G722. See RFC3551.
			else
				afvec.add(new AttributeField("rtpmap", String.format("%d %s/%d", i, codec.userName(), codec.samp_rate())));
		}
		if (user_profile.dtmf_avp != 0){
			avpvec.add(String.valueOf(user_profile.dtmf_avp));
			afvec.add(new AttributeField("rtpmap", String.format("%d telephone-event/%d", user_profile.dtmf_avp, user_profile.audio_sample_rate)));
			afvec.add(new AttributeField("fmtp", String.format("%d 0-15", user_profile.dtmf_avp)));
		}
				
		//String attr_param = String.valueOf(avp);
		
		local_sdp.addMedia(new MediaField("audio", user_profile.audio_port, 0, "RTP/AVP", avpvec), afvec);
		
		call = new ExtendedCall(sip_provider, 
				user_profile.from_url, 
				user_profile.contact_url, 
				user_profile.username,
				user_profile.realm,
				user_profile.passwd,
				this);
		String target_url = number + "@" + user_profile.realm;
		target_url = sip_provider.completeNameAddress(target_url).toString();
		
		call.call(target_url, 
				local_sdp.toString(),
				/*"\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\""*/ null);
	}
	
	public boolean isOnCall() {
		return Receiver.call_state == UserAgent.UA_STATE_INCALL;
	}
	
	public void hang() {
		if (call != null && isOnCall()) {
			call.hangup();
			call = null;
		}
	}
	
	protected String getContactURL(String username,SipProvider sip_provider) {
		int i = username.indexOf("@");
		if (i != -1) {
			// if the username already contains a @ 
			//strip it and everthing following it
			username = username.substring(0, i);
		}

		return username + "@" + IpAddress.localIpAddress
		+ (sip_provider.getPort() != 0?":"+sip_provider.getPort():"")
		+ ";transport=" + sip_provider.getDefaultTransport();		
	}
		
	@Override
	public void onCallTransfer(ExtendedCall call, NameAddress referTo,
			NameAddress referedBy, Message refer) {
		log.debug("===========> onCallTransfer");
	}

	@Override
	public void onCallTransferAccepted(ExtendedCall call, Message resp) {
		log.debug("===========> onCallTransferAccepted");
	}

	@Override
	public void onCallTransferFailure(ExtendedCall call, String reason,
			Message notify) {
		log.debug("===========> onCallTransferFailure");
	}

	@Override
	public void onCallTransferRefused(ExtendedCall call, String reason,
			Message resp) {
		log.debug("===========> onCallTransferRefused");
	}

	@Override
	public void onCallTransferSuccess(ExtendedCall call, Message notify) {
		log.debug("===========> onCallTransferSuccess");
	}

	@Override
	public void onCallAccepted(Call call, String sdp, Message resp) {
		log.debug("===========> onCallAccepted");
		
		Receiver.call_state = UserAgent.UA_STATE_INCALL;
		makeToast(R.string.connection_established);
		RtpStreamReceiver.good = RtpStreamReceiver.lost = RtpStreamReceiver.loss = RtpStreamReceiver.late = 0;
		mute = false;
		speakerMode = AudioManager.MODE_IN_CALL;
		
		SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
		SessionDescriptor new_sdp = new SessionDescriptor(local_sdp.getOrigin(),
				local_sdp.getSessionName(),
				local_sdp.getConnection(),
				local_sdp.getTime());
		new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
		new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp.getMediaDescriptors());
		local_sdp = new_sdp;
		call.setLocalSessionDescriptor(local_sdp.toString());

		Codecs.Map codecs = Codecs.getCodec(local_sdp);
		int local_audio_port = 0,
			local_video_port = 0,
			dtmf_pt = 0,
			remote_video_port = 0,
			remote_audio_port = 0;
		
		MediaDescriptor m = local_sdp.getMediaDescriptor("video");
		if (m != null)
			local_video_port = m.getMedia().getPort();
		m = local_sdp.getMediaDescriptor("audio");
		if (m != null) {
			local_audio_port = m.getMedia().getPort();
			if (m.getMedia().getFormatList().contains(String.valueOf(user_profile.dtmf_avp)))
				dtmf_pt = user_profile.dtmf_avp;
		}
		
		String remote_media_address = (new Parser(remote_sdp.getConnection().toString())).skipString().skipString().getString();
		for (Enumeration<MediaDescriptor> e = remote_sdp.getMediaDescriptors()
				.elements(); e.hasMoreElements();) {
			MediaField media = e.nextElement().getMedia();
			if (media.getMedia().equals("audio"))
				remote_audio_port = media.getPort();
			if (media.getMedia().equals("video"))
				remote_video_port = media.getPort();
		}
		
		String audio_in = null;
		if (user_profile.send_tone) {
			audio_in = JAudioLauncher.TONE;
		} else if (user_profile.send_file != null) {
			audio_in = user_profile.send_file;
		}
		String audio_out = null;
		if (user_profile.recv_file != null) {
			audio_out = user_profile.recv_file;
		}

		audio_app  = new JAudioLauncher(local_audio_port,
				remote_media_address, remote_audio_port, 0, audio_in,
				audio_out, codecs.codec.samp_rate(),
				user_profile.audio_sample_size,
				codecs.codec.frame_size(), null, codecs, dtmf_pt);
		audio_app.startMedia();		
		setSpeaker(speakerMode);
	}

	@Override
	public void onCallCanceling(Call call, Message cancel) {
		log.debug("===========> onCallCanceling");
	}

	@Override
	public void onCallClosed(Call call, Message resp) {
		log.debug("===========> onCallClosed");

		Receiver.call_state = UserAgent.UA_STATE_IDLE;
		makeToast(R.string.connection_closed);
		
		if (audio_app != null) {
			audio_app.stopMedia();
			audio_app = null;
		}
	}

	@Override
	public void onCallClosing(Call call, Message bye) {
		log.debug("===========> onCallClosing");
	}

	@Override
	public void onCallConfirmed(Call call, String sdp, Message ack) {
		log.debug("===========> onCallConfirmed");
	}

	@Override
	public void onCallIncoming(Call call, NameAddress callee,
			NameAddress caller, String sdp, Message invite) {
		log.debug("===========> onCallIncoming");
	}

	@Override
	public void onCallModifying(Call call, String sdp, Message invite) {
		log.debug("===========> onCallModifying");
	}

	@Override
	public void onCallReInviteAccepted(Call call, String sdp, Message resp) {
		log.debug("===========> onCallReInviteAccepted");
	}

	@Override
	public void onCallReInviteRefused(Call call, String reason, Message resp) {
		log.debug("===========> onCallReInviteRefused");
	}

	@Override
	public void onCallReInviteTimeout(Call call) {
		log.debug("===========> onCallReInviteTimeout");
	}

	@Override
	public void onCallRedirection(Call call, String reason,
			Vector<String> contactList, Message resp) {
		log.debug("===========> onCallRedirection");
	}

	@Override
	public void onCallRefused(Call call, String reason, Message resp) {
		log.debug("===========> onCallRefused");
		
		Receiver.call_state = UserAgent.UA_STATE_IDLE;	
		makeToast(R.string.connection_refused);
	}

	@Override
	public void onCallRinging(Call call, Message resp) {
		log.debug("===========> onCallRinging");
	}

	@Override
	public void onCallTimeout(Call call) {
		log.debug("===========> onCallTimeout");
	}

	public boolean isMuted() {
		return mute;
	}
	
	public void muteCall(boolean mute) {
		if (audio_app != null && mute != this.mute) {
			this.mute = mute;
			audio_app.muteMedia();
		}
	}

	public int getSpeaker() {
		return speakerMode;
	}

	public void setSpeaker(int mode) {
		if (audio_app != null) {
			speakerMode = mode;
			audio_app.speakerMedia(speakerMode);
		}
	}
	
	private void makeToast(final int resId) {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
