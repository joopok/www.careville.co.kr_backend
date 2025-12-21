package kr.co.cleaning.ctrl;

import kr.co.cleaning.svc.CmmnSvc;
import kr.co.cleaning.svc.ReviewSvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewCtrl.class)
class ReviewCtrlApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewSvc reviewSvc;

    // 일부 페이지 진입에서만 사용되므로 목으로 주입
    @MockBean
    private CmmnSvc cmmnSvc;

    @Test
    @DisplayName("리뷰 등록 유효성 실패 - 필수값 누락")
    void createReview_validationFail() throws Exception {
        String body = "{\n" +
                "  \"reviewCn\": \"내용\",\n" +
                "  \"starRate\": 5\n" +
                "}";

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void createReview_success() throws Exception {
        HashMap<String, Object> svcRet = new HashMap<>();
        svcRet.put("isReg", "Y");
        svcRet.put("reviewSeq", 100);
        when(reviewSvc.setReviewReg(any(), any())).thenReturn(svcRet);

        String body = "{\n" +
                "  \"reviewNm\": \"홍길동\",\n" +
                "  \"reviewCn\": \"내용\",\n" +
                "  \"starRate\": 5\n" +
                "}";

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.reviewSeq").value(100));
    }

    @Test
    @DisplayName("전체 리뷰 조회 API 성공")
    void getAllReviews_success() throws Exception {
        HashMap<String, Object> reviewItem = new HashMap<>();
        reviewItem.put("reviewSeq", 1);
        HashMap<String, Object> listResp = new HashMap<>();
        listResp.put("list", List.of(reviewItem));
        listResp.put("rowNum", 1);
        listResp.put("serviceCdLst", List.of());
        listResp.put("productCdList", List.of());
        when(reviewSvc.getReviewList(any(), any())).thenReturn(listResp);

        mockMvc.perform(get("/api/reviews/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() throws Exception {
        HashMap<String, Object> upd = new HashMap<>();
        upd.put("success", true);
        upd.put("isUpd", "Y");
        upd.put("message", "리뷰가 성공적으로 수정되었습니다.");
        when(reviewSvc.setReviewUpd(any(), any())).thenReturn(upd);

        String body = "{\n\t\"reviewNm\":\"수정\"\n}";
        mockMvc.perform(post("/api/reviews/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() throws Exception {
        HashMap<String, Object> del = new HashMap<>();
        del.put("success", true);
        del.put("isDel", "Y");
        del.put("message", "리뷰가 성공적으로 삭제되었습니다.");
        when(reviewSvc.setReviewDel(any(), any())).thenReturn(del);

        mockMvc.perform(post("/api/reviews/123/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

