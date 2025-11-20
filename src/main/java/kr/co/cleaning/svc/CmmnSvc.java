package kr.co.cleaning.svc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.cleaning.core.config.KFException;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.core.utils.FileUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.mapper.CmmnMapper;

@Service
public class CmmnSvc{

	private final static Logger log	= LoggerFactory.getLogger(CmmnSvc.class);

	@Autowired
	CmmnMapper mapper;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	SessionCmn sessionCmn;

	public List<HashMap<String,Object>> getCodeList(String groupCd) throws Exception {
		return mapper.getCodeList(groupCd);
	}

	public List<HashMap<String,Object>> getServiceCdList() throws Exception {
		return mapper.getServiceCdList();
	}

	public List<HashMap<String,Object>> getProductCdList() throws Exception {
		return mapper.getProductCdList();
	}

	public List<HashMap<String,Object>> getFileList(HashMap<String,Object> paramMap) throws Exception {
		return mapper.getFileList(paramMap);
	}

	public HashMap<String,Object> setFileUpload(HashMap<String,Object> paramMap,List<MultipartFile> files) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		List<String> setFileSeq				= new ArrayList<>();
		List<HashMap<String,Object>> fLst	= fileUtil.fileUpload(files,false);

		if(fLst == null || fLst.size() <= 0) throw new KFException("파일을 업로드 하지 못 했습니다.");

		if(fLst.size() > 1){
			for(HashMap<String,Object> m : fLst){
				mapper.setFileInsert(m);
				setFileSeq.add(AESUtil.urlEnc(SUtils.nvl(m.get("FILE_SEQ"))));
			}
			returnMap.put("fileSeq", setFileSeq);

		}else{
			HashMap<String,Object> m = fLst.get(0);
			mapper.setFileInsert(m);
			returnMap.put("fileSeq", AESUtil.urlEnc(SUtils.nvl(m.get("FILE_SEQ"))));
		}

