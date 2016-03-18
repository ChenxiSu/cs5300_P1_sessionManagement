package project1a;
import java.util.Date;

public class session {
	private int sessionId;
	private int versionNum;
	private String message;
	private Date curTime;
	private Date expireTime;
	public session(){
		message="";
	}
	public int getSessionId(){
		return sessionId;
	}
	public void setSessionId(int id){
		this.sessionId = id;
	}
	public int getVersionNum(){
		return versionNum;
	}
	public void setVersionNum(int num){
		this.versionNum = num;
	}
	public String getMessage(){
		return message;
	}
	public void setMessage(String str){
		this.message = str;
	}
	public Date getCurTime(){
		return curTime;
	}
	public void setCurTime(Date time){
		this.curTime = time;
	}
	public Date getExpireTime(){
		return expireTime;
	}
	public void setExpireTime(Date eTime){
		this.expireTime = eTime;
	}
}
