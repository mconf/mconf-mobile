package org.mconf.bbb.presentation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.MainRtmpConnection;
import org.mconf.bbb.Module;
import org.mconf.bbb.listeners.Listener;
import org.mconf.bbb.listeners.ListenersModule;
import org.red5.server.api.IAttributeStore;
import org.red5.server.api.so.IClientSharedObject;
import org.red5.server.api.so.ISharedObjectBase;
import org.red5.server.api.so.ISharedObjectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;

public class PresentationModule  extends Module implements ISharedObjectListener {

	
	private static final Logger log = LoggerFactory.getLogger(PresentationModule.class);
	private final IClientSharedObject presentationSO;
	
	private ArrayList<Slide> presentation = new ArrayList<Slide>();
	
	private int currentSlide;
	private String host;
	private String conference;
	private String room;
	private String slideUri;
	
	
	public PresentationModule(MainRtmpConnection handler, Channel channel) {
		super(handler, channel);
		
		presentationSO= handler.getSharedObject("presentationSO", false);
		presentationSO.addSharedObjectListener(this);
		presentationSO.connect(channel);
	}

	public void doGetPresentationInfo()
	{
		Command cmd = new CommandAmf0("presentation.getPresentationInfo", null);
    	handler.writeCommandExpectingResult(channel, cmd);
	}
	
	public boolean onGetPresentationInfo(String resultFor, Command command)
	{
		if(resultFor.equals("presentation.getPresentationInfo"))
		{
			Map<String, Object> args = (Map<String, Object>) command.getArg(0);
			Map<String, Object> presentation = (Map<String, Object>) args.get("presentation");
			boolean sharing = (Boolean) presentation.get("sharing");
			if(sharing)
			{
				currentSlide = (Integer) presentation.get("slide");
				log.debug("The presenter has shared slides and showing slide " + currentSlide);
				String presentationName = (String) presentation.get("currentPresentation");
				onPresentationReady(presentationName);
				return true;
			}
		}
		return false;
	}


	
	private void onPresentationReady(String presentationName) {
		
		String  fullUri = host + "/bigbluebutton/presentation/" + conference + "/" + room + "/" + presentationName+"/slides";	
		String slideUri= host + "/bigbluebutton/presentation/" + conference + "/" + room + "/" + presentationName;
		loadSlides(fullUri, slideUri);

	}

	private void loadSlides(String fullUri,	String slideUri) {
		// TODO Auto-generated method stub
		//how to create a java function like the load function on PresentationService
		this.slideUri = slideUri;
	}
	
	private void parseSlides (String presentationString) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(presentationString.getBytes("UTF-8")));
		doc.getDocumentElement().normalize();
		
		String presentationName = ((Element) doc.getElementsByTagName("presentation").item(0)).getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		
		NodeList slideList = doc.getElementsByTagName("slide");
		for (int i = 0; i < slideList.getLength(); ++i) {
			Element elementSlide = (Element) slideList.item(i);
			
			String sUri = slideUri + "/" + elementSlide.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
			String thumbUri = slideUri + "/" + elementSlide.getElementsByTagName("thumb").item(0).getFirstChild().getNodeValue();
			int slideNumber = Integer.valueOf(elementSlide.getElementsByTagName("number").item(0).getFirstChild().getNodeValue());
			Slide slide = new Slide(slideNumber, sUri, thumbUri );
			this.presentation.add(slide);
		}
		
		if(this.presentation.size()>0)
			loadPresentationListener(true, presentationName);
		else 
			loadPresentationListener(false, presentationName);
	}

	private void loadPresentationListener(boolean loaded, String presentationName) {
		// TODO like in PresentationService
		
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSharedObjectSend(ISharedObjectBase so, String method,
			List<?> params) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onCommand(String resultFor, Command command) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	
	@Override
	public void onSharedObjectConnect(ISharedObjectBase so) {
		log.debug("onSharedObjectConnect");
	}

	@Override
	public void onSharedObjectDelete(ISharedObjectBase so, String key) {
		log.debug("onSharedObjectDelete");
	}

	@Override
	public void onSharedObjectDisconnect(ISharedObjectBase so) {
		log.debug("onSharedObjectDisconnect");
	}
	
	public ArrayList<Slide> getPresentation() {
		return presentation;
	}
	
	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so, String key,
			Object value) {
		log.debug("onSharedObjectUpdate1");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			IAttributeStore values) {
		log.debug("onSharedObjectUpdate2");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			Map<String, Object> values) {
		log.debug("onSharedObjectUpdate3");
	}
	
	
	public void setUrlParameters(String host,String conference,String room) {
		this.host = host;
		this.conference = conference;
		this.room = room;
	}

	

}
