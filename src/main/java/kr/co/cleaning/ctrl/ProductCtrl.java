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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.svc.CmmnSvc;
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
    private CmmnSvc cmmnSvc;

	@Autowired
	SessionCmn sessionCmn;

    /**
     * 상품 목록 화면
     */
    @RequestMapping("/product010.do")
    public String productList(@RequestParam Map<String, Object> param, Model model, HttpServletRequest request) {
        // 서버 렌더 단계에서는 DB 조회를 생략하고, 클라이언트에서 AJAX로 목록/카테고리를 로드합니다.
        // 초기 파라미터만 전달해도 페이지 동작에는 문제가 없습니다.
        model.addAttribute("searchParam", param);
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
    public ModelAndView productInsert(@RequestParam Map<String, Object> param,
            @RequestParam(value = "mainImg1", required = false) MultipartFile mainImg1,
            @RequestParam(value = "mainImg2", required = false) MultipartFile mainImg2,
            @RequestParam(value = "mainImg3", required = false) MultipartFile mainImg3,
            @RequestParam(value = "mainImg4", required = false) MultipartFile mainImg4,
            @RequestParam(value = "mainImg5", required = false) MultipartFile mainImg5,
            @RequestParam(value = "mainImg6", required = false) MultipartFile mainImg6,
            @RequestParam(value = "galleryFiles", required = false) MultipartFile[] galleryFiles,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();

        try {
            // 등록자 정보 설정
            Map<String, Object> userInfo = sessionCmn.getLogonInfo();
            if (userInfo == null) {
                mav.addObject("result", "FAIL");
                mav.addObject("msg", "로그인이 필요합니다.");
                mav.setViewName("jsonView");
                return mav;
            }
            param.put("regUserId", userInfo.get("mngrId"));

            // 대표 이미지 6개 업로드
            MultipartFile[] mainImages = {mainImg1, mainImg2, mainImg3, mainImg4, mainImg5, mainImg6};
            for (int i = 0; i < mainImages.length; i++) {
                MultipartFile mainImg = mainImages[i];
                if (mainImg != null && !mainImg.isEmpty()) {
                    List<MultipartFile> fileList = new java.util.ArrayList<>();
                    fileList.add(mainImg);
                    HashMap<String, Object> uploadParam = new HashMap<>();
                    HashMap<String, Object> fileResult = cmmnSvc.setFileUpload(uploadParam, fileList);
                    if (fileResult != null && fileResult.get("fileSeq") != null) {
                        String encryptedFileSeq = fileResult.get("fileSeq").toString();
                        String decryptedFileSeq = AESUtil.urlDec(encryptedFileSeq);
                        param.put("fileSeq" + (i + 1), Integer.parseInt(decryptedFileSeq));
                        logger.debug("Main image {} uploaded - fileSeq: {}", (i + 1), decryptedFileSeq);
                    }
                }
            }

            // 상품 등록 (useGeneratedKeys로 productNo가 param에 설정됨)
            int result = productSvc.insertProduct(param);
            logger.debug("Product inserted - productNo: {}", param.get("productNo"));

            // 갤러리 이미지 업로드 및 관계 등록 (최대 10개)
            if (galleryFiles != null && galleryFiles.length > 0 && param.get("productNo") != null) {
                int uploadedCount = 0;
                for (MultipartFile galleryFile : galleryFiles) {
                    if (galleryFile != null && !galleryFile.isEmpty() && uploadedCount < 10) {
                        List<MultipartFile> galleryList = new java.util.ArrayList<>();
                        galleryList.add(galleryFile);
                        HashMap<String, Object> uploadParam = new HashMap<>();
                        HashMap<String, Object> fileResult = cmmnSvc.setFileUpload(uploadParam, galleryList);
                        if (fileResult != null && fileResult.get("fileSeq") != null) {
                            HashMap<String, Object> relationParam = new HashMap<>();
                            relationParam.put("fileTrgetSe", "PRODUCT");
                            relationParam.put("fileTrgetSeq", param.get("productNo"));
                            relationParam.put("fileSeq", fileResult.get("fileSeq"));
                            cmmnSvc.setFileRelationInsert(relationParam);
                            uploadedCount++;
                            logger.debug("Gallery image {} uploaded for productNo: {}", uploadedCount, param.get("productNo"));
                        }
                    }
                }
            }

            mav.addObject("result", "SUCCESS");
            mav.addObject("msg", "상품이 등록되었습니다.");
            mav.addObject("redirectUrl", "/apage/product010.do");

        } catch(Exception e) {
            // 상세 스택 트레이스 로깅 (디버깅용)
            logger.error("상품 등록 실패 - 예외 유형: {}, 메시지: {}",
                        e.getClass().getName(), e.getMessage());
            logger.error("상세 스택 트레이스:", e);
            mav.addObject("result", "FAIL");
            mav.addObject("msg", "상품 등록에 실패했습니다: " + e.getMessage());
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
            @RequestParam(value = "mainImg1", required = false) MultipartFile mainImg1,
            @RequestParam(value = "mainImg2", required = false) MultipartFile mainImg2,
            @RequestParam(value = "mainImg3", required = false) MultipartFile mainImg3,
            @RequestParam(value = "mainImg4", required = false) MultipartFile mainImg4,
            @RequestParam(value = "mainImg5", required = false) MultipartFile mainImg5,
            @RequestParam(value = "mainImg6", required = false) MultipartFile mainImg6,
            @RequestParam(value = "galleryFiles", required = false) MultipartFile[] galleryFiles,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();

        try {
            // 수정자 정보 설정
            Map<String, Object> userInfo = sessionCmn.getLogonInfo();
            if (userInfo == null) {
                mav.addObject("result", "FAIL");
                mav.addObject("msg", "로그인이 필요합니다.");
                mav.setViewName("jsonView");
                return mav;
            }
            param.put("modUserId", userInfo.get("mngrId"));

            // 대표 이미지 6개 처리
            MultipartFile[] mainImages = {mainImg1, mainImg2, mainImg3, mainImg4, mainImg5, mainImg6};
            for (int i = 0; i < mainImages.length; i++) {
                MultipartFile mainImg = mainImages[i];
                String paramKey = "fileSeq" + (i + 1);

                if (mainImg != null && !mainImg.isEmpty()) {
                    // 새 파일 업로드
                    List<MultipartFile> fileList = new java.util.ArrayList<>();
                    fileList.add(mainImg);
                    HashMap<String, Object> uploadParam = new HashMap<>();
                    HashMap<String, Object> fileResult = cmmnSvc.setFileUpload(uploadParam, fileList);
                    if (fileResult != null && fileResult.get("fileSeq") != null) {
                        String encryptedFileSeq = fileResult.get("fileSeq").toString();
                        String decryptedFileSeq = AESUtil.urlDec(encryptedFileSeq);
                        param.put(paramKey, Integer.parseInt(decryptedFileSeq));
                        logger.debug("Main image {} uploaded - fileSeq: {}", (i + 1), decryptedFileSeq);
                    }
                } else {
                    // 새 파일 업로드 없음 - 기존 fileSeq 값 처리
                    String existingFileSeq = (String) param.get(paramKey);
                    if (existingFileSeq != null && !existingFileSeq.isEmpty()) {
                        try {
                            // 암호화된 값이면 복호화
                            String decrypted = AESUtil.urlDec(existingFileSeq);
                            if (decrypted != null && !decrypted.isEmpty()) {
                                param.put(paramKey, Integer.parseInt(decrypted));
                                logger.debug("Main image {} preserved - fileSeq: {}", (i + 1), decrypted);
                            }
                        } catch (Exception e) {
                            // 숫자 형태면 그대로 사용
                            try {
                                param.put(paramKey, Integer.parseInt(existingFileSeq));
                                logger.debug("Main image {} preserved (numeric) - fileSeq: {}", (i + 1), existingFileSeq);
                            } catch (NumberFormatException nfe) {
                                // 파싱 실패 시 기존 값 유지를 위해 null로 설정하지 않음
                                param.remove(paramKey);
                                logger.debug("Main image {} fileSeq parse failed, keeping existing", (i + 1));
                            }
                        }
                    }
                }
            }

            // 상품 수정
            int result = productSvc.updateProduct(param);

            // 갤러리 이미지 추가 업로드 (최대 10개)
            if (galleryFiles != null && galleryFiles.length > 0 && param.get("productNo") != null) {
                int uploadedCount = 0;
                for (MultipartFile galleryFile : galleryFiles) {
                    if (galleryFile != null && !galleryFile.isEmpty() && uploadedCount < 10) {
                        List<MultipartFile> galleryList = new java.util.ArrayList<>();
                        galleryList.add(galleryFile);
                        HashMap<String, Object> uploadParam = new HashMap<>();
                        HashMap<String, Object> fileResult = cmmnSvc.setFileUpload(uploadParam, galleryList);
                        if (fileResult != null && fileResult.get("fileSeq") != null) {
                            HashMap<String, Object> relationParam = new HashMap<>();
                            relationParam.put("fileTrgetSe", "PRODUCT");
                            relationParam.put("fileTrgetSeq", param.get("productNo"));
                            relationParam.put("fileSeq", fileResult.get("fileSeq"));
                            cmmnSvc.setFileRelationInsert(relationParam);
                            uploadedCount++;
                            logger.debug("Gallery image {} added for productNo: {}", uploadedCount, param.get("productNo"));
                        }
                    }
                }
            }

            mav.addObject("result", "SUCCESS");
            mav.addObject("msg", "상품이 수정되었습니다.");
            mav.addObject("redirectUrl", "/apage/product020.do?productNo=" + param.get("productNo"));

        } catch(Exception e) {
            // 상세 스택 트레이스 로깅 (디버깅용)
            logger.error("상품 수정 실패 - productNo: {}, 예외 유형: {}, 메시지: {}",
                        param.get("productNo"), e.getClass().getName(), e.getMessage());
            logger.error("상세 스택 트레이스:", e);
            mav.addObject("result", "FAIL");
            mav.addObject("msg", "상품 수정에 실패했습니다: " + e.getMessage());
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
            if (userInfo == null) {
                mav.addObject("result", "FAIL");
                mav.addObject("msg", "로그인이 필요합니다.");
                mav.setViewName("jsonView");
                return mav;
            }
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
                pagination.put("totalRowCnt", totalCnt);
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
