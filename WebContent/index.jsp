<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	
					<jsp:useBean id="Session" type="project1a.session" scope="request">
					</jsp:useBean>
					<div id="horizontalBar" >
						<p>NetId:cs2238   Session:${Session.sessionId} Version:${Session.versionNum}  Date:${Session.curTime}</p>
					<%
					System.out.println("found the cookie");
					//if(Integer.parseInt(tempArray[1])==0) content="Hello User";
					//else content =curSession.message;
					%>
					<h1 id="hello">${Session.message}</h1>
					<form action="index" method="GET" >
						<input type="submit" value="replace">
						<input type="text" name="content">
					</form><br>
					<a href="index?action=refresh"><button type="button">Refresh</button></a><br>
					<a href="index?action=logout"><button type="button">Logout</button></a>
					<br><br>
					<p>Cookie:${Session.sessionId}_${Session.versionNum} Expires:${Session.expireTime}</p>				
		
		
	</div>
</body>
</html>