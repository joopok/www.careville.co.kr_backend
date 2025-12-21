package kr.co.cleaning.svc;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.mapper.CnsltMapper;
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
class CnsltSvcTest {

    @Mock private SessionCmn sessionCmn;
    @Mock private CnsltMapper cnsltMapper;
    @Mock private CmmnSvc cmmnSvc;
    @Mock private PageUtil pageUtil;
    @Mock private HttpServletRequest request;

    @InjectMocks private CnsltSvc cnsltSvc;

    @Test
    @DisplayName("상담 목록 조회")
    void getCnsltList_success() throws Exception {
        when(pageUtil.getRowNum()).thenReturn(1);
        when(pageUtil.getPaging("json")).thenReturn(Map.of());
        when(cnsltMapper.getCnsltCnt(any())).thenReturn(1);
        when(cnsltMapper.getCnsltList(any())).thenReturn(List.of(new HashMap<>()));

        HashMap<String, Object> res = cnsltSvc.getCnsltList(request, new HashMap<>());
        assertNotNull(res.get("list"));
        assertNotNull(res.get("pagination"));
        assertEquals(1, res.get("rowNum"));
    }

    @Test
    @DisplayName("상담 상세 조회 - 파일/코드 포함")
    void getCnsltView_success() throws Exception {
        when(cmmnSvc.getFileList(any())).thenReturn(List.of(new HashMap<>(Map.of("fileSeq", 1))));
        when(cnsltMapper.getCnsltView(any())).thenReturn(new HashMap<>(Map.of("cnsltSeq", 1)));
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());
        when(cmmnSvc.getCodeList("002")).thenReturn(List.of());

        HashMap<String, Object> res = cnsltSvc.getCnsltView(request, new HashMap<>(Map.of("cnsltSeq", 1)));
        assertNotNull(res.get("view"));
        assertNotNull(res.get("filesLst"));
        assertNotNull(res.get("serviceCdLst"));
        assertNotNull(res.get("sttusCdLst"));
    }

    @Test
    @DisplayName("상담 등록/수정 성공")
    void setCnsltRegUpd_success() throws Exception {
        when(cnsltMapper.setCnsltReg(any())).thenReturn(1);
        when(cmmnSvc.setFileRelationInsert(any())).thenReturn(1);
        HashMap<String, Object> reg = cnsltSvc.setCnsltReg(request, new HashMap<>());
        assertEquals("Y", reg.get("isReg"));

        when(sessionCmn.get("mngrId")).thenReturn("admin");
        when(cnsltMapper.setCnsltUpd(any())).thenReturn(1);
        HashMap<String, Object> upd = cnsltSvc.setCnsltUpd(request, new HashMap<>());
        assertEquals("Y", upd.get("isUpd"));
    }
}

