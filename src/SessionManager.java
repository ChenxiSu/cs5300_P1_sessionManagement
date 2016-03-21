
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
@WebServlet("/SessionManager")
public class SessionManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap <Integer, session> sessionManagment = new ConcurrentHashMap<Integer,session>();
    public String cookieName="cs5300p1a";
    public int latestSessionId=0;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SessionManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException{
    	res.setContentType("text/html");  
    	// rewrite first time user
    	Cookie[] cookies = req.getCookies();
    	boolean firstTimeUser = true;
    	Cookie expectedCookie = null;
    	//find out whether request contains our cookie or not
    	if(cookies != null) {
    		for(Cookie cookie:cookies){
        		if(cookie.getName().equals(cookieName)){
        			String temp = cookie.getValue();
        			int curSessionId = Integer.parseInt(temp.split("_")[1]);
        			Calendar calobj = Calendar.getInstance();
            		Date now = calobj.getTime();
        			for(session s : sessionManagment.values() ){
        				if(now.after(s.getExpireTime())) sessionManagment.remove(s.getSessionId());
        			}
        			if(sessionManagment.containsKey(curSessionId)){
        				firstTimeUser = false;
            			expectedCookie=cookie;
        			}    			
        			break;
        		}
        	}
    	}
    	
    	// a new session should be created and assigned
    	if(firstTimeUser){
    		//create a new cookie, generate a serize of values and connect them in a string
    		System.out.println("dealing with a first time user");
    		int sessionID = latestSessionId+1;
    		latestSessionId++;
    		int versionNum = 0;
    		String message = "Hello User";
    		Calendar calobj = Calendar.getInstance();
    		Date curDateTime = calobj.getTime();		
    		Date exprieDateTime= new Date(curDateTime.getTime()+30000);
    		
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
    		//already a user 		
    		
    		
    		String requestAction = req.getParameter("action"); 
    		
    		String content = expectedCookie.getValue();
    		String[] cookieArray = content.split("_");    		
    		int curSessionID = Integer.parseInt(cookieArray[1]);
    		
    		//situation 1: with no request for action but with cookie info
    		if(requestAction == null){
    			//just need to refresh
    			System.out.println("dealing with request coming through url");
    			session curSession = sessionManagment.get(curSessionID);
    			String message = curSession.getMessage();
    			//1.whether timeout or not
    			Date expectedExpireTime=curSession.getExpireTime();
    			boolean inExpiredState = timeout(expectedExpireTime);
        		
        		if(inExpiredState){
        			// delete the curSession and create new session
            		curSession = timeoutCreateNewSession(curSessionID);
        		}else{
        			// not time out, just update some value
        			int versionNum = curSession.getVersionNum();
        			curSession.setVersionNum(versionNum+1);
            		Calendar calobj = Calendar.getInstance();
            		Date curDateTime2 = calobj.getTime();
            		Date expireDateTime2= new Date((curDateTime2.getTime()+30000));
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
    		
    		// situation 2 :user with request for actions
    		else{
    			// request for replace
    			if(requestAction.equals("replace") ){
    				String requestContent = req.getParameter("content");
    				//update the current session
        			session curSession = sessionManagment.get(curSessionID);
        			
        			Date expectedExpireTime=curSession.getExpireTime();
        			boolean inExpiredState=timeout(expectedExpireTime);
            		Cookie newCookie;
            		//session expired
            		if(inExpiredState){
            			session newSession = timeoutCreateNewSession(curSessionID);
            			newCookie = cookieGeneration(newSession.getSessionId(),newSession.getVersionNum());
            			System.out.println(newCookie.getValue());
            			System.out.println(newSession.getVersionNum());
            			req.setAttribute("Session", newSession);
            		}
            		// session still active
            		else{
            			int versionNm = curSession.getVersionNum();
            			curSession.setVersionNum(versionNm+1);
                		if(!requestContent.equals("")){
                			curSession.setMessage(requestContent);
                		}
                		Calendar calobj2 = Calendar.getInstance();
                		Date curDateTime = calobj2.getTime();
                		Date exprieDateTime= new Date((curDateTime.getTime()+30000));
                		curSession.setCurTime(curDateTime);
                		curSession.setExpireTime(exprieDateTime);
                		sessionManagment.put(curSessionID, curSession);
                		newCookie = cookieGeneration(curSessionID,curSession.getVersionNum());
                		req.setAttribute("Session", curSession);
                		res.addCookie(newCookie);
                		req.getRequestDispatcher("/index.jsp").forward(req, res);
            		}
    			}
    			else if(requestAction.equals("refresh")){
    				System.out.println("dealing with refresh request");
        			session curSession = sessionManagment.get(curSessionID);
        			
        			Date expectedExpireTime=curSession.getExpireTime();
        			boolean inExpiredState = timeout(expectedExpireTime);
            		//session expired
            		if(inExpiredState){
            			curSession = timeoutCreateNewSession(curSessionID);
            		}
            		//session still active
            		else{
            			int versionNum = curSession.getVersionNum();
            			curSession.setVersionNum(versionNum+1);
                		Calendar calobj2 = Calendar.getInstance();
                		Date curDateTime = calobj2.getTime();
                		Date expireDateTime= new Date((curDateTime.getTime())+30000);
                		curSession.setCurTime(curDateTime);
                		curSession.setExpireTime(expireDateTime);
                		sessionManagment.put(curSessionID, curSession);
            		}
            		Cookie newCookie = cookieGeneration(curSession.getSessionId(),curSession.getVersionNum());
            		req.setAttribute("Session", curSession);
            		res.addCookie(newCookie);
            		req.getRequestDispatcher("/index.jsp").forward(req, res);
    			}
    			else{
    				if(requestAction.equals("logout")){
    					//logout
            			sessionManagment.remove(curSessionID);
            			res.sendRedirect("logout.html");
    				}
    			}
    		}    		
    	}
	}
    // decide whether session has expired or not
    public boolean timeout(Date expireTime){
		Calendar calobj = Calendar.getInstance();
		Date now = calobj.getTime();
		if(now.after(expireTime)) 	return true;
		else return false;
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
		Date expireDateTime1= new Date((curDateTime.getTime()+30000));
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
    	String output = ""+0+"_"+sessionId+"_"+versionNum+"_"+0+"_"+0;//+"/"+message+"/"+dt.toString()+"/"+expireDt.toString();
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
		System.out.println("request recieved, redirect to doGet method");
		doGet(request, response);
	}

}
