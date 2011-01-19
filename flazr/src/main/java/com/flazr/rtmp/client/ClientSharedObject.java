package com.flazr.rtmp.client;

import org.jboss.netty.channel.Channel;

public class ClientSharedObject {

	private boolean persistent;
	private String name;
	private boolean initialSyncReceived;
	private Channel source;

	public ClientSharedObject(String name, boolean persistent) {
		this.name = name;
		this.persistent = persistent;
	}

	public boolean isPersistentObject() {
		return persistent;
	}

	/**
	 * Connect the shared object using the passed connection.
	 * 
	 * @param conn Attach SO to given connection
	 */
	public void connect(Channel conn) {
		if (isConnected())
			throw new RuntimeException("already connected");

/*
		source = conn;
		SharedObjectMessage msg = new SharedObjectMessage(name, 0, isPersistentObject());
		msg.addEvent(new SharedObjectEvent(SharedObjectEvent.Type.SERVER_CONNECT, null, null));
		//Channel c = ((RTMPConnection) conn).getChannel((byte) 3);
		//Command command = new Command()
		source.write(msg);
*/
	}

	private boolean isConnected() {
		return initialSyncReceived;
	}

	
}
