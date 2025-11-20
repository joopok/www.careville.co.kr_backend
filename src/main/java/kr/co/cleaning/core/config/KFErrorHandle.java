package kr.co.cleaning.core.config;

import java.util.Enumeration;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.cleaning.core.utils.SUtils;

@Controller
public class KFErrorHandle implements ErrorController {

	private final Logger log	= LoggerFactory.getLogger(getClass());

    @GetMapping("/error")
    public String errorHandle(HttpServletRequest req, ModelMap modelMap) {

    	log.error("┌─────────────────────────────────────────────────────────────┐");
    	log.error("│                      [ Error Handler ]                      │");
    	log.error("└─────────────────────────────────────────────────────────────┘");

        int statusCd		= SUtils.strToInt((req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
        String excpMsg		= SUtils.nvl(req.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        HttpStatus status	= HttpStatus.resolve(SUtils.strToInt(statusCd));
        String excpCdMsg	= status != null ?  status.getReasonPhrase() : "";

    	modelMap.addAttribute("isError"		,"true");
    	modelMap.addAttribute("excpCd"		,statusCd);
    	modelMap.addAttribute("excpCdMsg"	,excpCdMsg);
    	modelMap.addAttribute("excpMsg"		,excpMsg);

		log.error("───── excpCd     : {}",statusCd);
		log.error("───── excpCdMsg  : {}",excpCdMsg);
		log.error("───── excpMsg    : {}",excpMsg);


    	Enumeration<String> em 				= req.getHeaderNames();
    	HashMap<String,String> headerMap	= new HashMap<>();
    	String headerNm						= "";
    	String pageView						= "";

    	while(em.hasMoreElements()){
    		headerNm	=  em.nextElement();
    		headerMap.put(headerNm, req.getHeader(headerNm));
    	}

    	log.error("───── isAjax   : {}",headerMap.containsKey("x-kframe-ajax-call"));
    	log.error("───── isJson   : {}",SUtils.nvl(headerMap.get("accept")).contains("application/json"));

    	if(SUtils.nvl(headerMap.get("accept")).contains("application/json")){
    		pageView	= "jsonView";

    	}else{
    		pageView		= "error/defaultError";;
    		String referer	= SUtils.nvl(headerMap.get("referer"),"/apage/");

//    		if(excpCd == 901){
//    			referer	= "/apage/";
//    		}

    		modelMap.put("backUrl"	,referer);
    	}

        return pageView;
    }
}