package org.mconf.bbb.android;

import org.mconf.bbb.BigBlueButtonClient;
import org.sipdroid.net.KeepAliveSip;
import org.sipdroid.sipua.RegisterAgent;
import org.sipdroid.sipua.SipdroidEngine;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.UserAgentProfile;
import org.sipdroid.sipua.ui.Settings;
import org.sipdroid.sipua.ui.Sipdroid;
import org.zoolu.net.IpAddress;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import android.os.Build;

public class VoipEngine extends SipdroidEngine {
	public UserAgent ua;
	private RegisterAgent ra;
	private KeepAliveSip kas;
	public UserAgentProfile user_profile;
	public SipProvider sip_provider;
	
	public boolean StartEngine(BigBlueButtonClient context) {	
		user_profile = new UserAgentProfile();
		user_profile.username = context.getJoinService().getJoinedMeeting().getFullname();
		user_profile.passwd = Settings.DEFAULT_PASSWORD;
		user_profile.realm = context.getJoinService().getServerUrl().replace("http://", "");
		user_profile.from_url = user_profile.username;
		user_profile.qvalue = Settings.DEFAULT_MMTEL_QVALUE;
		user_profile.mmtel = Settings.DEFAULT_MMTEL;
		user_profile.pub = Settings.DEFAULT_3G;
		
		SipStack.init();
		
		try {
			SipStack.debug_level = 0;
			SipStack.max_retransmission_timeout = 4000;
			SipStack.default_transport_protocols = new String[1];
			SipStack.default_transport_protocols[0] = "tcp";
			
			String version = "Sipdroid/" + Sipdroid.getVersion() + "/" + Build.MODEL;
			SipStack.ua_info = version;
			SipStack.server_info = version;
			
			IpAddress.setLocalIpAddress();
			sip_provider = new SipProvider(IpAddress.localIpAddress, 0);
			user_profile.contact_url = getContactURL(user_profile.username,sip_provider);
			
			if (user_profile.from_url.indexOf("@") < 0) {
				user_profile.from_url +=
					"@"
					+ user_profile.realm;
			}
			
			CheckEngine();
			
			String icsi = null;
			if (user_profile.mmtel == true){
				icsi = "\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"";
			}
	
			ua = new UserAgent(sip_provider, user_profile);
			ra = new RegisterAgent(sip_provider, user_profile.from_url, // modified
					user_profile.contact_url, user_profile.username,
					user_profile.realm, user_profile.passwd, this, user_profile,
					user_profile.qvalue, icsi, user_profile.pub); // added by mandrajg
			kas = new KeepAliveSip(sip_provider,100000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		register();
		listen();
		
		return true;
	}
	
	@Override
	public void register() {
		IpAddress.setLocalIpAddress();
		try {
			if (user_profile == null || user_profile.username.equals("") ||
					user_profile.realm.equals("")) 
				return;
			user_profile.contact_url = getContactURL(user_profile.username,sip_provider);
	
			if (ra != null && ra.register()) {
				System.out.println("registered!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void listen() {
		// TODO Auto-generated method stub
//		super.listen();
	}

}
