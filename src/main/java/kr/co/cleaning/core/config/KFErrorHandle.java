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

        int statusCd = SUtils.strToInt(req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        String requestUri = SUtils.nvl(req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        String excpMsg = SUtils.nvl(req.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        Throwable throwable = (Throwable) req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if ((excpMsg == null || excpMsg.isBlank()) && throwable != null) {
            excpMsg = SUtils.nvl(throwable.getMessage());
        }

        HttpStatus status = HttpStatus.resolve(statusCd);
        String excpCdMsg = status != null ? status.getReasonPhrase() : "";

        modelMap.addAttribute("isError", "true");
        modelMap.addAttribute("excpCd", statusCd);
        modelMap.addAttribute("excpCdMsg", excpCdMsg);
        modelMap.addAttribute("excpMsg", excpMsg);
        modelMap.addAttribute("requestUri", requestUri);

        // Reduce noisy logs for common 404s (sourcemaps, devtools probe)
        boolean isNoisy404 = (statusCd == 404) && (
            requestUri.endsWith(".map") ||
            requestUri.startsWith("/.well-known/appspecific/")
        );

        if (statusCd == 404) {
            if (isNoisy404) {
                log.debug("404 Not Found (suppressed): uri={}, msg={}", requestUri, excpMsg);
            } else {
                log.warn("404 Not Found: uri={}, msg={}", requestUri, excpMsg);
            }
        } else if (statusCd >= 500) {
            log.error("┌─────────────────────────────────────────────────────────────┐");
            log.error("│                      [ Error Handler ]                      │");
            log.error("└─────────────────────────────────────────────────────────────┘");
            log.error("───── excpCd     : {}", statusCd);
            log.error("───── excpCdMsg  : {}", excpCdMsg);
            log.error("───── requestUri : {}", requestUri);
            log.error("───── excpMsg    : {}", excpMsg);
            if (throwable != null) {
                log.error("───── exception  :", throwable);
            }
        }

        Enumeration<String> em = req.getHeaderNames();
        HashMap<String, String> headerMap = new HashMap<>();
        String pageView = "";

        while (em != null && em.hasMoreElements()) {
            String headerNm = em.nextElement();
            headerMap.put(headerNm, req.getHeader(headerNm));
        }

        boolean isJson = SUtils.nvl(headerMap.get("accept")).contains("application/json");
        if (statusCd >= 500) {
            log.error("───── isAjax   : {}", headerMap.containsKey("x-kframe-ajax-call"));
            log.error("───── isJson   : {}", isJson);
        } else if (statusCd == 404 && !isNoisy404) {
            log.warn("───── isAjax   : {}", headerMap.containsKey("x-kframe-ajax-call"));
            log.warn("───── isJson   : {}", isJson);
        }

        if (isJson) {
            pageView = "jsonView";
        } else if (statusCd == 404) {
            // Dedicated 404 page when available
            pageView = "error/404";
        } else {
            pageView = "error/defaultError";
            String referer = SUtils.nvl(headerMap.get("referer"), "/apage/");
            modelMap.put("backUrl", referer);
        }

        return pageView;
    }
}
