package kr.co.cleaning.ctrl;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.svc.DashboardSvc;

@Controller
public class HomeCtrl {

	private static final Logger log	= LoggerFactory.getLogger(HomeCtrl.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	DashboardSvc dashboardSvc;

	@GetMapping(value = {"/apage/home.do"})
	public String home(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		return "apage/home";
	}

	/**
	 * 대시보드 통계 API
	 * 신규 상담, 시공 사례, 고객 리뷰, 상품 건수 반환
	 */
	@PostMapping(value = {"/apage/dashboardStats.do"})
	public String dashboardStats(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		modelMap.addAllAttributes(dashboardSvc.getDashboardStats());
		return "jsonView";
	}

	/**
	 * 최근 활동 API
	 * 최근 상담, 리뷰, 시공 사례 등 활동 목록 반환
	 */
	@PostMapping(value = {"/apage/recentActivities.do"})
	public String recentActivities(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		modelMap.addAllAttributes(dashboardSvc.getRecentActivities());
		return "jsonView";
	}

	/**
	 * 대시보드 전체 데이터 API
	 * 통계 + 최근 활동 통합 반환
	 */
	@PostMapping(value = {"/apage/dashboardData.do"})
	public String dashboardData(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		modelMap.addAllAttributes(dashboardSvc.getDashboardData());
		return "jsonView";
	}

}
