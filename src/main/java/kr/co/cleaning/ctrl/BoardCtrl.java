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
import kr.co.cleaning.svc.BoardSvc;

@Controller
public class BoardCtrl {

	private static final Logger log	= LoggerFactory.getLogger(BoardCtrl.class);

	@Autowired
	BoardSvc svc;

	@RequestMapping("/apage/board0{pageNum}.do")
	public String pageView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, @PathVariable String pageNum ,ModelMap modelMap) throws Exception {

		/*
		 * 10 : list
		 * 20 : view
		 * 30 : register
		 * 40 : modify
		 * */

		String pageNumber	= pageNum;

		if(pageNum.equals("11")){
	        modelMap.addAllAttributes(svc.getBoardList(req,paramMap));
			return "jsonView";

		}else if(pageNum.equals("21")){

		}else if(pageNum.equals("41")){

		}else{
			pageNumber	= "10";
		}

        StringBuilder re = new StringBuilder();
        re.append("apage/board/board0");
        re.append(pageNumber);

		return re.toString();
	}

	@PostMapping("/boardList.do")
	public String boardList(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getBoardList(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/boardView.do")
	public String boardView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getBoardView(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/boardUpd.do")
	public String boardUpd(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setBoardUpd(req,paramMap));

		return "jsonView";
	}

	@PostMapping("/boardReg.do")
	public String boardReg(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setBoardReg(req,paramMap));

		return "jsonView";
	}


}
