package kr.co.cleaning.svc;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.mapper.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorySvcTest {

    @Mock private CategoryMapper categoryMapper;
    @Mock private SessionCmn sessionCmn;
    @Mock private PageUtil pageUtil;

    @InjectMocks private CategorySvc categorySvc;

    @Test
    @DisplayName("카테고리 목록 - 페이징 설정")
    void getCategoryList_success() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", "1");
        when(categoryMapper.selectCategoryListCnt(any())).thenReturn(1);
        when(categoryMapper.selectCategoryList(any())).thenReturn(List.of(Map.of("serviceCd", "SV001")));
        when(pageUtil.getPaging("json")).thenReturn(Map.of());
        when(pageUtil.getRowNum()).thenReturn(1);

        Map<String, Object> res = categorySvc.getCategoryList(params);
        assertNotNull(res.get("list"));
        assertNotNull(res.get("pagination"));
        assertEquals(1, res.get("rowNum"));
        assertEquals(1, res.get("totalCount"));
    }

    @Test
    @DisplayName("카테고리 등록 - 중복 코드 실패")
    void insertCategory_duplicateFail() {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceCd", "SV001");
        when(categoryMapper.checkDuplicateServiceCd("SV001")).thenReturn(1);

        Map<String, Object> res = categorySvc.insertServiceCategory(params);
        assertEquals("FAIL", res.get("result"));
    }

    @Test
    @DisplayName("카테고리 등록 - 성공")
    void insertCategory_success() {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceCd", "SV002");
        when(categoryMapper.checkDuplicateServiceCd("SV002")).thenReturn(0);
        when(categoryMapper.selectMaxServiceOrder()).thenReturn(3);
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("USER_ID", "admin"));
        when(categoryMapper.insertCategory(any())).thenReturn(1);

        Map<String, Object> res = categorySvc.insertServiceCategory(params);
        assertEquals("SUCCESS", res.get("result"));
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory_success() {
        Map<String, Object> params = new HashMap<>();
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("USER_ID", "admin"));
        when(categoryMapper.updateCategory(any())).thenReturn(1);

        Map<String, Object> res = categorySvc.updateServiceCategory(params);
        assertEquals("SUCCESS", res.get("result"));
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_success() {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("USER_ID", "admin"));
        when(categoryMapper.deleteCategory(any())).thenReturn(1);

        Map<String, Object> res = categorySvc.deleteServiceCategory("SV001");
        assertEquals("SUCCESS", res.get("result"));
    }

    @Test
    @DisplayName("카테고리 일괄 수정 - 카운트 합산")
    void updateCategoryBatch_success() {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("USER_ID", "admin"));
        when(categoryMapper.updateCategory(any())).thenReturn(1);

        Map<String, Object> res = categorySvc.updateCategoryBatch(List.of(Map.of("serviceCd", "SV001"), Map.of("serviceCd", "SV002")));
        assertEquals("SUCCESS", res.get("result"));
        assertEquals(2, res.get("updateCount"));
    }
}

