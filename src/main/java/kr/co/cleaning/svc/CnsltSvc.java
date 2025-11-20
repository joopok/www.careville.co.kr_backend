package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.List;

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
import kr.co.cleaning.mapper.CnsltMapper;

@Service
public class CnsltSvc{

	private final static Logger log	= LoggerFactory.getLogger(CnsltSvc.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	CnsltMapper mapper;

	@Autowired
	CmmnSvc cmmnSvc;

	@Autowired
	PageUtil pageUtil;

	public HashMap<String,Object> getCnsltList(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe", "CNSLT");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		pageUtil.setViewRowCnt(10);

		// pagination setting
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getCnsltCnt(paramMap));

		returnMap.put("rowNum"		,pageUtil.getRowNum());
		returnMap.put("pagination"	,pageUtil.getPaging("json"));		// pagination
		returnMap.put("list"		,mapper.getCnsltList(paramMap));	// 목록

		return returnMap;
	}

	public HashMap<String,Object> getCnsltView(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe"	,"CNSLT");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		paramMap.put("fileTrgetSeq"	,paramMap.get("cnsltSeq"));

		List<HashMap<String,Object>> files	= cmmnSvc.getFileList(paramMap);

		for(HashMap<String,Object> a : files){
			a.put("viewFileSeq", AESUtil.urlEnc(SUtils.nvl(a.get("fileSeq"))));
		}

		returnMap.put("view"			,mapper.getCnsltView(paramMap));	// 상세
		returnMap.put("filesLst"		,files);							// 파일리스트
		returnMap.put("serviceCdLst"	,cmmnSvc.getServiceCdList());		// 서비스내용 코드
		returnMap.put("sttusCdLst"		,cmmnSvc.getCodeList("002"));		// 상담진행상태 코드

		return returnMap;
	}

	public HashMap<String,Object> setCnsltUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("answerId", sessionCmn.get("mngrId"));

		int cnsltCnt	= mapper.setCnsltUpd(paramMap);

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setCnsltReg(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe", "CNSLT");	// 대상 구분(CNSLT:상담,BOARD:게시판)

		int cnsltCnt	= mapper.setCnsltReg(paramMap);

		paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("CNSLT_SEQ")));

		int isnertCnt	= cmmnSvc.setFileRelationInsert(paramMap);

		returnMap.put("isReg", "Y");

		return returnMap;
	}

}
