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
import kr.co.cleaning.core.utils.HtmlSanitizer;
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

		// limitViewRowCnt 또는 rowLimit가 설정된 경우 그 값을 사용, 아니면 기본값 24
		int viewRowCnt = 24;
		if (paramMap.get("limitViewRowCnt") != null) {
			viewRowCnt = Integer.parseInt(paramMap.get("limitViewRowCnt").toString());
		} else if (paramMap.get("rowLimit") != null) {
			viewRowCnt = Integer.parseInt(paramMap.get("rowLimit").toString());
		}
		pageUtil.setViewRowCnt(viewRowCnt);

		// pagination setting
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getCaseCnt(paramMap));

        List<HashMap<String,Object>> list	= mapper.getCaseList(paramMap);

        for(HashMap<String,Object> a : list){
            String fs = SUtils.nvl(a.get("fileSeq"));
            if (!SUtils.isNvl(fs)) {
                a.put("viewFileSeq", AESUtil.urlEnc(fs));
            } else {
                a.put("viewFileSeq", "");
            }
            String fs2 = SUtils.nvl(a.get("fileSeq2"));
            if (!SUtils.isNvl(fs2)) {
                a.put("viewFileSeq2", AESUtil.urlEnc(fs2));
            } else {
                a.put("viewFileSeq2", "");
            }
        }

		returnMap.put("rowNum"			,pageUtil.getRowNum());
		returnMap.put("pagination"		,pageUtil.getPaging("json"));		// pagination
		returnMap.put("list"			,list);		// 목록
		returnMap.put("svcCdList"		,cmmnSvc.getServiceCdList()); 		// 서비스내용 코드
		returnMap.put("caseSvcCdList"	,mapper.getCaseServiceList()); 		// 등록되어있는 서비스내용 코드

		return returnMap;
	}

	/**
	 * API용 시공사례 전체 목록 조회 (등록일 내림차순)
	 */
	public List<HashMap<String,Object>> getCaseListForApi(HashMap<String,Object> paramMap) throws Exception {
		List<HashMap<String,Object>> list = mapper.getCaseListAll(paramMap);

        for(HashMap<String,Object> item : list) {
            String fs = SUtils.nvl(item.get("fileSeq"));
            if (!SUtils.isNvl(fs)) {
                item.put("viewFileSeq", AESUtil.urlEnc(fs));
            } else {
                item.put("viewFileSeq", "");
            }
            String fs2 = SUtils.nvl(item.get("fileSeq2"));
            if (!SUtils.isNvl(fs2)) {
                item.put("viewFileSeq2", AESUtil.urlEnc(fs2));
            } else {
                item.put("viewFileSeq2", "");
            }
        }

		return list;
	}

	/**
	 * API용 시공사례 상세 조회
	 */
	public HashMap<String,Object> getCaseViewForApi(HashMap<String,Object> paramMap) throws Exception {
		HashMap<String,Object> view = mapper.getCaseView(paramMap);

        if(view != null) {
            String fs = SUtils.nvl(view.get("fileSeq"));
            if (!SUtils.isNvl(fs)) {
                view.put("viewFileSeq", AESUtil.urlEnc(fs));
                HashMap<String,Object> fileView = cmmnSvc.getFileView(view);
                view.put("fileName", SUtils.nvl(fileView.get("fileName")));
            } else {
                view.put("viewFileSeq", "");
                view.put("fileName", "");
            }
            String fs2 = SUtils.nvl(view.get("fileSeq2"));
            if (!SUtils.isNvl(fs2)) {
                view.put("viewFileSeq2", AESUtil.urlEnc(fs2));
            } else {
                view.put("viewFileSeq2", "");
            }
        }

		return view;
	}

    public HashMap<String,Object> getCaseView(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

        HashMap<String,Object> returnMap	= new HashMap<String, Object>();

        HashMap<String,Object> view			= mapper.getCaseView(paramMap);
        if (view == null) {
            throw new kr.co.cleaning.core.config.KFException("시공사례를 찾을 수 없습니다.");
        }
        // 대표이미지1 처리
        String fileSeqStr = SUtils.nvl(view.get("fileSeq"));
        if (!SUtils.isNvl(fileSeqStr)) {
            view.put("viewFileSeq", AESUtil.urlEnc(fileSeqStr));
            HashMap<String,Object> fileView = cmmnSvc.getFileView(view);
            view.put("fileName", SUtils.nvl(fileView.get("fileName")));
        } else {
            view.put("viewFileSeq", "");
            view.put("fileName", "");
        }
        // 대표이미지2 처리
        String fileSeq2Str = SUtils.nvl(view.get("fileSeq2"));
        if (!SUtils.isNvl(fileSeq2Str)) {
            view.put("viewFileSeq2", AESUtil.urlEnc(fileSeq2Str));
        } else {
            view.put("viewFileSeq2", "");
        }

        // 갤러리 목록 로드 (대표이미지도 포함되어 있을 수 있음)
        HashMap<String,Object> relParam = new HashMap<>();
        relParam.put("fileTrgetSe", "CASE");
        relParam.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("caseSeq")));
        List<HashMap<String,Object>> galleryList = cmmnSvc.getFileList(relParam);
        for (HashMap<String,Object> g : galleryList) {
            String gfs = SUtils.nvl(g.get("fileSeq"));
            if (!SUtils.isNvl(gfs)) {
                g.put("viewFileSeq", AESUtil.urlEnc(gfs));
            } else {
                g.put("viewFileSeq", "");
            }
        }

        returnMap.put("view"		,view);
        returnMap.put("galleryList", galleryList);
        // 수정화면 초기 hidden 값 세팅용(대표이미지1,2는 제외)
        StringBuilder gb = new StringBuilder();
        for (HashMap<String,Object> g : galleryList) {
            String fs = SUtils.nvl(g.get("fileSeq"));
            // 대표이미지1, 대표이미지2는 갤러리에서 제외
            if (!fs.equals(SUtils.nvl(view.get("fileSeq"))) && !fs.equals(SUtils.nvl(view.get("fileSeq2")))) {
                if (gb.length() > 0) gb.append(",");
                gb.append(SUtils.nvl(g.get("viewFileSeq")));
            }
        }
        returnMap.put("galleryArrStr", gb.toString());
        returnMap.put("svcCdList"	,cmmnSvc.getServiceCdList()); 		// 서비스내용 코드

        return returnMap;
    }

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String,Object> setCaseUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

        HashMap<String,Object> returnMap	= new HashMap<String, Object>();
        Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

        paramMap.put("fileTrgetSe"	,"CASE");	// 대상 구분(CNSLT:상담,BOARD:게시판)
        paramMap.put("regNm"		,sessionMap != null ? SUtils.nvl(sessionMap.get("mngrNcnm")) : "admin");
        // 대표이미지1
        paramMap.put("fileSeq"		,AESUtil.urlDec(SUtils.nvl(paramMap.get("mainImg"))));
        // 대표이미지2
        paramMap.put("fileSeq2"		,AESUtil.urlDec(SUtils.nvl(paramMap.get("mainImg2"))));
		paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("caseSeq")));
		if (SUtils.isNvl(paramMap.get("mainImg"))) {
			throw new kr.co.cleaning.core.config.KFException("대표 이미지1은 필수입니다.");
		}

		validateHashtags(SUtils.nvl(paramMap.get("hashtag")));

        // 내용 sanitize
        paramMap.put("caseCn", HtmlSanitizer.sanitize(SUtils.nvl(paramMap.get("caseCn"))));
        int caseCnt = mapper.setCaseUpd(paramMap);

        // 대표1 + 대표2 + 갤러리 전체 재설정
        String galleryArr = SUtils.nvl(paramMap.get("galleryArr"));
        String fileArrCombined = SUtils.nvl(paramMap.get("mainImg"));
        if (!SUtils.isNvl(paramMap.get("mainImg2"))) {
            fileArrCombined = fileArrCombined + "," + SUtils.nvl(paramMap.get("mainImg2"));
        }
        if (!SUtils.isNvl(galleryArr)) {
            fileArrCombined = fileArrCombined + "," + galleryArr;
        }
        HashMap<String,Object> relParam = new HashMap<>();
        relParam.put("fileTrgetSe", "CASE");
        relParam.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("caseSeq")));
        relParam.put("fileArr", fileArrCombined);
        cmmnSvc.resetFileRelations(relParam);

        returnMap.put("isUpd", "Y");

        return returnMap;
    }

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String,Object> setCaseReg(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();
		Map<String, Object> sessionMap		= sessionCmn.getLogonInfo();

        paramMap.put("fileTrgetSe"	,"CASE");	// 대상 구분(CNSLT:상담,BOARD:게시판)
        paramMap.put("regNm"		,sessionMap != null ? SUtils.nvl(sessionMap.get("mngrNcnm")) : "admin");
        // 대표이미지1
		paramMap.put("fileSeq"		,AESUtil.urlDec(SUtils.nvl(paramMap.get("mainImg"))));
		// 대표이미지2
		paramMap.put("fileSeq2"		,AESUtil.urlDec(SUtils.nvl(paramMap.get("mainImg2"))));
		if (SUtils.isNvl(paramMap.get("mainImg"))) {
			throw new kr.co.cleaning.core.config.KFException("대표 이미지1은 필수입니다.");
		}

		validateHashtags(SUtils.nvl(paramMap.get("hashtag")));

        // 내용 sanitize
        paramMap.put("caseCn", HtmlSanitizer.sanitize(SUtils.nvl(paramMap.get("caseCn"))));
        int caseCnt		= mapper.setCaseReg(paramMap);

        paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("CASE_SEQ")));
        // 대표1 + 대표2 + 갤러리 전체 삽입
        String galleryArr = SUtils.nvl(paramMap.get("galleryArr"));
        String fileArrCombined = SUtils.nvl(paramMap.get("mainImg"));
        if (!SUtils.isNvl(paramMap.get("mainImg2"))) {
            fileArrCombined = fileArrCombined + "," + SUtils.nvl(paramMap.get("mainImg2"));
        }
        if (!SUtils.isNvl(galleryArr)) {
            fileArrCombined = fileArrCombined + "," + galleryArr;
        }
        paramMap.put("fileArr", fileArrCombined);
        int isnertCnt	= cmmnSvc.setFileRelationInsert(paramMap);

		returnMap.put("isReg", "Y");

		return returnMap;
	}

	// 해시태그 서버 검증: 개별 태그 길이, 개수, 총 길이 제한
	private void validateHashtags(String hashtags) {
		if (SUtils.isNvl(hashtags)) return;
		String[] parts = hashtags.split(",");
		int maxTags = 20;
		int maxPerTag = 30;
		int maxTotal = 500;
		if (hashtags.length() > maxTotal) {
			throw new kr.co.cleaning.core.config.KFException("해시태그 총 길이를 초과했습니다.(최대 " + maxTotal + ")");
		}
		if (parts.length > maxTags) {
			throw new kr.co.cleaning.core.config.KFException("해시태그는 최대 " + maxTags + "개까지 가능합니다.");
		}
		for (String t : parts) {
			String tag = t.trim();
			if (tag.startsWith("#")) tag = tag.substring(1);
			if (tag.length() > maxPerTag) {
				throw new kr.co.cleaning.core.config.KFException("해시태그 길이가 너무 깁니다.(최대 " + maxPerTag + ")");
			}
		}
	}

    @Transactional(rollbackFor = Exception.class)
    public HashMap<String,Object> setCaseDel(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

        HashMap<String,Object> returnMap	= new HashMap<String, Object>();
        // 삭제 전에 대표 파일 정보 확보
        HashMap<String,Object> view = mapper.getCaseView(paramMap);
        Integer fileSeq = null;
        if (view != null) {
            String fs = SUtils.nvl(view.get("fileSeq"));
            fileSeq = SUtils.isNvl(fs) ? null : Integer.valueOf(fs);
        }

        int caseCnt = mapper.setCaseDel(paramMap);

        // 관련 파일 관계 및 파일 삭제
        HashMap<String,Object> delParam = new HashMap<>();
        delParam.put("fileTrgetSe", "CASE");
        delParam.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("caseSeq")));
        HashMap<String,Object> relDelRs = cmmnSvc.setFileRelationDel(delParam);

        // 관계 삭제로 파일이 제거되지 않았다면(레거시 데이터 대비) 대표파일 직접 삭제 시도
        if ((relDelRs == null || !(Boolean.TRUE.equals(relDelRs.get("isDel")))) && fileSeq != null) {
            try {
                cmmnSvc.setFileDelBySeq(fileSeq);
            } catch (Exception ignore) {
                // 파일이 없거나 이미 삭제된 경우 무시
            }
        }

        returnMap.put("isDel", "Y");

        return returnMap;
    }

}
