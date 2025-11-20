package kr.co.cleaning.ctrl;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.svc.CmmnSvc;
import kr.co.cleaning.svc.CnsltSvc;

@Controller
public class CnsltCtrl {

	private static final Logger log	= LoggerFactory.getLogger(CnsltCtrl.class);

	@Autowired
	CnsltSvc svc;

	@Autowired
	CmmnSvc CmmnSvc;

	@RequestMapping("/apage/cnslt0{pageNum}.do")
	public String pageView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, @PathVariable String pageNum ,ModelMap modelMap) throws Exception {
		/*
		 * 10 : list
		 * 20 : view
		 * 30 : register
		 * 40 : modify
		 * */

		String pageNumber	= pageNum;

		if(pageNum.equals("10")){
			modelMap.addAttribute("svcCdList", CmmnSvc.getServiceCdList());

		}else if(pageNum.equals("11")){
			modelMap.addAllAttributes(svc.getCnsltList(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("21")){
			modelMap.addAllAttributes(svc.getCnsltView(req,paramMap));
			pageNumber	= "20";

		}else if(pageNum.equals("41")){
			modelMap.addAllAttributes(svc.setCnsltUpd(req,paramMap));
			return "jsonView";
		}

        StringBuilder re = new StringBuilder();
        re.append("apage/cnslt/cnslt0");
        re.append(pageNumber);

		return re.toString();
	}

	@PostMapping("/cnsltList.do")
	public String cnsltList(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

        modelMap.addAllAttributes(svc.getCnsltList(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/cnsltView.do")
	public String cnsltView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getCnsltView(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/cnsltUpd.do")
	public String cnsltUpd(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setCnsltUpd(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/cnsltReg.do")
	public String cnsltReg(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setCnsltReg(req,paramMap));

		return "jsonView";
	}


}
