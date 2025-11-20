package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.cleaning.core.config.KFException;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.mapper.MngerMapper;

@Service
public class MngerSvc{

	private final static Logger log	= LoggerFactory.getLogger(MngerSvc.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	MngerMapper mapper;

	@Autowired
	CmmnSvc cmmnSvc;

	@Autowired
	PageUtil pageUtil;

	public HashMap<String,Object> getMngerList(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		pageUtil.setViewRowCnt(10);
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getMngerCnt(paramMap));

		returnMap.put("rowNum"			,pageUtil.getRowNum());
		returnMap.put("pagination"		,pageUtil.getPaging("json"));				// pagination
		returnMap.put("list"			,mapper.getMngerList(paramMap));			// 목록
		returnMap.put("sesionMgnrSeq"	,SUtils.nvl(sessionMap.get("mngrSeq")));

		return returnMap;
	}

	public HashMap<String,Object> getMngerView(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		returnMap.put("view"	,mapper.getMngerView(paramMap));	// 상세

		return returnMap;
	}

	public HashMap<String,Object> setMngerUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		String sessionMngrSeq			= SUtils.nvl(sessionMap.get("mngrSeq"));
		String mngrSeq					= SUtils.nvl(paramMap.get("mngrSeq"));
		String mngrPw					= SUtils.nvl(paramMap.get("mngrPw"));
		HashMap<String,Object> rsMap	= mapper.getMngerView(paramMap);
		String dbMngrPw					= SUtils.nvl(rsMap.get("mngrPw"));
		boolean matched					= BCrypt.checkpw(mngrPw, dbMngrPw);

		if(!sessionMngrSeq.equals("1") && mngrSeq.equals("1")){
			throw new KFException("\"superadmin\"계정은 \"superadmin\"계정으로 접속 후 변경 가능합니다.");
		}

		if(!matched){
			throw new KFException("비밀번호를 정확히 입력해 주세요.");
		}

		paramMap.put("sessionMngrSeq", sessionMngrSeq);

		if(mapper.setMngerUpd(paramMap) == 0) {
			throw new KFException("변경 되지 않았습니다.");
		}

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	public HashMap<String,Object> setMngrSttusUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();
		String sessionMngrSeq				= SUtils.nvl(sessionMap.get("mngrSeq"));
		String mngrSeq						= SUtils.nvl(paramMap.get("mngrSeq"));

		if(!sessionMngrSeq.equals("1")){
			throw new KFException("상태 변경 권한이 없습니다.");
		}

		if(mngrSeq.equals("1")){
			throw new KFException("슈퍼관리자는 변경할수 없습니다.");
		}

		if(mapper.setMngrSttusUpd(paramMap) == 0){
			throw new KFException("수정되지 않았습니다.");
		}

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	public HashMap<String,Object> setMngrPwUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		String sessionMngrSeq			= SUtils.nvl(sessionMap.get("mngrSeq"));
		String mngrSeq					= SUtils.nvl(paramMap.get("mngrSeq"));
		String mngrPw					= SUtils.nvl(paramMap.get("mngrPw"));
		String mngrPwUpdt				= SUtils.nvl(paramMap.get("mngrPwUpdt"));
		String encMngrPwUpdt			= BCrypt.hashpw(mngrPwUpdt, BCrypt.gensalt());

		HashMap<String,Object> rsMap	= mapper.getMngerView(paramMap);
		String dbMngrPw					= SUtils.nvl(rsMap.get("mngrPw"));
		boolean matched					= BCrypt.checkpw(mngrPw, dbMngrPw);

		if(!sessionMngrSeq.equals("1") && !sessionMngrSeq.equals(mngrSeq)){
			throw new KFException("비밀번호 변경 권한이 없습니다.");
		}

		if(!sessionMngrSeq.equals("1") && mngrSeq.equals("1")){
			throw new KFException("\"superadmin\"계정은 \"superadmin\"계정으로 접속 후 변경 가능합니다.");
		}

		if(!sessionMngrSeq.equals("1") && !matched){
			throw new KFException("비밀번호를 정확히 입력해 주세요.");
		}

		paramMap.put("encMngrPw",encMngrPwUpdt);

		if(mapper.setMngrPwUpd(paramMap) == 0){
			throw new KFException("수정되지 않았습니다.");
		}

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	public HashMap<String,Object> setMngrDel(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

		String sessionMngrSeq			= SUtils.nvl(sessionMap.get("mngrSeq"));
		String mngrSeq					= SUtils.nvl(paramMap.get("mngrSeq"));
		String mngrPw					= SUtils.nvl(paramMap.get("mngrPw"));

		HashMap<String,Object> rsMap	= mapper.getMngerView(paramMap);
		String dbMngrPw					= SUtils.nvl(rsMap.get("mngrPw"));
		boolean matched					= BCrypt.checkpw(mngrPw, dbMngrPw);

		if(!sessionMngrSeq.equals("1")){
			throw new KFException("삭제 권한이 없습니다.");
		}

		if(mngrSeq.equals("1")){
			throw new KFException("\"superadmin\"계정은 삭제 할 수 없습니다.");
		}

		if(!matched){
			throw new KFException("비밀번호를 정확히 입력해 주세요.");
		}

		if(mapper.setMngrDel(paramMap) == 0){
			throw new KFException("삭제되지 않았습니다.");
		}

		returnMap.put("isDel", "Y");

		return returnMap;
	}

	public HashMap<String,Object> setMngerReg(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		log.info("paramMap :: {}",paramMap);

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		String mngrPw	= SUtils.nvl(paramMap.get("mngrPw"));
		String mngrTel	= SUtils.nvl(paramMap.get("mngrTel")).replace("-", "");
		String hashed	= BCrypt.hashpw(mngrPw, BCrypt.gensalt());

		paramMap.put("mngrTel"	,mngrTel);
		paramMap.put("mngrPw"	,hashed);

		if(mapper.getMngerIdDupChk(paramMap) > 0){
			throw new KFException("중복된 아이디가 있습니다.");
		}

		mapper.setMngerReg(paramMap);

		returnMap.put("isReg", "Y");

		return returnMap;
	}

}
