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

		// viewFileSeq가 없으면 빈 결과 반환
		String viewFileSeq = SUtils.nvl(paramMap.get("viewFileSeq"));
		if (SUtils.isNvl(viewFileSeq)) {
			returnMap.put("filePath", "");
			returnMap.put("fileName", "");
			return returnMap;
		}

		String decryptedSeq = AESUtil.urlDec(viewFileSeq);

		// 복호화 실패 또는 빈 값이면 빈 결과 반환
		if (SUtils.isNvl(decryptedSeq)) {
			returnMap.put("filePath", "");
			returnMap.put("fileName", "");
			return returnMap;
		}

		paramMap.put("fileSeq", decryptedSeq);

		HashMap<String,Object> fileMap		= mapper.getFileView(paramMap);

		// 파일 정보가 없으면 빈 결과 반환 (예외 대신 graceful 처리)
		if(fileMap == null || fileMap.isEmpty()) {
			log.warn("파일 정보를 찾을 수 없습니다. fileSeq: {}", decryptedSeq);
			returnMap.put("filePath", "");
			returnMap.put("fileName", "");
			return returnMap;
		}

		returnMap.put("filePath", SUtils.nvl(fileMap.get("fileFullPath")));
		returnMap.put("fileName", SUtils.nvl(fileMap.get("fileRealNm")));

		return returnMap;
	}

    public HashMap<String,Object> setFileDel(HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		// viewFileSeq가 없으면 삭제 실패 반환
		String viewFileSeq = SUtils.nvl(paramMap.get("viewFileSeq"));
		if (SUtils.isNvl(viewFileSeq)) {
			returnMap.put("isDel", false);
			return returnMap;
		}

		paramMap.put("fileSeq", AESUtil.urlDec(viewFileSeq));

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

    public HashMap<String,Object> getFileThumbView(HashMap<String,Object> paramMap) throws Exception {

        HashMap<String,Object> returnMap = new HashMap<>();
        String viewSeqEnc = SUtils.nvl(paramMap.get("viewFileSeq"));

        // viewFileSeq가 없으면 빈 결과 반환 (예외 대신 graceful 처리)
        if (SUtils.isNvl(viewSeqEnc)) {
            returnMap.put("filePath", "");
            returnMap.put("fileName", "");
            return returnMap;
        }

        HashMap<String,Object> q = new HashMap<>();
        String decryptedSeq = AESUtil.urlDec(viewSeqEnc);

        // 복호화 실패 또는 빈 값이면 빈 결과 반환
        if (SUtils.isNvl(decryptedSeq)) {
            returnMap.put("filePath", "");
            returnMap.put("fileName", "");
            return returnMap;
        }

        q.put("fileSeq", decryptedSeq);

        HashMap<String,Object> fileMap = mapper.getFileView(q);

        // 파일 정보가 없으면 빈 결과 반환 (예외 대신 graceful 처리)
        if (fileMap == null || fileMap.isEmpty()) {
            log.warn("파일 정보를 찾을 수 없습니다. fileSeq: {}", decryptedSeq);
            returnMap.put("filePath", "");
            returnMap.put("fileName", "");
            return returnMap;
        }

        String basePath = SUtils.nvl(fileMap.get("filePath"));
        String fake = SUtils.nvl(fileMap.get("fileFakeNm"));
        int w = SUtils.strToInt(paramMap.get("w"), 480);

        String thumbFullPath = fileUtil.generateThumbnailIfNotExists(basePath, fake, w);

        returnMap.put("filePath", thumbFullPath);
        returnMap.put("fileName", SUtils.nvl(fileMap.get("fileRealNm")));
        return returnMap;
    }

    /**
     * 파일 SEQ로 직접 삭제 (레거시 데이터 정리용)
     * 관계 없이 단독으로 등록된 파일도 안전하게 삭제한다.
     */
    public void setFileDelBySeq(Integer fileSeq) throws Exception {
        if (fileSeq == null) return;

        HashMap<String,Object> q = new HashMap<>();
        q.put("fileSeq", fileSeq);

        HashMap<String,Object> fileMap = mapper.getFileView(q);
        if (fileMap == null || fileMap.isEmpty()) return;

        String path = SUtils.nvl(fileMap.get("fileFullPath"));
        mapper.setFileDel(q);
        fileUtil.deleteFile(new File(path));
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
                String toStore = s != null && s.matches("\\d+") ? s : AESUtil.urlDec(s);
                forMap.put("fileSeq"		,toStore);

                cnt	+= mapper.setFileRelationInsert(forMap);
            }
        }

        return cnt;
    }

    /**
     * 대상의 기존 파일 관계만 초기화(삭제)하고, 전달된 배열로 재삽입한다.
     * 물리 파일은 삭제하지 않는다.
     */
    public int resetFileRelations(HashMap<String,Object> paramMap) throws Exception {
        String fileArr = SUtils.nvl(paramMap.get("fileArr"));
        String fileSeq = SUtils.nvl(paramMap.get("fileSeq"));
        String[] fileArray = fileArr.equals("") ? fileSeq.split(",") : fileArr.split(",");
        String fileTrgetSe = SUtils.nvl(paramMap.get("fileTrgetSe"));
        int fileTrgetSeq = SUtils.strToInt(paramMap.get("fileTrgetSeq"));

        // 기존 관계만 삭제(파일은 보존)
        HashMap<String,Object> delParam = new HashMap<>();
        delParam.put("fileTrgetSe", fileTrgetSe);
        delParam.put("fileTrgetSeq", fileTrgetSeq);
        mapper.setFileRelationDelSecond(delParam);

        int cnt = 0;
        if (fileArray != null && fileArray.length > 0 && !fileArray[0].equals("")) {
            for (String s : fileArray) {
                HashMap<String,Object> forMap = new HashMap<>();
                forMap.put("fileTrgetSe", fileTrgetSe);
                forMap.put("fileTrgetSeq", fileTrgetSeq);
                String toStore = s != null && s.matches("\\d+") ? s : AESUtil.urlDec(s);
                forMap.put("fileSeq", toStore);
                cnt += mapper.setFileRelationInsert(forMap);
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
                String toStore = s != null && s.matches("\\d+") ? s : AESUtil.urlDec(s);
                forMap.put("fileSeq"		,toStore);

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
        String a = SUtils.nvl(paramMap.get("a"));
        String b = SUtils.nvl(paramMap.get("b"));

        // 안전 검증: a는 yyyyMM(숫자 6자리), b는 안전한 파일명만 허용
        if (!a.matches("^\\d{6}$")) {
            throw new KFException("잘못된 파일 경로 요청입니다.");
        }
        if (!b.matches("^[A-Za-z0-9][A-Za-z0-9._-]*$")) {
            throw new KFException("잘못된 파일명 요청입니다.");
        }

        String base = fileUtil.getFilePath();
        String path = SUtils.normalizePath(base + "/editor/" + a + "/" + b);

        returnMap.put("filePath", path);
        returnMap.put("fileName", b);

        return returnMap;
    }


}