		return returnMap;
	}

	public HashMap<String,Object> getFileView(HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileSeq", AESUtil.urlDec(SUtils.nvl(paramMap.get("viewFileSeq"))));

		HashMap<String,Object> fileMap		= mapper.getFileView(paramMap);

		if(fileMap == null) throw new KFException("파일을 찾을 수 없습니다.");

		returnMap.put("filePath", SUtils.nvl(fileMap.get("fileFullPath")));
		returnMap.put("fileName", SUtils.nvl(fileMap.get("fileRealNm")));

		return returnMap;
	}

	public HashMap<String,Object> setFileDel(HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileSeq", AESUtil.urlDec(SUtils.nvl(paramMap.get("viewFileSeq"))));

		HashMap<String,Object> fileMap		= mapper.getFileView(paramMap);
		String path							= SUtils.nvl(fileMap.get("fileFullPath"));

		if(fileMap.size() > 0){
			mapper.setFileDel(paramMap);
			fileUtil.deleteFile(new File(path));
			returnMap.put("isDel"	,true);

		}else{
			returnMap.put("isDel"	,false);
		}

		return returnMap;
	}

	public int setFileRelationInsert(HashMap<String,Object> paramMap) throws Exception {

		String fileArr					= SUtils.nvl(paramMap.get("fileArr"));
		String fileSeq					= SUtils.nvl(paramMap.get("fileSeq"));
		String fileArry[]				= fileArr.equals("") ? fileSeq.split(",") : fileArr.split(",");
		String fileTrgetSe				= SUtils.nvl(paramMap.get("fileTrgetSe"));
		int fileTrgetSeq				= SUtils.strToInt(paramMap.get("fileTrgetSeq"));
		int cnt							= 0;
		HashMap<String,Object> forMap	= null;

		if(fileArry != null && !fileArry[0].equals("")) {
			for(String s: fileArry){
				forMap	= new HashMap<>();
				forMap.put("fileTrgetSe"	,fileTrgetSe);
				forMap.put("fileTrgetSeq"	,fileTrgetSeq);
				forMap.put("fileSeq"		,s);

				cnt	+= mapper.setFileRelationInsert(forMap);
			}
		}

		return cnt;
	}

	public int setFileRelationUpdate(HashMap<String,Object> paramMap) throws Exception {

		String fileArr					= SUtils.nvl(paramMap.get("fileArr"));
		String fileSeq					= SUtils.nvl(paramMap.get("fileSeq"));
		String fileArry[]				= fileArr.equals("") ? fileSeq.split(",") : fileArr.split(",");
		String fileTrgetSe				= SUtils.nvl(paramMap.get("fileTrgetSe"));
		int fileTrgetSeq				= SUtils.strToInt(paramMap.get("fileTrgetSeq"));
		int cnt							= 0;
		HashMap<String,Object> forMap	= null;

		if(fileArry != null && !fileArry[0].equals("")) {
			for(String s: fileArry){
				forMap	= new HashMap<>();
				forMap.put("fileTrgetSe"	,fileTrgetSe);
				forMap.put("fileTrgetSeq"	,fileTrgetSeq);
				forMap.put("fileSeq"		,s);

				cnt	+= mapper.setFileRelationUpdate(forMap);
			}
		}

		return cnt;
	}

	public HashMap<String,Object> setFileRelationDel(HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap		= new HashMap<String, Object>();
		List<HashMap<String,Object>> delList	= mapper.getFileRelationList(paramMap);

		mapper.setFileRelationDelFirst(paramMap);
		mapper.setFileRelationDelSecond(paramMap);

		for(HashMap<String,Object> m : delList){
			fileUtil.deleteFile(new File(SUtils.nvl(m.get("fileFullPath"))));
		}

		returnMap.put("isDel"	,(delList.size() > 0 ? true : false));

		return returnMap;
	}

	public HashMap<String,Object> getSignIn(HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		String mngrPw						= SUtils.nvl(paramMap.get("mngrPw"));
		String encMngrPw					= BCrypt.hashpw(mngrPw, BCrypt.gensalt());
		HashMap<String,Object> rsMap		= mapper.getSignIn(paramMap);

		if(rsMap != null && rsMap.size() > 0) {
			sessionCmn.setLogon(rsMap);

		}else{
			sessionCmn.invalLogon();
			throw new KFException("아이디 정보가 없습니다.",997);
		}

		String dbMngrPw		= SUtils.nvl(rsMap.get("mngrPw"));
		boolean matched		= BCrypt.checkpw(mngrPw, dbMngrPw);

		if(!matched){
			sessionCmn.invalLogon();
			throw new KFException("비밀번호를 정확히 입력해 주세요.",997);
		}


		returnMap.put("isSignIn" ,matched);

		return returnMap;
	}


	public HashMap<String,Object> setEditorImgUploader(List<MultipartFile> files) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		List<HashMap<String,Object>> fLst	= fileUtil.fileUpload(files,true);

		if(fLst == null || fLst.size() <= 0) throw new KFException("파일을 업로드 하지 못 했습니다.");

		List<HashMap<String,String>> rsLst	= new ArrayList<>();
		HashMap<String,String> rsMap		= new HashMap<>();

		for(HashMap<String,Object> m : fLst){

			rsMap		= new HashMap<>();
			rsMap.put("bNewLine"	,"true");
			rsMap.put("sFileName"	,SUtils.nvl(m.get("fileRealNm")));
			rsMap.put("sFileURL"	,"/editorFileView.do?a="+SUtils.nvl(m.get("filePathEdit"))+"&b="+SUtils.nvl(m.get("fileFakeNm")));
			rsLst.add(rsMap);
		}

		returnMap.put("files", rsLst);

		return returnMap;
	}

	public HashMap<String,Object> getEditorFileView(HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		StringBuffer str					= new StringBuffer();
		str.append(fileUtil.getFilePath());
		str.append("/editor/");
		str.append(SUtils.nvl(paramMap.get("a")));
		str.append("/");
		str.append(SUtils.nvl(paramMap.get("b")));

		returnMap.put("filePath", str.toString());
		returnMap.put("fileName", SUtils.nvl("aa"));

		return returnMap;
	}


}
