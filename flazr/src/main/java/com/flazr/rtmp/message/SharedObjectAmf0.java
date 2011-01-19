package com.flazr.rtmp.message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.amf.Amf0Value;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.so.ISharedObjectEvent;
import com.flazr.rtmp.so.ISharedObjectEvent.Type;
import com.flazr.rtmp.so.SharedObjectEvent;
import com.flazr.rtmp.so.SharedObjectTypeMapping;

public class SharedObjectAmf0 extends SharedObject {

	private static final Logger logger = LoggerFactory.getLogger(SharedObjectAmf0.class);
	
	/**
	 * SO events chain
	 */
	protected ConcurrentLinkedQueue<ISharedObjectEvent> events;

	public SharedObjectAmf0(RtmpHeader header, ChannelBuffer in) {
		super(header, in);
	}

	public SharedObjectAmf0(String name, boolean persistent) {
		super(name, persistent);
		this.events = new ConcurrentLinkedQueue<ISharedObjectEvent>();
	}

	@Override
	MessageType getMessageType() {
		return MessageType.SHARED_OBJECT_AMF0;
	}

	public void addEvent(ISharedObjectEvent event) {
		events.add(event);
	}

	public void addEvent(Type type, String key, Object value) {
		events.add(new SharedObjectEvent(type, key, value));
	}

	public ConcurrentLinkedQueue<ISharedObjectEvent> getEvents() {
		return events;
	}
	
	public void clear() {
		events.clear();
	}

