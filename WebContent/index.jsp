<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	
		
	<%
	//create cookies to]
	Cookie[] cookies = null;

	if(request != null){
		cookies = request.getCookies();
		System.out.println(request.getCookies());
		if(cookies != null){
			boolean hasCookie=false;
			for(Cookie cookie : cookies){
				// if expected cookie is found
				if(cookie.getName().equals("cs5300p1a")){
					String content;
					String tempStr = cookie.getValue();
					String[] tempArray = tempStr.split("/");
					%>
					<div id="horizontalBar" >
						<p>NetId:cs2238   Session:<%= tempArray[0] %>  Version:<%=tempArray[1] %>  Date:<%=tempArray[3] %> </p>
					<%
					System.out.println("found the cookie");
					if(Integer.parseInt(tempArray[1])==1) content="Hello User";
					else content = tempArray[2];
					%>
					<h1 id="hello"><%= content %></h1>
					<%
					hasCookie=true;
					%>
					<form action="index" method="GET" >
						<input type="submit" value="replace">
						<input type="text" name="content">
					</form><br>
					<a href="index?action=refresh"><button type="button">Refresh</button></a><br>
					<a href="index?action=logout"><button type="button">Logout</button></a>
					<br><br>
					<p>Cookie:<%= tempStr%> Expires:<%=tempArray[4] %></p>
				<%
					break;
				}
			}			
		}
	}
	%>
				
		
		
	</div>
</body>
</html>