package kr.co.cleaning.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

public class SessionCmn {

    private final HttpSession session;

    @Value("${kframe.session-id}")
    private String SESSION_KEY;

    @Value("${kframe.session-log-in-key}")
    private String LOGON_KEY;

    public SessionCmn(HttpSession session) {
        this.session	= session;
    }

    @PostConstruct
    public void init() {
        if(session.getAttribute(SESSION_KEY) == null){
            session.setAttribute(SESSION_KEY, new ConcurrentHashMap<String, Object>());
        }
    }

    @SuppressWarnings("unchecked")
	private Map<String, Object> getSessionMap() {
        return (Map<String, Object>) session.getAttribute(SESSION_KEY);
    }

    /**
     * 공통 세션관리
     * */
    public void set(String key, Object value) {
        Map<String, Object> sessionMap	= getSessionMap();
        sessionMap.put(key, value);
    }

    public Object get(String key) {
    	Map<String, Object> sessionMap	= getSessionMap();
    	Object value 					= sessionMap.get(key);
    	return (value != null) ? value : null;
    }

    public void remove(String key) {
        Map<String, Object> sessionMap	= getSessionMap();
        sessionMap.remove(key);
    }

    public void invalidate() {
        session.invalidate();
    }


    /**
     * 로그온 세션관리
     * */
    public void setLogon(HashMap<String,Object> paramMap) {
        Map<String, Object> sessionMap	= getSessionMap();
        sessionMap.put(LOGON_KEY, paramMap);
    }

    public Map<String, Object> getLogonInfo() {
    	Map<String, Object> sessionMap	= getSessionMap();
    	Map<String, Object> value 		= (Map<String, Object>) sessionMap.get(LOGON_KEY);
    	return (value != null) ? value : null;
    }

    public void invalLogon() {
        Map<String, Object> sessionMap	= getSessionMap();
        sessionMap.remove(LOGON_KEY);
    }

    public boolean isLogon(){
    	return getLogonInfo() == null ? false : true;
    }
}
