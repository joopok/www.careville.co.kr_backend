package kr.co.cleaning.svc;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.mapper.BoardMapper;
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
class BoardSvcTest {

    @Mock private SessionCmn sessionCmn;
    @Mock private BoardMapper boardMapper;
    @Mock private CmmnSvc cmmnSvc;
    @Mock private PageUtil pageUtil;

    @Mock private HttpServletRequest request;

    @InjectMocks private BoardSvc boardSvc;

    @Test
    @DisplayName("게시판 목록 조회 - 페이징 및 목록 포함")
    void getBoardList_success() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        when(pageUtil.getRowNum()).thenReturn(1);
        when(pageUtil.getPaging("json")).thenReturn(Map.of("total", 1));
        when(boardMapper.getBoardCnt(any())).thenReturn(1);
        when(boardMapper.getBoardList(any())).thenReturn(List.of(new HashMap<>()))
        ;

        HashMap<String, Object> res = boardSvc.getBoardList(request, params);
        assertNotNull(res.get("list"));
        assertNotNull(res.get("pagination"));
        assertEquals(1, res.get("rowNum"));
    }

    @Test
    @DisplayName("게시판 상세 조회 - 파일/코드 포함")
    void getBoardView_success() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("boardSeq", 10);
        when(cmmnSvc.getFileList(any())).thenReturn(List.of(new HashMap<>(Map.of("fileSeq", 1))));
        when(boardMapper.getBoardView(any())).thenReturn(new HashMap<>(Map.of("boardSeq", 10)));
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());
        when(cmmnSvc.getCodeList("002")).thenReturn(List.of());

        HashMap<String, Object> res = boardSvc.getBoardView(request, params);
        assertNotNull(res.get("view"));
        assertNotNull(res.get("filesLst"));
        assertNotNull(res.get("serviceCdLst"));
        assertNotNull(res.get("sttusCdLst"));
    }

    @Test
    @DisplayName("게시판 등록 - 파일관계 처리 후 성공")
    void setBoardReg_success() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("boardSeq", "1");
        when(boardMapper.setBoardReg(any())).thenReturn(1);
        when(cmmnSvc.setFileRelationInsert(any())).thenReturn(1);

        HashMap<String, Object> res = boardSvc.setBoardReg(request, params);
        assertEquals("Y", res.get("isReg"));
        verify(boardMapper).setBoardReg(any());
        verify(cmmnSvc).setFileRelationInsert(any());
    }

    @Test
    @DisplayName("게시판 수정 - 답변자 ID 셋 후 성공")
    void setBoardUpd_success() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        when(sessionCmn.get("mngrId")).thenReturn("admin");
        when(boardMapper.setBoardUpd(any())).thenReturn(1);

        HashMap<String, Object> res = boardSvc.setBoardUpd(request, params);
        assertEquals("Y", res.get("isUpd"));
        verify(boardMapper).setBoardUpd(any());
    }
}

