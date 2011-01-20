package org.mconf.bbb.api;

import org.apache.log4j.BasicConfigurator;
import org.mconf.bbb.chat.BigBlueButtonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingApi {

	private static final Logger log = LoggerFactory.getLogger(TestingApi.class);
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		
		BigBlueButtonClient client = new BigBlueButtonClient();
		client.load();
		
		if (!client.getMeetings().isEmpty()) {
			client.join(client.getMeetings().get(0), "My name", true);
		}
	}

}
