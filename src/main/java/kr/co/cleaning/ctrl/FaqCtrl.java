package kr.co.cleaning.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.svc.FaqSvc;

@Controller
public class FaqCtrl {

    @Autowired
    private FaqSvc faqSvc;

    // /admin/faqs → 기존 어드민 체계(/apage/*)에 맞춰 리다이렉트
    @GetMapping("/admin/faqs")
    public String adminFaqRedirect() {
        return "redirect:/apage/faq010.do";
    }

    // 어드민 화면 라우팅 (목록 화면)
    @RequestMapping("/apage/faq010.do")
    public String faqListPage(HttpServletRequest req, HttpServletResponse res,
                              @RequestParam(required = false) Map<String, Object> params,
                              ModelMap model) throws Exception {
        if (params == null) params = new HashMap<>();
        model.addAttribute("params", params);
        return "apage/faq/faq010";
    }

    // 어드민: 목록 JSON
    @PostMapping("/faqList.do")
    public String faqList(HttpServletRequest req, HttpServletResponse res,
                          @RequestParam HashMap<String, Object> paramMap,
                          ModelMap model) throws Exception {
        model.addAllAttributes(faqSvc.getFaqList(paramMap));
        return "jsonView";
    }

    // 어드민: 등록
    @PostMapping("/faqReg.do")
    public String faqReg(HttpServletRequest req, HttpServletResponse res,
                         @RequestParam HashMap<String, Object> paramMap,
                         ModelMap model) throws Exception {
        model.addAllAttributes(faqSvc.insertFaq(paramMap));
        return "jsonView";
    }

    // 어드민: 수정
    @PostMapping("/faqUpd.do")
    public String faqUpd(HttpServletRequest req, HttpServletResponse res,
                         @RequestParam HashMap<String, Object> paramMap,
                         ModelMap model) throws Exception {
        model.addAllAttributes(faqSvc.updateFaq(paramMap));
        return "jsonView";
    }

    // 어드민: 상세 조회
    @PostMapping("/faqView.do")
    public String faqView(HttpServletRequest req, HttpServletResponse res,
                          @RequestParam HashMap<String, Object> paramMap,
                          ModelMap model) throws Exception {
        model.addAllAttributes(faqSvc.getFaq(paramMap));
        return "jsonView";
    }

    // 어드민: 삭제(논리)
    @PostMapping("/faqDel.do")
    public String faqDel(HttpServletRequest req, HttpServletResponse res,
                         @RequestParam HashMap<String, Object> paramMap,
                         ModelMap model) throws Exception {
        model.addAllAttributes(faqSvc.deleteFaq(paramMap));
        return "jsonView";
    }

