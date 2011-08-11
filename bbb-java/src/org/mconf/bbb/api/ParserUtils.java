package org.mconf.bbb.api;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ParserUtils {

	static public String getNodeValue(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null
				&& list.getLength() > 0
				&& list.item(0) != null
				&& list.item(0).getFirstChild() != null) {
			return list.item(0).getFirstChild().getNodeValue();
		} else
			return "";
	}
	
}
