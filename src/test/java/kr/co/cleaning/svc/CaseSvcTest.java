package kr.co.cleaning.svc;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.mapper.CaseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseSvcTest {

    @Mock private SessionCmn sessionCmn;
    @Mock private CaseMapper caseMapper;
    @Mock private CmmnSvc cmmnSvc;
    @Mock private PageUtil pageUtil;
    @Mock private HttpServletRequest request;

    @InjectMocks private CaseSvc caseSvc;

    @Test
    @DisplayName("사례 목록 조회 - 페이징 포함")
    void getCaseList_success() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        when(pageUtil.getRowNum()).thenReturn(1);
        when(pageUtil.getPaging("json")).thenReturn(Map.of("p", 1));
        when(caseMapper.getCaseCnt(any())).thenReturn(1);
        when(caseMapper.getCaseList(any())).thenReturn(List.of(new HashMap<>(Map.of("fileSeq", 1))));
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());
        when(caseMapper.getCaseServiceList()).thenReturn(List.of());

        HashMap<String, Object> res = caseSvc.getCaseList(request, params);
        assertNotNull(res.get("list"));
        assertNotNull(res.get("pagination"));
        assertEquals(1, res.get("rowNum"));
        assertNotNull(res.get("svcCdList"));
        assertNotNull(res.get("caseSvcCdList"));
    }

    @Test
    @DisplayName("사례 상세 조회 - 파일명 포함")
    void getCaseView_success() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("caseSeq", 1);
        when(caseMapper.getCaseView(any())).thenReturn(new HashMap<>(Map.of("fileSeq", 10)));
        when(cmmnSvc.getFileView(any())).thenReturn(new HashMap<>(Map.of("fileName", "image.jpg")));
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());

        HashMap<String, Object> res = caseSvc.getCaseView(request, params);
        Map<?,?> view = (Map<?,?>) res.get("view");
        assertEquals("image.jpg", view.get("fileName"));
        assertNotNull(res.get("svcCdList"));
    }

    @Test
    @DisplayName("사례 등록 - 파일관계 추가")
    void setCaseReg_success() throws Exception {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("mngrNcnm", "관리자"));
        when(caseMapper.setCaseReg(any())).thenReturn(1);
        when(cmmnSvc.setFileRelationInsert(any())).thenReturn(1);

        HashMap<String, Object> res = caseSvc.setCaseReg(request, new HashMap<>());
        assertEquals("Y", res.get("isReg"));
    }

    @Test
    @DisplayName("사례 수정 - 파일관계 추가")
    void setCaseUpd_success() throws Exception {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("mngrNcnm", "관리자"));
        when(caseMapper.setCaseUpd(any())).thenReturn(1);
        when(cmmnSvc.setFileRelationInsert(any())).thenReturn(1);

        HashMap<String, Object> res = caseSvc.setCaseUpd(request, new HashMap<>(Map.of("caseSeq", "1")));
        assertEquals("Y", res.get("isUpd"));
    }

    @Test
    @DisplayName("사례 삭제")
    void setCaseDel_success() throws Exception {
        when(caseMapper.setCaseDel(any())).thenReturn(1);
        HashMap<String, Object> res = caseSvc.setCaseDel(request, new HashMap<>());
        assertEquals("Y", res.get("isDel"));
    }
}

