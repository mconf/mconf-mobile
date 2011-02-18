<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	request.setCharacterEncoding("UTF-8"); 
	response.setCharacterEncoding("UTF-8"); 
%>
<%@page import="org.apache.commons.httpclient.HttpClient"%>
<%@page import="org.apache.commons.httpclient.HttpMethod"%>
<%@page import="org.apache.commons.httpclient.methods.GetMethod"%>

<%@ include file="bbb_api.jsp"%>
<%
    if (request.getParameterMap().isEmpty()) {
    } else if (request.getParameter("action").equals("getMeetings")) {
	String meetings = getMeetings();
%>
<%=meetings%>
<%
    } else if (request.getParameter("action").equals("join")) {
	String meetingID = request.getParameter("meetingID");
	String fullName = request.getParameter("fullName");
	String password = request.getParameter("password");
	String joinUrl = getJoinMeetingURL(fullName, meetingID, password);
	String enterUrl = BigBlueButtonURL + "api/enter";
	String result = "<response><returncode>FAILED</returncode><message>Can't join the meeting</message></response>";
	try {
	    HttpClient client = new HttpClient();
	    HttpMethod method = new GetMethod(joinUrl);
	    client.executeMethod(method);
	    method.releaseConnection();
	    
	    method = new GetMethod(enterUrl);
	    client.executeMethod(method);
	    result = method.getResponseBodyAsString();
	    method.releaseConnection();
	} catch (Exception e) {
	}
%>
<%=result%>
<%
    }
%>