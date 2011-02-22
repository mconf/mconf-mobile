package org.mconf.bbb.android.voip;

import java.util.Vector;

import org.sipdroid.codecs.Codec;
import org.sipdroid.codecs.Codecs;
import org.sipdroid.net.KeepAliveSip;
import org.sipdroid.sipua.UserAgentProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoolu.net.IpAddress;
import org.zoolu.sdp.AttributeField;
import org.zoolu.sdp.MediaField;
import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.call.ExtendedCall;
import org.zoolu.sip.call.ExtendedCallListener;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.LogLevel;

import android.os.Build;

public class VoiceModule implements ExtendedCallListener {
	private static final Logger log = LoggerFactory.getLogger(VoiceModule.class);

	protected SipProvider sip_provider;
	protected UserAgentProfile user_profile;
	protected ExtendedCall call;
	protected SessionDescriptor sdp;
	protected KeepAliveSip keep_alive;
	
	public VoiceModule(String username, String url, String number) {
		SipStack.init();
		
		SipStack.debug_level = LogLevel.LOWER;
		SipStack.max_retransmission_timeout = 4000;
		SipStack.default_transport_protocols = new String[1];
		SipStack.default_transport_protocols[0] = "tcp";
		
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
		
		sdp = new SessionDescriptor(user_profile.from_url,
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
		
		sdp.addMedia(new MediaField("audio", user_profile.audio_port, 0, "RTP/AVP", avpvec), afvec);
		
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
				sdp.toString(),
				"\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"");
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
	}

	@Override
	public void onCallCanceling(Call call, Message cancel) {
		log.debug("===========> onCallCanceling");
	}

	@Override
	public void onCallClosed(Call call, Message resp) {
		log.debug("===========> onCallClosed");
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
	}

	@Override
	public void onCallRinging(Call call, Message resp) {
		log.debug("===========> onCallRinging");
	}

	@Override
	public void onCallTimeout(Call call) {
		log.debug("===========> onCallTimeout");
	}
	
}
