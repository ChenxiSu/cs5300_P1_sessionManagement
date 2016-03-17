
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class welcome
 */
@WebServlet("/welcome")
public class welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, String> sessionManagment = new HashMap<Integer,String>();
    public String cookieName="cs5300p1a";
    public int latestSessionId=0;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public welcome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
//	}
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException{
    	res.setContentType("text/html");  
    	System.out.println("aaaddd");
    	// rewrite first time user
    	Cookie[] cookies = req.getCookies();
    	boolean firstTimeUser = true;
    	Cookie expectedCookie = null;
    	//find out whether request contains our cookie or not
    	if(cookies != null) {
    		for(Cookie cookie:cookies){
        		if(cookie.getName().equals(cookieName)){
        			System.out.println(" i am not first time user");
        			String temp = cookie.getValue();
        			int curSessionId = Integer.parseInt(temp.split("/")[0]);
        			if(sessionManagment.containsKey(curSessionId)){
        				firstTimeUser = false;
            			expectedCookie=cookie;
        			}    			
        			break;
        		}
        	}
    	}
    	
    	
    	if(firstTimeUser){
    		//create a new cookie, generate a serize of values and connect them in a string
    		System.out.println("I am a first time user");
    		int sessionID = latestSessionId+1;
    		latestSessionId++;
    		int version = 1;
    		String message = "Hello User";
    		//LocalDateTime dateTime = LocalDateTime.now();
    		Calendar calobj = Calendar.getInstance();
    		Date curDateTime = calobj.getTime();
    		calobj.add(1, 5);
    		Date exprieDateTime= calobj.getTime();
    		//LocalDateTime expireDateTime = dateTime.minusMinutes(5);

    		//generate a cookie and redirect the page
    		Cookie cookie = cookieGeneration(sessionID,version, message,curDateTime,exprieDateTime);
    		res.addCookie(cookie);
    		res.sendRedirect("index.jsp");
//    		req.getRequestDispatcher("index.jsp").forward(req, res);
    	}
    	else{
    		//not first time user, get the request info and current cookie info		
    		String replaceMessage = req.getParameter("content");
    		String requestAction = req.getParameter("action"); 
    		String content = expectedCookie.getValue();
    		String[] cookieArray = content.split("/");    		
    		int curSessionID = Integer.parseInt(cookieArray[0]);
    		int curVersionNum = Integer.parseInt(cookieArray[1]);
    		String curMessage = cookieArray[2];
    		
    		//situation 1: an already logged in user access through same url again
    		if(replaceMessage==null && requestAction== null){
    			//just need to refresh
    			System.out.println("and I am refreshing");
    			int sessionID = curSessionID;
        		int version = curVersionNum+1;
        		String message = curMessage;
        		//LocalDateTime dateTime = LocalDateTime.now();
        		//LocalDateTime expireDateTime = dateTime.plusMinutes(5);
        		Calendar calobj = Calendar.getInstance();
        		Date curDateTime = calobj.getTime();
        		calobj.add(1, 5);
        		Date exprieDateTime= calobj.getTime();
        		
        		Cookie newCookie = cookieGeneration(sessionID,version,message, curDateTime,exprieDateTime);
        		res.addCookie(newCookie);
        		res.sendRedirect("index.jsp");
    		}
    		// situation 2 :user asked to replace the display string
    		else if(replaceMessage!= null ){
    			String requestContent = req.getParameter("content");
    			System.out.println("and I am replacing");
    			System.out.println(requestContent);
    			int sessionID = curSessionID;
        		int version = curVersionNum+1;
        		String message;
        		if(requestContent.equals("")){
        			System.out.println("no new submitted message");
        			message=curMessage;
        		}
        		else{
        			message = requestContent;
        		}
        		//LocalDateTime dateTime = LocalDateTime.now();
        		//LocalDateTime expireDateTime = dateTime.plusMinutes(5);
        		Calendar calobj = Calendar.getInstance();
        		Date curDateTime = calobj.getTime();
        		calobj.add(1, 5);
        		Date exprieDateTime= calobj.getTime();
        		Cookie newCookie = cookieGeneration(sessionID,version,message, curDateTime,exprieDateTime);
        		res.addCookie(newCookie);
        		res.sendRedirect("index.jsp");
    		}
    		//situation 3: user asked to refresh or log out
    		else if(requestAction!=null){
    			if(requestAction.equals("logout")) {
        			sessionManagment.remove(curSessionID);
        			res.sendRedirect("logout.html");
        		}
        		else if(requestAction.equals("refresh")){
        			System.out.println("and I am refreshing");
        			int sessionID = curSessionID;
            		int version = curVersionNum+1;
            		String message = curMessage;
            		//LocalDateTime dateTime = LocalDateTime.now();
            		//LocalDateTime expireDateTime = dateTime.plusMinutes(5);	
            		Calendar calobj = Calendar.getInstance();
            		Date curDateTime = calobj.getTime();
            		calobj.add(1, 5);
            		Date exprieDateTime= calobj.getTime();
            		Cookie newCookie = cookieGeneration(sessionID,version,message, curDateTime,exprieDateTime);
            		res.addCookie(newCookie);
            		res.sendRedirect("index.jsp");
        		}
    		}
    		else{
    			
    		}
    		
    			
    		
    		
    		
    		/*
    		 * Ignore the version number first, but should be seriously considered 
    		 * in next half
    		 */

    		
    	}

    	
//    	PrintWriter pw=res.getWriter();  
//    	  
//    	String content=req.getParameter("content");//will return value  
//    	if(content==null || content=="" ){
//    		pw.println();
//    	}
//    	pw.println("Welcome ");  
//    	  
//    	pw.close();  
	}
    public Cookie cookieGeneration(int sessionId, int versionNum, String message,
    		Date dt, Date expireDt){
		DateFormat df = new SimpleDateFormat("EEEE dd MM yy HH:mm:ss z");
		//String dateTimeStr = df.format(dt);
		//String expireDtStr = df.format(expireDt);
    	String output = ""+sessionId+"/"+versionNum+"/"+message+"/"+dt.toString()+"/"+expireDt.toString();
    	Cookie cookie= new Cookie(cookieName, output);
		cookie.setMaxAge(2*60);//?????????
		sessionManagment.put(sessionId, output);
		return cookie;
    }
    
    public LocalDateTime expireTimeGeneration(long minutes){
    	LocalDateTime dt = LocalDateTime.now();
    	LocalDateTime expireTime= dt.plusMinutes(minutes);
    	return expireTime;
    }
    
    public int getTheNextSessionId(){
    	int maxSessionId=0;
    	for(int i : sessionManagment.keySet() ){
    		if(i>maxSessionId) maxSessionId = i;
    	}
    	return maxSessionId+1;
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
