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
import kr.co.cleaning.mapper.BoardMapper;

@Service
public class BoardSvc{

	private final static Logger log	= LoggerFactory.getLogger(BoardSvc.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	BoardMapper mapper;

	@Autowired
	CmmnSvc cmmnSvc;

	@Autowired
	PageUtil pageUtil;

	public HashMap<String,Object> getBoardList(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("boardGb"		,"CASE");	// 게시판구분(공지,FAQ,포토...)
		paramMap.put("fileTrgetSe"	,"BOARD");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		pageUtil.setViewRowCnt(12);

		log.info("paramMap :: {}",paramMap);

		// pagination setting
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getBoardCnt(paramMap));

		returnMap.put("rowNum"		,pageUtil.getRowNum());
		returnMap.put("pagination"	,pageUtil.getPaging("json"));		// pagination
		returnMap.put("list"		,mapper.getBoardList(paramMap));	// 목록

		return returnMap;
	}

	public HashMap<String,Object> getBoardView(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe"	,"BOARD");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		paramMap.put("fileTrgetSeq"	,paramMap.get("boardSeq"));

		List<HashMap<String,Object>> files	= cmmnSvc.getFileList(paramMap);

		for(HashMap<String,Object> a : files){
			a.put("viewFileSeq", AESUtil.urlEnc(SUtils.nvl(a.get("fileSeq"))));
		}

		log.info("files : {}",files);

		returnMap.put("view"			,mapper.getBoardView(paramMap));	// 상세
		returnMap.put("filesLst"		,files);							// 파일리스트
		returnMap.put("serviceCdLst"	,cmmnSvc.getServiceCdList());		// 서비스내용 코드
		returnMap.put("sttusCdLst"		,cmmnSvc.getCodeList("002"));		// 상담진행상태 코드

		return returnMap;
	}

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String,Object> setBoardUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		Map<String, Object> sessionMap = sessionCmn.getLogonInfo();
		paramMap.put("answerId", sessionMap != null ? SUtils.nvl(sessionMap.get("mngrId")) : "admin");

		int boardCnt	= mapper.setBoardUpd(paramMap);

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setBoardReg(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe", "BOARD");	// 대상 구분(CNSLT:상담,BOARD:게시판)

		int boardCnt	= mapper.setBoardReg(paramMap);

		paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("BOARD_SEQ")));

		int isnertCnt	= cmmnSvc.setFileRelationInsert(paramMap);

		returnMap.put("isReg", "Y");

		return returnMap;
	}

}
