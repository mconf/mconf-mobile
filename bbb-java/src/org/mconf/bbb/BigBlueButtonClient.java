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

package org.mconf.bbb;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.api.JoinServiceBase;
import org.mconf.bbb.api.JoinServiceProxy;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.chat.ChatModule;
import org.mconf.bbb.listeners.ListenersModule;
import org.mconf.bbb.users.Participant;
import org.mconf.bbb.users.UsersModule;
import org.mconf.bbb.video.IVideoListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Command;
import com.flazr.util.Utils;

public class BigBlueButtonClient {

	private static final Logger log = LoggerFactory.getLogger(BigBlueButtonClient.class);

	private MainRtmpConnection mainConnection = null;

	private JoinServiceProxy joinServiceProxy = new JoinServiceProxy();

	private int myUserId = -1;
	private ChatModule chatModule = null;
	private UsersModule usersModule = null;
	private ListenersModule listenersModule = null;

	private Set<IBigBlueButtonClientListener> eventListeners = new LinkedHashSet<IBigBlueButtonClientListener>();
	private Set<IVideoListener> videoListeners = new LinkedHashSet<IVideoListener>();

	public void setMyUserId(int myUserId) {
		this.myUserId = myUserId;
		log.info("My userID is {}", myUserId);
	}

	public MainRtmpConnection getConnection()
	{
		return mainConnection;
	}

	public int getMyUserId() {
		return myUserId;
	}

	public void createChatModule(MainRtmpConnection handler, Channel channel) {
		chatModule = new ChatModule(handler, channel);
	}

	public ChatModule getChatModule() {
		return chatModule;
	}

	public void createUsersModule(MainRtmpConnection handler,
			Channel channel) {
		usersModule = new UsersModule(handler, channel);
	}

	public UsersModule getUsersModule() {
		return usersModule;
	}

	public void createListenersModule(MainRtmpConnection handler,
			Channel channel) {
		listenersModule = new ListenersModule(handler, channel);
	}

	public ListenersModule getListenersModule() {
		return listenersModule;
	}

	public void addListener(IBigBlueButtonClientListener listener) {
		eventListeners.add(listener);
	}

	public void removeListener(IBigBlueButtonClientListener listener) {
		eventListeners.remove(listener);
	}

	public Set<IBigBlueButtonClientListener> getListeners() {
		return eventListeners;
	}

	public void addVideoListener(IVideoListener listener) {
		videoListeners.add(listener);
	}

	public void removeVideoListener(IVideoListener listener) {
		listener.stop();
		videoListeners.remove(listener);
	}

	public void removeAllVideoListeners() {
		for (IVideoListener v : videoListeners) {
			v.stop();
		}
		videoListeners.clear();
	}

	public Set<IVideoListener> getVideoListeners() {
		return videoListeners;
	}	

	public void createJoinService(String serverUrl) {
		if (serverUrl.contains("/bigbluebutton/api/"))
			serverUrl = serverUrl.substring(0, serverUrl.indexOf("/bigbluebutton/api/"));
		
		joinServiceProxy.setServer(serverUrl);
	}
	
	public void createJoinService(String serverUrl, String salt) {
		joinServiceProxy.setServer(serverUrl, salt);
	}
	
	public JoinServiceBase getJoinService() {
		return joinServiceProxy.getJoinService();
	}

	public boolean connectBigBlueButton() {

		ClientOptions opt = new ClientOptions();
		opt.setClientVersionToUse(Utils.fromHex("00000000"));
		opt.setHost(getJoinService().getServerUrl().toLowerCase().replace("http://", ""));
		opt.setAppName("bigbluebutton/" + getJoinService().getJoinedMeeting().getConference());
		log.debug(opt.toString());

		mainConnection = new MainRtmpConnection(opt, this);
		return mainConnection.connect();
	}

	@SuppressWarnings("unused")
	private void connectSip() {

	}

	public void disconnect() {
		if (mainConnection != null)
			mainConnection.disconnect();
		joinServiceProxy.resetJoinedMeeting();
	}

	public Collection<Participant> getParticipants() {
		return getUsersModule().getParticipants().values();
	}

	public List<ChatMessage> getPublicChatMessages() {
		return getChatModule().getPublicChatMessage();
	}

	public void sendPrivateChatMessage(String message, int userId) {
		getChatModule().sendPrivateChatMessage(message, userId);
	}

	public void sendPublicChatMessage(String message) {
		getChatModule().sendPublicChatMessage(message);		
	}

	public void raiseHand(boolean value) {
		raiseHand(myUserId, value);
	}
	
	public void raiseHand(int userId, boolean value) {
		getUsersModule().raiseHand(userId, value);
	}
	
	public void assignPresenter(int userId) {
		getUsersModule().assignPresenter(userId);
	}

	public void kickUser(int userId) {
		getUsersModule().kickUser(userId);
	}

	public void kickListener(int listenerId) {
		getListenersModule().doEjectUser(listenerId);
	}

	public void muteUnmuteListener(int listenerId, boolean value){
		getListenersModule().doMuteUnmuteUser(listenerId,value);
	}

	public void muteUnmuteRoom(boolean value)
	{
		getListenersModule().doMuteAllUsers(value);
	}

	public static void main(String[] args) {
		BigBlueButtonClient client = new BigBlueButtonClient();
		client.createJoinService("http://test.bigbluebutton.org/", "03b07");
		client.getJoinService().load();
		if (client.getJoinService().join("English 110", "Eclipse", false)
				&& (client.getJoinService().getJoinedMeeting() != null)) {
			client.connectBigBlueButton();
		}
	}

	public boolean onCommand(String resultFor, Command command) {
		if (usersModule.onCommand(resultFor, command)
				|| chatModule.onCommand(resultFor, command)
				|| listenersModule.onCommand(resultFor, command))
			return true;
		else
			return false;
	}

	public boolean onVideo(byte[] aux) {
		for (IVideoListener l : videoListeners) {
			l.onVideo(aux);
		}
		return true;
	}

	public boolean isConnected() {
		if (mainConnection == null)
			return false;
		else
			return mainConnection.isConnected();
	}

}
