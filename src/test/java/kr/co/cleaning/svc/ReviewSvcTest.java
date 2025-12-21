package kr.co.cleaning.svc;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.mapper.ReviewMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
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
class ReviewSvcTest {

    @Mock private SessionCmn sessionCmn;
    @Mock private ReviewMapper reviewMapper;
    @Mock private CmmnSvc cmmnSvc;
    @Mock private PageUtil pageUtil;
    @Mock private HttpServletRequest request;

    @InjectMocks private ReviewSvc reviewSvc;

    @Test
    @DisplayName("리뷰 목록 조회 - 페이징 포함")
    void getReviewList_success() throws Exception {
        when(pageUtil.getRowNum()).thenReturn(1);
        when(pageUtil.getPaging("json")).thenReturn(Map.of());
        when(reviewMapper.getReviewCnt(any())).thenReturn(1);
        when(reviewMapper.getReviewList(any())).thenReturn(List.of(new HashMap<>()));
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());
        when(cmmnSvc.getProductCdList()).thenReturn(List.of());

        HashMap<String, Object> res = reviewSvc.getReviewList(request, new HashMap<>());
        assertNotNull(res.get("list"));
        assertNotNull(res.get("pagination"));
        assertEquals(1, res.get("rowNum"));
    }

    @Test
    @DisplayName("리뷰 상세 비밀번호 검증 - 비밀번호 필요")
    void getReviewViewWithPassword_required() throws Exception {
        when(reviewMapper.getReviewViewWithPassword(any())).thenReturn(new HashMap<>(Map.of("pw", "hashed")));
        HashMap<String, Object> res = reviewSvc.getReviewViewWithPasswordCheck(request, new HashMap<>(Map.of("reviewSeq", 1)));
        assertEquals(false, res.get("success"));
        assertEquals(true, res.get("requirePassword"));
    }

    @Test
    @DisplayName("리뷰 상세 비밀번호 검증 - 불일치")
    void getReviewViewWithPassword_wrong() throws Exception {
        String hash = BCrypt.hashpw("1234", BCrypt.gensalt());
        when(reviewMapper.getReviewViewWithPassword(any())).thenReturn(new HashMap<>(Map.of("pw", hash)));

        HashMap<String, Object> res = reviewSvc.getReviewViewWithPasswordCheck(request, new HashMap<>(Map.of("reviewSeq", 1, "pw", "0000")));
        assertEquals(false, res.get("success"));
        assertEquals("비밀번호가 일치하지 않습니다.", res.get("message"));
    }

    @Test
    @DisplayName("리뷰 상세 비밀번호 검증 - 성공")
    void getReviewViewWithPassword_success() throws Exception {
        String hash = BCrypt.hashpw("1234", BCrypt.gensalt());
        HashMap<String, Object> review = new HashMap<>(Map.of("pw", hash, "reviewNm", "홍길동"));
        when(reviewMapper.getReviewViewWithPassword(any())).thenReturn(review);
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());

        HashMap<String, Object> res = reviewSvc.getReviewViewWithPasswordCheck(request, new HashMap<>(Map.of("reviewSeq", 1, "pw", "1234")));
        assertEquals(true, res.get("success"));
        Map<?,?> view = (Map<?,?>) res.get("view");
        assertFalse(view.containsKey("pw"));
    }

    @Test
    @DisplayName("리뷰 수정 - 비로그인, 비밀번호 필요")
    void setReviewUpd_requirePassword() throws Exception {
        when(reviewMapper.getReviewViewWithPassword(any())).thenReturn(new HashMap<>(Map.of("pw", "hash")));
        HashMap<String, Object> res = reviewSvc.setReviewUpd(request, new HashMap<>(Map.of("reviewSeq", 1)));
        assertEquals(false, res.get("success"));
        assertEquals(true, res.get("requirePassword"));
    }

    @Test
    @DisplayName("리뷰 수정 - 비밀번호 검증 후 성공")
    void setReviewUpd_successWithPassword() throws Exception {
        String hash = BCrypt.hashpw("1234", BCrypt.gensalt());
        when(reviewMapper.getReviewViewWithPassword(any())).thenReturn(new HashMap<>(Map.of("pw", hash, "svcDate", "2024-01-02")));
        when(reviewMapper.setReviewUpd(any())).thenReturn(1);

        HashMap<String, Object> res = reviewSvc.setReviewUpd(request, new HashMap<>(Map.of("reviewSeq", 1, "pw", "1234")));
        assertEquals(true, res.get("success"));
        assertEquals("Y", res.get("isUpd"));
    }

    @Test
    @DisplayName("리뷰 삭제 - 비밀번호 검증 후 성공")
    void setReviewDel_successWithPassword() throws Exception {
        String hash = BCrypt.hashpw("9999", BCrypt.gensalt());
        when(reviewMapper.getReviewViewWithPassword(any())).thenReturn(new HashMap<>(Map.of("pw", hash)));
        when(reviewMapper.setReviewDel(any())).thenReturn(1);

        HashMap<String, Object> res = reviewSvc.setReviewDel(request, new HashMap<>(Map.of("reviewSeq", 1, "pw", "9999")));
        assertEquals(true, res.get("success"));
        assertEquals("Y", res.get("isDel"));
    }
}