	public boolean isEmpty() {
		return events.isEmpty();
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" { ");
		final Iterator<ISharedObjectEvent> it = events.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(" }");
		return sb.toString();
	}
	
	/**
	 * Encode a string without the string type byte
	 * @param out
	 * @param s
	 */
	void encodeString(ChannelBuffer out, String s) {
		out.writeShort((short) s.length());
		out.writeBytes(s.getBytes());
	}
	
	@Override
	public ChannelBuffer encode() {
        ChannelBuffer out = ChannelBuffers.dynamicBuffer();
        encodeString(out, name);
        // SO version
        out.writeInt(version);
        // Encoding (this always seems to be 2 for persistent shared objects)
        out.writeInt(persistent? 2 : 0);
        // unknown field
        out.writeInt(0);
        
        int mark, len;
        
        for (ISharedObjectEvent event : events) {
            byte type = SharedObjectTypeMapping.toByte(event.getType());

            switch (event.getType()) {
				case SERVER_CONNECT:
				case SERVER_DISCONNECT:
				case CLIENT_INITIAL_DATA:
				case CLIENT_CLEAR_DATA:
					out.writeByte(type);
					out.writeInt(0);
        			break;
        			
				case SERVER_DELETE_ATTRIBUTE:
				case CLIENT_DELETE_DATA:
				case CLIENT_UPDATE_ATTRIBUTE:
					out.writeByte(type);
					mark = out.writerIndex();
					out.writeInt(0); // we will be back

					encodeString(out, event.getKey());
					len = out.writerIndex() - mark - 4;
					out.markWriterIndex();
					
					out.writerIndex(mark);
					out.writeInt(len);
					
					out.resetWriterIndex(); // for some reason, it's needed to write an integer at the end
					out.writeInt(0);
					break;
					
				case SERVER_SET_ATTRIBUTE:
				case CLIENT_UPDATE_DATA:
					if (event.getKey() == null) {
						// Update multiple attributes in one request
						Map<?, ?> initialData = (Map<?, ?>) event.getValue();
						for (Object o : initialData.keySet()) {
							
							out.writeByte(type);
							mark = out.writerIndex();
							out.writeInt(0); // we will be back
							
							String key = (String) o;
							encodeString(out, key);
							Amf0Value.encode(out, initialData.get(key));
							
							len = out.writerIndex() - mark - 4;
							out.writerIndex(mark);
							out.writeInt(len);
						}
					} else {
						out.writeByte(type);
						mark = out.writerIndex();
						out.writeInt(0); // we will be back
						
						encodeString(out, event.getKey());
						Amf0Value.encode(out, event.getValue());
						out.markWriterIndex();

						len = out.writerIndex() - mark - 4;
						out.writerIndex(mark);
						out.writeInt(len);

						out.resetWriterIndex();
						out.writeInt(0);
					}
					break;
				case CLIENT_SEND_MESSAGE:
				case SERVER_SEND_MESSAGE:
					// Send method name and value
					out.writeByte(type);
					mark = out.writerIndex();
					out.writeInt(0); // we will be back

					// Serialize name of the handler to call...
					Amf0Value.encode(out, event.getKey());
					// ...and the arguments
					for (Object arg : (List<?>) event.getValue()) {
						Amf0Value.encode(out, arg);
					}
					out.markWriterIndex();
					
					len = out.writerIndex() - mark - 4;
					out.writerIndex(mark);
					out.writeInt(len);
					
					out.resetWriterIndex();
					out.writeInt(0);
					break;

				case CLIENT_STATUS:
					out.writeByte(type);
					final String status = event.getKey();
					final String message = (String) event.getValue();
					out.writeInt(message.length() + status.length() + 4);
					encodeString(out, message);
					encodeString(out, status);
					break;

				default:
					logger.error("Unknown event " + event.getType());
					
					out.writeByte(type);
					mark = out.writerIndex();
					out.writeInt(0); // we will be back
					
					encodeString(out, event.getKey());
					Amf0Value.encode(out, event.getValue());
					
					len = out.writerIndex() - mark - 4;
					out.writerIndex(mark);
					out.writeInt(len);
					break;
        	}
        }
        return out;
	}

	/**
	 * Read a string without the string type byte
	 * @param in
	 * @return a decoded string
	 */
	String decodeString(ChannelBuffer in) {
		int length = in.readShort();
		byte[] str = new byte[length];
		in.readBytes(str);
		return new String(str);
	}
	
	@Override
	public void decode(ChannelBuffer in) {
		name = decodeString(in);
		version = in.readInt();
		persistent = in.readInt() == 2;
		in.skipBytes(4);
		
		events = new ConcurrentLinkedQueue<ISharedObjectEvent>();
		
		while (in.readableBytes() > 0) {
			ISharedObjectEvent.Type type = SharedObjectTypeMapping.toType(in.readByte());
			if (type == null) {
				in.skipBytes(in.readableBytes());
				continue;
			}
			
			String key = null;
			Object value = null;
			
			int length = in.readInt();
			if (type == ISharedObjectEvent.Type.CLIENT_STATUS) {
				// Status code
				key = decodeString(in);
				// Status level
				value = decodeString(in);
			} else if (type == ISharedObjectEvent.Type.CLIENT_UPDATE_DATA) {
				key = null;
				// Map containing new attribute values
				final Map<String, Object> map = new HashMap<String, Object>();
				final int start = in.readerIndex();
				while (in.readerIndex() - start < length) {
					String tmp = decodeString(in);
					map.put(tmp, Amf0Value.decode(in));
				}
				value = map;
			} else if (type != ISharedObjectEvent.Type.SERVER_SEND_MESSAGE && type != ISharedObjectEvent.Type.CLIENT_SEND_MESSAGE) {
				if (length > 0) {
					key = decodeString(in);
					if (length > key.length() + 2) {
						value = Amf0Value.decode(in);
					}
				}
			} else {
				final int start = in.readerIndex();
				// the "send" event seems to encode the handler name
				// as complete AMF string including the string type byte
				key = (String) Amf0Value.decode(in);

				// read parameters
				final List<Object> list = new LinkedList<Object>();
				// while loop changed for JIRA CODECS-9
				while (in.readerIndex() - start < length) {
					list.add(Amf0Value.decode(in));
				}
				value = list;
			}
			
			addEvent(type, key, value);
		}
	}

	public static SharedObjectAmf0 soConnect(String name, boolean persistent) {
	    SharedObjectAmf0 so = new SharedObjectAmf0(name, persistent);
		so.addEvent(Type.SERVER_CONNECT, null, null);
		return so;
	}

	public static SharedObjectAmf0 soDisconnect(String name, boolean persistent) {
	    SharedObjectAmf0 so = new SharedObjectAmf0(name, persistent);
		so.addEvent(Type.SERVER_DISCONNECT, null, null);
		return so;
	}

	public static SharedObjectAmf0 soSetAttribute(String name, boolean persistent, String attribute, String value) {
	    SharedObjectAmf0 so = new SharedObjectAmf0(name, persistent);
		so.addEvent(Type.SERVER_SET_ATTRIBUTE, attribute, value);
		return so;
	}

	public static SharedObjectAmf0 soDeleteAttribute(String name, boolean persistent, String attribute) {
	    SharedObjectAmf0 so = new SharedObjectAmf0(name, persistent);
		so.addEvent(Type.SERVER_DELETE_ATTRIBUTE, attribute, null);
		return so;
	}
	
	public static SharedObject soSendMessage(String name, boolean persistent, String message, List<?> args) {
	    SharedObjectAmf0 so = new SharedObjectAmf0(name, persistent);
		so.addEvent(Type.SERVER_SEND_MESSAGE, message, args);
		return so;
	}
	
}
