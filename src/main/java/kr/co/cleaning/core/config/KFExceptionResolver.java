package kr.co.cleaning.core.config;

import java.util.Enumeration;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.utils.SUtils;

public class KFExceptionResolver extends SimpleMappingExceptionResolver {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		log.error("┌─────────────────────────────────────────────────────────────┐");
		log.error("│                    [ Exception Resolve ]                    │");
		log.error("└─────────────────────────────────────────────────────────────┘");

		ModelAndView mv = new ModelAndView();
		HashMap<String, Object> excpMap = new HashMap<>();

		String viewNm = determineViewName(ex, request);
		int excpCd = determineStatusCode(request, viewNm);
		String excpMsg = ex.getMessage();
		String excpPath = SUtils.nvl(handler).replaceAll("#.*$", "");
		String excpMethod = ex.getClass().getSimpleName();
		String excpCdMsg = "";

		if (ex instanceof DataAccessException) {
			String sqlState = null;

			if (ex.getCause() instanceof java.sql.SQLException) {
				java.sql.SQLException sqlEx = (java.sql.SQLException) ex.getCause();
				sqlState = sqlEx.getSQLState();
			}

			SqlErrorCode codeFromState = SqlErrorCode.fromSqlState(sqlState);
			excpCd = sqlState == null ? 99999 : SUtils.strToInt(sqlState);
			excpCdMsg = codeFromState.name();
			excpMsg = codeFromState.name() + " - " + codeFromState.getMessage();

		} else {
			if (ex instanceof KFException) {
				excpCd = ((KFException) ex).getCode();
			}

			HttpStatus status = HttpStatus.resolve(excpCd);
			excpCdMsg = status == null || excpCd == 999 ? "User-specified error." : status.getReasonPhrase();
		}

		/*
		 * StringBuffer excpToStr = new StringBuffer();
		 * StackTraceElement[] stacks = ex.getStackTrace();
		 * excpToStr.append(ex.getMessage()+"<br>");
		 * for(StackTraceElement element : stacks){
		 * excpToStr.append(element+"<br>");
		 * }
		 * excpMap.put("excpToStr" ,excpToStr.toString());
		 */

		excpMap.put("isError", "true");
		excpMap.put("excpCd", excpCd);
		excpMap.put("excpCdMsg", excpCdMsg);
		excpMap.put("excpMsg", excpMsg);
		excpMap.put("excpPath", excpPath);
		excpMap.put("excpMethod", excpMethod);

		Enumeration<String> em = request.getHeaderNames();
		HashMap<String, String> headerMap = new HashMap<>();
		String headerNm = "";
		String pageView = "";

		while (em.hasMoreElements()) {
			headerNm = em.nextElement();
			headerMap.put(headerNm, request.getHeader(headerNm));
		}

		log.error("───── isAjax   : {}", headerMap.containsKey("x-kframe-ajax-call"));
		log.error("───── isJson   : {}", SUtils.nvl(headerMap.get("accept")).contains("application/json"));

		if (SUtils.nvl(headerMap.get("accept")).contains("application/json")) {
			pageView = "jsonView";

		} else {
			pageView = viewNm;
			String referer = SUtils.nvl(headerMap.get("referer"), "/apage/");

			if (excpCd == 901) {
				referer = "/apage/";
			}

			excpMap.put("backUrl", referer);
		}

		mv.setViewName(pageView);
		mv.addAllObjects(excpMap);

		log.error("───── pageView   : {}", pageView);
		log.error("───── excpCd     : {}", excpCd);
		log.error("───── excpCdMsg  : {}", excpCdMsg);
		log.error("───── excpMsg    : {}", excpMsg);
		;
		log.error("───── excpPath   : {}", excpPath);
		log.error("───── excpMethod : {}", excpMethod);

		// ex.printStackTrace();

		return mv;
	}
}
