package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.mapper.CaseMapper;

@Service
public class CaseSvc{

	private final static Logger log	= LoggerFactory.getLogger(CaseSvc.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	CaseMapper mapper;

	@Autowired
	CmmnSvc cmmnSvc;

	@Autowired
	PageUtil pageUtil;

	public HashMap<String,Object> getCaseList(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		HashMap<String,Object> beforeParam	= (HashMap<String, Object>) paramMap.clone();

		paramMap.put("fileTrgetSe"	,"CASE");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		pageUtil.setViewRowCnt(12);

		// pagination setting
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getCaseCnt(paramMap));

		List<HashMap<String,Object>> list	= mapper.getCaseList(paramMap);

		for(HashMap<String,Object> a : list){
			a.put("viewFileSeq", AESUtil.urlEnc(SUtils.nvl(a.get("fileSeq"))));
		}

		returnMap.put("rowNum"			,pageUtil.getRowNum());
		returnMap.put("pagination"		,pageUtil.getPaging("json"));		// pagination
		returnMap.put("list"			,list);		// 목록
		returnMap.put("svcCdList"		,cmmnSvc.getServiceCdList()); 		// 서비스내용 코드
		returnMap.put("caseSvcCdList"	,mapper.getCaseServiceList()); 		// 등록되어있는 서비스내용 코드

		return returnMap;
	}

	public HashMap<String,Object> getCaseView(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		HashMap<String,Object> view			= mapper.getCaseView(paramMap);
		view.put("viewFileSeq"	,AESUtil.urlEnc(SUtils.nvl(view.get("fileSeq"))));

		HashMap<String,Object> fileView		= cmmnSvc.getFileView(view);
		view.put("fileName"		,SUtils.nvl(fileView.get("fileName")));

		returnMap.put("view"		,view);
		returnMap.put("svcCdList"	,cmmnSvc.getServiceCdList()); 		// 서비스내용 코드

		return returnMap;
	}

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String,Object> setCaseUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		paramMap.put("fileTrgetSe"	,"CASE");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		paramMap.put("regNm"		,SUtils.nvl(sessionMap.get("mngrNcnm")));
		paramMap.put("fileSeq"		,AESUtil.urlDec(SUtils.nvl(paramMap.get("mainImg"))));
		paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("caseSeq")));

		int caseCnt		= mapper.setCaseUpd(paramMap);
		int isnertCnt	= cmmnSvc.setFileRelationInsert(paramMap);

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String,Object> setCaseReg(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		paramMap.put("fileTrgetSe"	,"CASE");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		paramMap.put("regNm"		,SUtils.nvl(sessionMap.get("mngrNcnm")));
		paramMap.put("fileSeq"		,AESUtil.urlDec(SUtils.nvl(paramMap.get("mainImg"))));

		int caseCnt		= mapper.setCaseReg(paramMap);

		paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("CNSLT_SEQ")));

		int isnertCnt	= cmmnSvc.setFileRelationInsert(paramMap);

		returnMap.put("isReg", "Y");

		return returnMap;
	}

	public HashMap<String,Object> setCaseDel(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		int caseCnt		= mapper.setCaseDel(paramMap);

		returnMap.put("isDel", "Y");

		return returnMap;
	}

}
