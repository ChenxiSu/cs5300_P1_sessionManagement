<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<script>
function validation(){
	var text = document.getElementById("inputText").value;
	var len = text.length;
	if(len>512) {
		document.getElementById("warning").innerText="words limit has been reached!";
	}else{
		document.getElementById("warning").innerText="";
	}
	
}
function alertOverLong(){
	var text = document.getElementById("inputText").value;
	var len = text.length;
	if(len>512){
		alert("Please limit your letters within 512!");
		return false;
	}else return true;
	
}
</script>
		<jsp:useBean id="Session" type="project1a.session" scope="request" />
		<div id="horizontalBar" >
			<p>NetId:cs2238&nbsp;&nbsp;Session:0_${Session.sessionId}&nbsp;&nbsp;Version:${Session.versionNum}&nbsp;&nbsp;Date:${Session.curTime}</p>
			<h1 id="hello">${Session.message}</h1>
			<form action="index" method="POST" onsubmit="return alertOverLong()">
				<input type="submit" name="action" value="replace" >
				<input type="text" name="content" id="inputText" onkeydown="validation()" /> <p id="warning"></p>
				<input type="submit" name="action" value="refresh" /><br><br>
				<input type="submit" name="action" value="logout">
			</form>
			<br><br>
			<p>Cookie:0_${Session.sessionId}_${Session.versionNum}_0_0 &nbsp;&nbsp; Expires:${Session.expireTime}</p>				
			
		</div>
</body>
</html>