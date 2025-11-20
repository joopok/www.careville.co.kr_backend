package kr.co.cleaning.ctrl;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.config.SessionCmn;

@Controller
public class HomeCtrl {

	private static final Logger log	= LoggerFactory.getLogger(HomeCtrl.class);

	@Autowired
	SessionCmn sessionCmn;

	@GetMapping(value = {"/apage/home.do"})
	public String signIn(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		return "redirect:/apage/cnslt010.do";
//		return "apage/home";
	}

}