    // 공개 API: FAQ 목록
    @GetMapping({"/api/v1/faqs", "/api/v1/faqs.do"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publicFaqs(@RequestParam(required = false) Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (params == null) params = new HashMap<>();
            // 기본 정렬/페이징 처리(선택)
            params.putIfAbsent("rowStrt", 0);
            params.putIfAbsent("rowLimit", 100);

            List<Map<String, Object>> list = faqSvc.getPublicFaqList(params);
            result.put("success", true);
            result.put("data", list);
            result.put("totalCount", list.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "FAQ 조회 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    // ======================== REST API (프론트엔드 호환) ========================

    /**
     * FAQ 목록 조회 - 프론트엔드 호환 형식
     * GET /api/faqs
     */
    @GetMapping("/api/faqs")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFaqList() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("rowStrt", 0);
            params.put("rowLimit", 100);

            List<Map<String, Object>> dbList = faqSvc.getPublicFaqList(params);

            // 프론트엔드 형식으로 변환
            List<Map<String, Object>> frontendList = new ArrayList<>();
            for (Map<String, Object> item : dbList) {
                Map<String, Object> converted = new HashMap<>();
                converted.put("id", String.valueOf(item.get("faqSeq")));
                converted.put("question", item.get("question"));
                converted.put("answer", item.get("answer"));
                converted.put("category", item.get("category") != null ? item.get("category") : "");
                converted.put("display", "Y".equals(item.get("displayYn")));
                converted.put("order", item.get("displayOrder") != null ? item.get("displayOrder") : 0);
                frontendList.add(converted);
            }

            result.put("success", true);
            result.put("data", frontendList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "FAQ 조회 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * FAQ 등록
     * POST /api/faqs
     */
    @PostMapping("/api/faqs")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createFaq(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            String question = (String) body.get("question");
            String answer = (String) body.get("answer");

            if (question == null || question.trim().isEmpty() ||
                answer == null || answer.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "question and answer are required");
                return ResponseEntity.badRequest().body(result);
            }

            // 프론트엔드 → 백엔드 필드 변환
            Map<String, Object> param = new HashMap<>();
            param.put("question", question);
            param.put("answer", answer);
            param.put("category", body.get("category") != null ? body.get("category") : "");

            // display: true/false → displayYn: Y/N
            Object display = body.get("display");
            param.put("displayYn", (display == null || Boolean.TRUE.equals(display)) ? "Y" : "N");

            // order → displayOrder
            Object order = body.get("order");
            param.put("displayOrder", order != null ? Integer.valueOf(order.toString()) : 0);

            Map<String, Object> svcResult = faqSvc.insertFaq(param);

            // 응답 변환
            Map<String, Object> created = new HashMap<>();
            created.put("id", String.valueOf(param.get("faqSeq")));
            created.put("question", question);
            created.put("answer", answer);
            created.put("category", param.get("category"));
            created.put("display", "Y".equals(param.get("displayYn")));
            created.put("order", param.get("displayOrder"));

            result.put("success", "Y".equals(svcResult.get("isReg")));
            result.put("data", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "FAQ 등록 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * FAQ 수정
     * PUT /api/faqs/{id}
     */
    @PutMapping("/api/faqs/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFaq(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("faqSeq", Integer.valueOf(id));

            if (body.get("question") != null) {
                param.put("question", body.get("question"));
            }
            if (body.get("answer") != null) {
                param.put("answer", body.get("answer"));
            }
            if (body.get("category") != null) {
                param.put("category", body.get("category"));
            }
            if (body.get("display") != null) {
                param.put("displayYn", Boolean.TRUE.equals(body.get("display")) ? "Y" : "N");
            }
            if (body.get("order") != null) {
                param.put("displayOrder", Integer.valueOf(body.get("order").toString()));
            }

            // 기존 데이터 조회 후 병합
            Map<String, Object> existing = faqSvc.getFaq(param);
            if (existing.get("view") == null) {
                result.put("success", false);
                result.put("message", "FAQ not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            Map<String, Object> view = (Map<String, Object>) existing.get("view");
            if (!param.containsKey("question")) param.put("question", view.get("question"));
            if (!param.containsKey("answer")) param.put("answer", view.get("answer"));
            if (!param.containsKey("category")) param.put("category", view.get("category"));
            if (!param.containsKey("displayYn")) param.put("displayYn", view.get("displayYn"));
            if (!param.containsKey("displayOrder")) param.put("displayOrder", view.get("displayOrder"));

            Map<String, Object> svcResult = faqSvc.updateFaq(param);

            // 응답 변환
            Map<String, Object> updated = new HashMap<>();
            updated.put("id", id);
            updated.put("question", param.get("question"));
            updated.put("answer", param.get("answer"));
            updated.put("category", param.get("category"));
            updated.put("display", "Y".equals(param.get("displayYn")));
            updated.put("order", param.get("displayOrder"));

            result.put("success", "Y".equals(svcResult.get("isUpd")));
            result.put("data", updated);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "FAQ 수정 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * FAQ 삭제
     * DELETE /api/faqs/{id}
     */
    @DeleteMapping("/api/faqs/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFaq(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("faqSeq", Integer.valueOf(id));

            // 존재 여부 확인
            Map<String, Object> existing = faqSvc.getFaq(param);
            if (existing.get("view") == null) {
                result.put("success", false);
                result.put("message", "FAQ not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            Map<String, Object> svcResult = faqSvc.deleteFaq(param);
            result.put("success", "Y".equals(svcResult.get("isDel")));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "FAQ 삭제 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}

