<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	request.setCharacterEncoding("UTF-8"); 
	response.setCharacterEncoding("UTF-8"); 
%>
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
%>
<joinUrl><%=joinUrl%></joinUrl>
<%
    }
%>