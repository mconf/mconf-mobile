package org.mconf.bbb.api;

import org.w3c.dom.Element;

public class Metadata {

	protected String email, 
			description,
			meetingId;
	
	public String getEmail() {
		return email;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getMeetingId() {
		return meetingId;
	}

//	<metadata>
//	    <email>fcecagno@gmail.com</email>
//	    <description>Test</description>
//	    <meetingId>English 101</meetingId>
//  </metadata>
	public boolean parse(Element elementMetadata) {
		email = ParserUtils.getNodeValue(elementMetadata, "email");
		description = ParserUtils.getNodeValue(elementMetadata, "description");
		meetingId = ParserUtils.getNodeValue(elementMetadata, "meetingId");
		return true;
	}

	@Override
	public String toString() {
		return "Metadata \n     description=" + description + "\n     email="
				+ email + "\n     meetingId=" + meetingId;
	}

}
