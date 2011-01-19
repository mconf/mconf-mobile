package com.flazr.rtmp.message;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import com.flazr.rtmp.RtmpHeader;

public abstract class SharedObject extends AbstractMessage {

	/**
	 * SO event name
	 */
	protected String name;

	/**
	 * SO version, used for synchronization purposes
	 */
	protected int version;

	/**
	 * Whether SO persistent
	 */
	protected boolean persistent;
	
	protected Map<String, Object> handlers;
	
    public SharedObject(RtmpHeader header, ChannelBuffer in) {
		super(header, in);
	};

	public SharedObject(String name, boolean persistent) {
		this.name = name;
		this.persistent = persistent;
		this.version = 0;
		this.handlers = new HashMap<String, Object>();
	}

	public String getName() {
		return name;
	}

	/*public void setName(String name) {
		this.name = name;
	}*/

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isPersistent() {
		return persistent;
	}

	/*public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}*/
	
	public String toString() {
		return super.toString() + "SharedObject name: " + name + " version: " + version + " " + (persistent? "persistent" : "non persistent");
	}
	
	public void connect(Channel channel) {
		SharedObjectAmf0 message = SharedObjectAmf0.soConnect(name, persistent);
		channel.write(message);
	}
	
	public void disconnect(Channel channel) {
		SharedObjectAmf0 message = SharedObjectAmf0.soDisconnect(name, persistent);
		channel.write(message);
	}
}
