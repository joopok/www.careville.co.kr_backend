package kr.co.cleaning.ctrl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.svc.CmmnSvc;
import kr.co.cleaning.svc.MngerSvc;

@Controller
public class MngerCtrl {

	private static final Logger log	= LoggerFactory.getLogger(MngerCtrl.class);

	@Autowired
	MngerSvc svc;

	@Autowired
	CmmnSvc CmmnSvc;

	@Autowired
	SessionCmn sessionCmn;

	@RequestMapping("/apage/mnger0{pageNum}.do")
	public String pageView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, @PathVariable String pageNum ,ModelMap modelMap) throws Exception {
		/*
		 * 10 : list
		 * 20 : view
		 * 30 : register
		 * 40 : modify
		 * 50 : delete
		 * 60 : password update
		 * */

		String pageNumber	= pageNum;
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		// 방어적 null 체크 - AOP에서 체크하지만 안전성을 위해 추가
		if (sessionMap == null) {
			sessionMap = new HashMap<>();
		}

		if(pageNum.equals("10")){
			modelMap.addAttribute("ssa", (SUtils.nvl(sessionMap.get("mngrSeq")).equals("1") ? "Y" : "N"));

		}else if(pageNum.equals("11")){
			modelMap.addAllAttributes(svc.getMngerList(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("20") || pageNum.equals("21")){
			modelMap.addAllAttributes(svc.getMngerView(req,paramMap));
			modelMap.addAttribute("ssa", (SUtils.nvl(sessionMap.get("mngrSeq")).equals("1") ? "Y" : "N"));
			String currentMngrSeq = SUtils.nvl(sessionMap.get("mngrSeq"));
			String targetMngrSeq = SUtils.nvl(paramMap.get("mngrSeq"));
			modelMap.addAttribute("canChangePw", currentMngrSeq.equals("1") || currentMngrSeq.equals(targetMngrSeq));
			pageNumber	= "20";

		}else if(pageNum.equals("31")){
			pageNumber	= "30";

		}else if(pageNum.equals("32")){
			modelMap.addAllAttributes(svc.setMngerReg(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("40")){
			modelMap.addAllAttributes(svc.getMngerView(req,paramMap));

		}else if(pageNum.equals("41")){	// 전체 변경
			modelMap.addAllAttributes(svc.setMngerUpd(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("42")){ // 상태 변경
			modelMap.addAllAttributes(svc.setMngrSttusUpd(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("43")){ // 비밀번호 변경 view
			// DB에서 관리자 정보 조회
			modelMap.addAllAttributes(svc.getMngerView(req, paramMap));
			modelMap.addAttribute("ssa",(SUtils.nvl(sessionMap.get("mngrSeq")).equals("1") ? "Y" : "N"));
			// 현재 로그인 사용자가 본인이거나 슈퍼관리자인 경우만 비밀번호 변경 가능
			String currentMngrSeq = SUtils.nvl(sessionMap.get("mngrSeq"));
			String targetMngrSeq = SUtils.nvl(paramMap.get("mngrSeq"));
			modelMap.addAttribute("canChangePw", currentMngrSeq.equals("1") || currentMngrSeq.equals(targetMngrSeq));

		}else if(pageNum.equals("44")){ // 비밀번호 변경
			modelMap.addAllAttributes(svc.setMngrPwUpd(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("51")){ // 삭제 view
			paramMap.put("ssa",(SUtils.nvl(sessionMap.get("mngrSeq")).equals("1") ? "Y" : "N"));
			modelMap.addAttribute("view", paramMap);

		}else if(pageNum.equals("52")){ // 삭제
			modelMap.addAllAttributes(svc.setMngrDel(req,paramMap));
			return "jsonView";

		}

        StringBuilder re = new StringBuilder();
        re.append("apage/mnger/mnger0");
        re.append(pageNumber);

		return re.toString();
	}

}
