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

package org.mconf.bbb.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Meetings {

	private static final Logger log = LoggerFactory.getLogger(Meetings.class);

	private List<Meeting> meetings = new ArrayList<Meeting>();

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public Meetings() {

	}

	public boolean parse(String str) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException, DOMException, ParseException {
		meetings.clear();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
		doc.getDocumentElement().normalize();

		NodeList nodeMeetings = doc.getElementsByTagName("meeting");
		// if nodeMeetings.getLength() == 0 and the conference is on, probably the "salt" is wrong
		log.debug("parsing: {}", str);
		log.debug("nodeMeetings.getLength() = {}", nodeMeetings.getLength());
		for (int i = 0; i < nodeMeetings.getLength(); ++i) {
			Meeting meeting = new Meeting();
			if (meeting.parse((Element) nodeMeetings.item(i))) {
				meetings.add(meeting);				
			}
		}

		return true;
	}

	@Override
	public String toString() {
		if (meetings.isEmpty())
			return "No meetings currently running";
		
		String str = "";
		for (Meeting meeting : meetings) {
			str += meeting.toString() + "\n"; 
		}
		return str.substring(0, str.length() - 1);
	}

}
