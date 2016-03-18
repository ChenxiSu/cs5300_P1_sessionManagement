
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import project1a.session;

/**
 * Servlet implementation class welcome
 */
@WebServlet("/welcome")
public class welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, session> sessionManagment = new HashMap<Integer,session>();
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
        			String temp = cookie.getValue();
        			int curSessionId = Integer.parseInt(temp.split("_")[0]);
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
    		int versionNum = 0;
    		String message = "Hello User";
    		Calendar calobj = Calendar.getInstance();
    		Date curDateTime = calobj.getTime();		
    		Date exprieDateTime= new Date(curDateTime.getTime()+5000);
    		
    		session newSession = new session();
    		newSession.setSessionId(sessionID);
    		newSession.setVersionNum(versionNum);
    		newSession.setMessage(message);
    		newSession.setCurTime(curDateTime);
    		newSession.setExpireTime(exprieDateTime);
    		sessionManagment.put(sessionID, newSession);

    		//generate a cookie, add the cookie to repsonse
    		//also, generate a session and pass it to jsp for building the page
    		Cookie cookie = cookieGeneration(sessionID, versionNum);
    		req.setAttribute("Session", newSession);
    		res.addCookie(cookie);
    		req.getRequestDispatcher("/index.jsp").forward(req, res);
    	}
    	else{
    		//not first time user, get the request info and current cookie info		
    		String replaceMessage = req.getParameter("content");
    		String requestAction = req.getParameter("action"); 
    		String content = expectedCookie.getValue();
    		String[] cookieArray = content.split("_");    		
    		int curSessionID = Integer.parseInt(cookieArray[0]);
    		
    		//situation 1: an already logged in user access through same url again
    		if(replaceMessage==null && requestAction== null){
    			//just need to refresh
    			System.out.println("and I am refreshing");
    			session curSession = sessionManagment.get(curSessionID);
    			String message = curSession.getMessage();
    			//1.whether timeout or not
    			Date expectedExpireTime=curSession.getExpireTime();
    			Calendar calobj = Calendar.getInstance();
        		Date now = calobj.getTime();
        		
        		if(now.after(expectedExpireTime)){
        			// delete the curSession and create new session
            		curSession = timeoutCreateNewSession(curSessionID);
        		}else{
        			// not time out, just update some value
        			int versionNum = curSession.getVersionNum();
        			curSession.setVersionNum(versionNum+1);
            		calobj = Calendar.getInstance();
            		Date curDateTime2 = calobj.getTime();
            		Date expireDateTime2= new Date((curDateTime2.getTime()+5000));
            		curSession.setCurTime( curDateTime2);
            		curSession.setExpireTime(expireDateTime2);
            		sessionManagment.put(curSessionID, curSession);
        		}	
        		//session message won't change
        		Cookie newCookie = cookieGeneration(curSession.getSessionId(),curSession.getVersionNum());
        		req.setAttribute("Session", curSession);
        		res.addCookie(newCookie);
        		req.getRequestDispatcher("/index.jsp").forward(req, res);
    		}
    		
    		// situation 2 :user asked to replace the display string
    		else if(replaceMessage!= null ){
    			String requestContent = req.getParameter("content");
    			
    			//update the current session
    			session curSession = sessionManagment.get(curSessionID);
    			
    			Date expectedExpireTime=curSession.getExpireTime();
    			Calendar calobj = Calendar.getInstance();
        		Date now = calobj.getTime();
        		Cookie newCookie;
        		if(now.after(expectedExpireTime)){
        			//time out
        			session newSession = timeoutCreateNewSession(curSessionID);
        			newCookie = cookieGeneration(newSession.getSessionId(),newSession.getVersionNum());
        			System.out.println(newCookie.getValue());
        			System.out.println(newSession.getVersionNum());
        			req.setAttribute("Session", newSession);
        		}
        		else{
        			//not time out
        			int versionNm = curSession.getVersionNum();
        			curSession.setVersionNum(versionNm+1);
            		if(!requestContent.equals("")){
            			curSession.setMessage(requestContent);
            		}
            		Calendar calobj2 = Calendar.getInstance();
            		Date curDateTime = calobj2.getTime();
            		Date exprieDateTime= new Date((curDateTime.getTime()+5000));
            		curSession.setCurTime(curDateTime);
            		curSession.setExpireTime(exprieDateTime);
            		sessionManagment.put(curSessionID, curSession);
            		newCookie = cookieGeneration(curSessionID,curSession.getVersionNum());
            		req.setAttribute("Session", curSession);
        		}
        		
        		res.addCookie(newCookie);
        		req.getRequestDispatcher("/index.jsp").forward(req, res);
    		}
    		
    		//situation 3: user asked to refresh or log out
    		else if(requestAction!=null){
    			if(requestAction.equals("logout")) {
        			//logout
        			sessionManagment.remove(curSessionID);
        			res.sendRedirect("logout.html");
        		}
        		else if(requestAction.equals("refresh")){
        			//refresh
        			System.out.println("and I am refreshing");
        			session curSession = sessionManagment.get(curSessionID);
        			

        			Date expectedExpireTime=curSession.getExpireTime();
        			Calendar calobj = Calendar.getInstance();
            		Date now = calobj.getTime();
            		
            		if(now.after(expectedExpireTime)){
            			//time out
            			curSession = timeoutCreateNewSession(curSessionID);
            		}
            		else{
            			//not time out
            			int versionNum = curSession.getVersionNum();
            			curSession.setVersionNum(versionNum+1);
                		Calendar calobj2 = Calendar.getInstance();
                		Date curDateTime = calobj2.getTime();
                		Date expireDateTime= new Date((curDateTime.getTime())+5000);
                		curSession.setCurTime(curDateTime);
                		curSession.setExpireTime(expireDateTime);
                		sessionManagment.put(curSessionID, curSession);
            		}
        			
        			
        			
            		Cookie newCookie = cookieGeneration(curSession.getSessionId(),curSession.getVersionNum());
            		req.setAttribute("Session", curSession);
            		res.addCookie(newCookie);
            		req.getRequestDispatcher("/index.jsp").forward(req, res);
        		}
    		}
    		else{
    			
    		}
    		
    			
    		
    		
    		
    		/*
    		 * Ignore the version number first, but should be seriously considered 
    		 * in next half
    		 */

    		
    	}

    	
	}
    public session timeoutCreateNewSession(int id){
    	sessionManagment.remove(id);
    	session newSession = new session();
		//create a new one for this   
    	latestSessionId+=1;
		newSession.setSessionId(latestSessionId);
		newSession.setVersionNum(0);
		Calendar calobj1 = Calendar.getInstance();
		Date curDateTime = calobj1.getTime();
		Date expireDateTime1= new Date((curDateTime.getTime()+5000));
		newSession.setCurTime( curDateTime);
		newSession.setExpireTime(expireDateTime1);
		newSession.setMessage("Hello User");    
		sessionManagment.put(newSession.getSessionId(), newSession);
		
    	return newSession;
    }
    
    public Cookie cookieGeneration(int sessionId, int versionNum){
		DateFormat df = new SimpleDateFormat("EEEE dd MM yy HH:mm:ss z");
		//String dateTimeStr = df.format(dt);
		//String expireDtStr = df.format(expireDt);
    	String output = ""+sessionId+"_"+versionNum;//+"/"+message+"/"+dt.toString()+"/"+expireDt.toString();
    	Cookie cookie= new Cookie(cookieName, output);
		cookie.setMaxAge(2*60);//?????????
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
    
    public void destroy(){
    	latestSessionId=0;
    	sessionManagment.clear();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
