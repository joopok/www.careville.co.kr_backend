package kr.co.cleaning.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.svc.ProductSvc;

/**
 * 상품 관리 Controller
 */
@Controller
@RequestMapping("/apage")
public class ProductCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ProductCtrl.class);

    @Autowired
    private ProductSvc productSvc;


	@Autowired
	SessionCmn sessionCmn;

    /**
     * 상품 목록 화면
     */
    @RequestMapping("/product010.do")
    public String productList(@RequestParam Map<String, Object> param, Model model, HttpServletRequest request) {

        // 페이징 처리
        String curPage = SUtils.nvl((String)param.get("curPage"), "1");
        int viewRowCnt = 10;
        int viewPageCnt = 10;

        param.put("curPage", curPage);
        param.put("rowStrt", (Integer.parseInt(curPage) - 1) * viewRowCnt);
        param.put("rowLimit", viewRowCnt);

        // 상품 목록 조회
        Map<String, Object> resultMap = productSvc.selectProductList(param);

        // 페이징 설정
        PageUtil pageUtil = new PageUtil();
        HashMap<String, Object> pageParam = new HashMap<>();
        pageParam.put("currPage", curPage);
        pageUtil.setCurrPage(pageParam);
        pageUtil.setTotalRowCnt(Integer.parseInt(resultMap.get("totCnt").toString()));
        pageUtil.setViewRowCnt(viewRowCnt);
        pageUtil.setViewPageCnt(viewPageCnt);

        model.addAttribute("list", resultMap.get("list"));
        model.addAttribute("pageUtil", pageUtil);
        model.addAttribute("searchParam", param);

        // 카테고리 목록 조회
        model.addAttribute("categoryList", productSvc.selectCategoryList());

        return "apage/product/productList";
    }

    /**
     * 상품 상세 화면
     */
    @GetMapping("/product020.do")
    public String productView(@RequestParam Map<String, Object> param, Model model, HttpServletRequest request) {

        // 상품 정보 조회
        Map<String, Object> product = productSvc.selectProduct(param);
        model.addAttribute("product", product);

        // 카테고리 목록
        model.addAttribute("categoryList", productSvc.selectCategoryList());


        return "apage/product/productView";
    }

    /**
     * 상품 등록 화면
     */
    @GetMapping("/product030.do")
    public String productRegister(Model model, HttpServletRequest request) {

        // 카테고리 목록 조회
        model.addAttribute("categoryList", productSvc.selectCategoryList());

        return "apage/product/productRegister";
    }

    /**
     * 상품 등록 처리
     */
    @PostMapping("/product031.do")
    public ModelAndView productInsert(@RequestParam Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();

        try {
            // 등록자 정보 설정
            Map<String, Object> userInfo = sessionCmn.getLogonInfo();
            param.put("regUserId", userInfo.get("mngrId"));

            // 상품 등록
            int result = productSvc.insertProduct(param);


            mav.addObject("result", "SUCCESS");
            mav.addObject("msg", "상품이 등록되었습니다.");
            mav.addObject("redirectUrl", "/apage/product010.do");

        } catch(Exception e) {
            e.printStackTrace();
            mav.addObject("result", "FAIL");
            mav.addObject("msg", "상품 등록에 실패했습니다.");
        }

        mav.setViewName("jsonView");
        return mav;
    }

    /**
     * 상품 수정 화면
     */
    @GetMapping("/product040.do")
    public String productModify(@RequestParam Map<String, Object> param, Model model, HttpServletRequest request) {

        // 상품 정보 조회
        Map<String, Object> product = productSvc.selectProduct(param);
        model.addAttribute("product", product);

        // 카테고리 목록
        model.addAttribute("categoryList", productSvc.selectCategoryList());


        return "apage/product/productModify";
    }

    /**
     * 상품 수정 처리
     */
    @PostMapping("/product041.do")
    public ModelAndView productUpdate(@RequestParam Map<String, Object> param,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();

        try {
            // 수정자 정보 설정
            Map<String, Object> userInfo = sessionCmn.getLogonInfo();
            param.put("modUserId", userInfo.get("mngrId"));

            // 상품 수정
            int result = productSvc.updateProduct(param);


            mav.addObject("result", "SUCCESS");
            mav.addObject("msg", "상품이 수정되었습니다.");
            mav.addObject("redirectUrl", "/apage/product020.do?productNo=" + param.get("productNo"));

        } catch(Exception e) {
            e.printStackTrace();
            mav.addObject("result", "FAIL");
            mav.addObject("msg", "상품 수정에 실패했습니다.");
        }

        mav.setViewName("jsonView");
        return mav;
    }

    /**
     * 상품 삭제 처리
     */
    @PostMapping("/product051.do")
    public ModelAndView productDelete(@RequestParam Map<String, Object> param, HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();

        try {
            // 수정자 정보 설정
            Map<String, Object> userInfo = sessionCmn.getLogonInfo();
            param.put("modUserId", userInfo.get("mngrId"));

            // 상품 삭제
            int result = productSvc.deleteProduct(param);

            mav.addObject("result", result > 0 ? "SUCCESS" : "FAIL");
            mav.addObject("msg", result > 0 ? "상품이 삭제되었습니다." : "상품 삭제에 실패했습니다.");
            mav.addObject("redirectUrl", "/apage/product010.do");

        } catch(Exception e) {
            e.printStackTrace();
            mav.addObject("result", "FAIL");
            mav.addObject("msg", "상품 삭제에 실패했습니다.");
        }

        mav.setViewName("jsonView");
        return mav;
    }

    /**
     * 상품 목록 데이터 (AJAX)
     */
    @PostMapping("/product011.do")
    @ResponseBody
    public Map<String, Object> getProductList(@RequestParam Map<String, Object> param, HttpServletRequest reques) {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            // action 타입 확인
            String action = (String) param.get("action");

            if ("getCategories".equals(action)) {
                // 카테고리 목록만 반환
                List<Map<String, Object>> categories = productSvc.selectCategoryList();
                resultMap.put("categories", categories);
            } else {
                // 페이징 파라미터 설정
                String pageNo = (String) param.getOrDefault("pageNo", "1");
                int curPage = Integer.parseInt(pageNo);
                int viewRowCnt = 10;

                param.put("rowStrt", (curPage - 1) * viewRowCnt);
                param.put("rowLimit", viewRowCnt);

                // 상품 목록 조회
                Map<String, Object> data = productSvc.selectProductList(param);

                // 페이지 정보 설정
                int totalCnt = Integer.parseInt(data.get("totCnt").toString());
                int totalPage = (totalCnt + viewRowCnt - 1) / viewRowCnt;

                Map<String, Object> pagination = new HashMap<>();
                pagination.put("currPage", curPage);
                pagination.put("totalPage", totalPage);
                pagination.put("prev", curPage > 1 ? curPage - 1 : 0);
                pagination.put("next", curPage < totalPage ? curPage + 1 : 0);

                // 페이지 번호 리스트 생성
                List<Integer> pageList = new java.util.ArrayList<>();
                int startPage = ((curPage - 1) / 10) * 10 + 1;
                int endPage = Math.min(startPage + 9, totalPage);
                for (int i = startPage; i <= endPage; i++) {
                    pageList.add(i);
                }
                pagination.put("order", pageList);

                resultMap.put("list", data.get("list"));
                resultMap.put("rowNum", totalCnt - ((curPage - 1) * viewRowCnt));
                resultMap.put("pagination", pagination);
            }

        } catch(Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "데이터 조회에 실패했습니다.");
        }

        return resultMap;
    }

    /**
     * 카테고리별 상품 목록 (AJAX)
     */
    @PostMapping("/product012.do")
    @ResponseBody
    public Map<String, Object> getProductsByCategory(@RequestParam Map<String, Object> param, HttpServletRequest reques) {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            Map<String, Object> product = productSvc.selectProduct(param);

            if(product != null) {
                resultMap.put("result", "SUCCESS");
                resultMap.put("product", product);
            } else {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "상품을 찾을 수 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "데이터 조회에 실패했습니다.");
        }

        return resultMap;
    }

    /**
     * 상품 상세 정보 (AJAX)
     */
    @PostMapping("/product021.do")
    @ResponseBody
    public Map<String, Object> getProductDetail(@RequestParam Map<String, Object> param, HttpServletRequest reques) {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            Map<String, Object> product = productSvc.selectProduct(param);

            if(product != null) {
                resultMap.put("result", "SUCCESS");
                resultMap.put("product", product);
            } else {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "상품을 찾을 수 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "데이터 조회에 실패했습니다.");
        }

        return resultMap;
    }

}