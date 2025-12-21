package kr.co.cleaning.ctrl;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.svc.DashboardSvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeCtrl.class)
class HomeCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionCmn sessionCmn;

    @MockBean
    private DashboardSvc dashboardSvc;

    @Test
    @DisplayName("홈 화면 이동")
    void homeView() throws Exception {
        mockMvc.perform(get("/apage/home.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/home"));
    }

    @Test
    @DisplayName("대시보드 통계 JSON")
    void dashboardStats() throws Exception {
        HashMap<String, Object> statsMap = new HashMap<>();
        statsMap.put("newCnslt", 3);
        when(dashboardSvc.getDashboardStats()).thenReturn(statsMap);

        mockMvc.perform(post("/apage/dashboardStats.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("newCnslt"));
    }

    @Test
    @DisplayName("대시보드 최근활동 JSON")
    void recentActivities() throws Exception {
        HashMap<String, Object> activitiesMap = new HashMap<>();
        activitiesMap.put("items", 1);
        when(dashboardSvc.getRecentActivities()).thenReturn(activitiesMap);

        mockMvc.perform(post("/apage/recentActivities.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("대시보드 전체 데이터 JSON")
    void dashboardData() throws Exception {
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put("a", 1);
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("stats", innerMap);
        when(dashboardSvc.getDashboardData()).thenReturn(dataMap);

        mockMvc.perform(post("/apage/dashboardData.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("stats"));
    }
}

