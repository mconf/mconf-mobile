package org.mconf.bbb.presentation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.IBigBlueButtonClientListener;
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
	private URLConnection urlConnection;

	
	
	public PresentationModule(MainRtmpConnection handler, Channel channel) {
		super(handler, channel);
		
		presentationSO= handler.getSharedObject("presentationSO", false);
		presentationSO.addSharedObjectListener(this);
		presentationSO.connect(channel);
		setUrlParameters(handler.getContext().getHost(), handler.getContext().getConference(), handler.getContext().getRoom());
	}
	
	public boolean loadSlideData(Slide slide) //to be used when the slide will be shown, after the changeSlide event
	{
		if(slide!=null)
		{
			return slide.load();
		}
		else 
			return false;
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
				
				String currentSlide =  presentation.get("slide").toString();
				log.debug("The presenter has shared slides and showing slide " + currentSlide);
				String presentationName = (String) presentation.get("currentPresentation");
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners())
					l.onPresentationShared(presentationName); //tells that a presentation has been shared
				onPresentationReady(presentationName);
				return true;
			}
		}
		return false;
	}


	private void onPresentationReady(String presentationName) {
		
		String  fullUri = host + "/bigbluebutton/presentation/" + conference + "/" + room + "/" + presentationName+"/slides";	
		String slideUri= host + "/bigbluebutton/presentation/" + conference + "/" + room + "/" + presentationName;
		log.debug("fullUri:{}"+fullUri);
		loadSlides(fullUri, slideUri);

	}

	private void loadSlides(String fullUri,	String slideUri)  {
		this.slideUri = slideUri;
		String urlInformation="";
		try {

			URL full = new URL(fullUri);
			urlConnection = full.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader( urlConnection.getInputStream()));

			String buffer;
			while((buffer = in.readLine())!=null)
				urlInformation+=buffer;

			log.debug("loading complete");
			parseSlides(urlInformation);


		} catch (IOException e) {
			log.error("IOException on URL reading");
			e.printStackTrace();
		} catch (SAXException e) {
			log.error("parse error");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			log.error("parse configuration error");
			e.printStackTrace();
		}


	}
	
	private void parseSlides (String presentationString) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(presentationString.getBytes("UTF-8")));
		doc.getDocumentElement().normalize();
		String presentationName = ((Element) doc.getElementsByTagName("presentation")).getAttribute("name");//\TODO classCastException
		
		NodeList slideList = doc.getElementsByTagName("slide");
		for (int i = 0; i < slideList.getLength(); ++i) {
			Element elementSlide = (Element) slideList.item(i);
			
			String sUri = slideUri + "/" + elementSlide.getAttribute("name");
			String thumbUri = slideUri + "/" + elementSlide.getAttribute("thumb");
			int slideNumber = Integer.valueOf(elementSlide.getAttribute("number"));
			Slide slide = new Slide(slideNumber, sUri, thumbUri );
			this.presentation.add(slide);
		}
		
		if(this.presentation.size()>0)
			loadPresentationListener(true, presentationName);
		else 
			loadPresentationListener(false, presentationName);
	}

	private void loadPresentationListener(boolean loaded, String presentationName) {

		if(loaded)
			for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
				l.onPresentationLoaded(presentationName, presentation);
			}
		else
			log.error("presentation not loaded");
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		log.debug("onSharedObjectClear");
		doGetPresentationInfo();
		
	}


	@Override
	public void onSharedObjectSend(ISharedObjectBase so, String method,
			List<?> params) {

		log.debug("onSharedObjectSend");

		if(method.equals("gotoSlideCallback")&& params!=null) //change slide
		{
			currentSlide = ((Double) params.get(0)).intValue();
			for (IBigBlueButtonClientListener l : handler.getContext().getListeners())
				l.onSlideChanged(currentSlide);
		}
		else if(method.equals("sharePresentationCallback")&& params!=null) //presentation shared or removed
		{
			String presentationName = (String) params.get(0);
			boolean share = (Boolean) params.get(1);
			if(share)//presentation shared
			{
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners())
					l.onPresentationShared(presentationName); //tells that a presentation has been shared
				
				//call the function to load the presentation
				currentSlide=0;
				onPresentationReady(presentationName);
				//after the presentation is loaded, another event will be dispatched to tell that
			}
			else //presentation removed
			{
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners())
					l.onPresentationRemoved(); //tells that a presentation has been removed
			}
				
		}
		
	}


	@Override
	public boolean onCommand(String resultFor, Command command) {
		if(onGetPresentationInfo(resultFor, command))
			return true;
		else
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
