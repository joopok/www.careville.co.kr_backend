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
import kr.co.cleaning.svc.CaseSvc;
import kr.co.cleaning.svc.CmmnSvc;

@Controller
public class CaseCtrl {

	private static final Logger log	= LoggerFactory.getLogger(CaseCtrl.class);

	@Autowired
	CaseSvc svc;

	@Autowired
	CmmnSvc CmmnSvc;

	@RequestMapping("/apage/case0{pageNum}.do")
	public String pageView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, @PathVariable String pageNum ,ModelMap modelMap) throws Exception {
		/*
		 * 10 : list
		 * 20 : view
		 * 30 : register
		 * 40 : modify
		 * 50 : delete
		 * */
		String pageNumber	= pageNum;

		modelMap.addAttribute("searchMap", paramMap.clone());

		if(pageNum.equals("10")){
			modelMap.addAllAttributes(svc.getCaseList(req,paramMap));

		}else if(pageNum.equals("20")){
			modelMap.addAllAttributes(svc.getCaseView(req,paramMap));

		}else if(pageNum.equals("30")){
			modelMap.addAttribute("svcCdList", CmmnSvc.getServiceCdList());

		}else if(pageNum.equals("31")){
			modelMap.addAllAttributes(svc.setCaseReg(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("40")){
			modelMap.addAllAttributes(svc.getCaseView(req,paramMap));

		}else if(pageNum.equals("41")){
			modelMap.addAllAttributes(svc.setCaseUpd(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("51")){
			modelMap.addAllAttributes(svc.setCaseDel(req,paramMap));
			return "jsonView";
		}

		StringBuilder re	= new StringBuilder();
		re.append("apage/case/case0");
        re.append(pageNumber);

		return re.toString();
	}

	@PostMapping("/caseList.do")
	public String caseList(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getCaseList(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/caseView.do")
	public String caseView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getCaseView(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/caseUpd.do")
	public String caseUpd(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setCaseUpd(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/caseReg.do")
	public String caseReg(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setCaseReg(req,paramMap));

		return "jsonView";
	}


}
