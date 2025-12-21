package kr.co.cleaning.ctrl;

import kr.co.cleaning.svc.BoardSvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardCtrl.class)
class BoardCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardSvc boardSvc;

    @Test
    @DisplayName("게시판 기본 화면 이동(10)")
    void boardDefaultView() throws Exception {
        mockMvc.perform(get("/apage/board010.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/board/board010"));
    }

    @Test
    @DisplayName("게시판 목록 JSON(11)")
    void boardListJson() throws Exception {
        when(boardSvc.getBoardList(any(), any())).thenReturn(new HashMap<>(Map.of("list", java.util.List.of())));

        mockMvc.perform(get("/apage/board011.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    @DisplayName("게시판 목록/상세/등록/수정 JSON API")
    void boardJsonApis() throws Exception {
        when(boardSvc.getBoardList(any(), any())).thenReturn(new HashMap<>(Map.of("list", java.util.List.of())));
        when(boardSvc.getBoardView(any(), any())).thenReturn(new HashMap<>(Map.of("view", Map.of())));
        when(boardSvc.setBoardReg(any(), any())).thenReturn(new HashMap<>(Map.of("isReg", "Y")));
        when(boardSvc.setBoardUpd(any(), any())).thenReturn(new HashMap<>(Map.of("isUpd", "Y")));

        mockMvc.perform(post("/boardList.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/boardView.do").param("boardSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/boardReg.do").param("title", "등록"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/boardUpd.do").param("boardSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));
    }
}

