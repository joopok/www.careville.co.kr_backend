package kr.co.cleaning.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.svc.ConfigSvc;

@Controller
public class ConfigCtrl {

    @Autowired
    private ConfigSvc configSvc;

    /**
     * 설정 관리 화면
     */
    @RequestMapping("/apage/config010.do")
    public String configPage(HttpServletRequest req, HttpServletResponse res,
                             @RequestParam(required = false) Map<String, Object> params,
                             ModelMap model) throws Exception {
        if (params == null) params = new HashMap<>();
        model.addAttribute("params", params);
        return "apage/config/config010";
    }

    /**
     * 설정 목록 조회 (JSON)
     */
    @PostMapping("/configList.do")
    public String configList(HttpServletRequest req, HttpServletResponse res,
                             @RequestParam HashMap<String, Object> paramMap,
                             ModelMap model) throws Exception {
        model.addAllAttributes(configSvc.getAllConfigGrouped());
        return "jsonView";
    }

    /**
     * 그룹별 설정 조회 (JSON)
     */
    @PostMapping("/configByGroup.do")
    public String configByGroup(HttpServletRequest req, HttpServletResponse res,
                                @RequestParam HashMap<String, Object> paramMap,
                                ModelMap model) throws Exception {
        model.addAllAttributes(configSvc.getConfigByGroup(paramMap));
        return "jsonView";
    }

    /**
     * 설정 단건 조회 (JSON)
     */
    @PostMapping("/configView.do")
    public String configView(HttpServletRequest req, HttpServletResponse res,
                             @RequestParam HashMap<String, Object> paramMap,
                             ModelMap model) throws Exception {
        model.addAllAttributes(configSvc.getConfig(paramMap));
        return "jsonView";
    }

    /**
     * 설정 등록 (JSON)
     */
    @PostMapping("/configReg.do")
    public String configReg(HttpServletRequest req, HttpServletResponse res,
                            @RequestParam HashMap<String, Object> paramMap,
                            ModelMap model) throws Exception {
        model.addAllAttributes(configSvc.insertConfig(paramMap));
        return "jsonView";
    }

    /**
     * 설정 수정 (JSON)
     */
    @PostMapping("/configUpd.do")
    public String configUpd(HttpServletRequest req, HttpServletResponse res,
                            @RequestParam HashMap<String, Object> paramMap,
                            ModelMap model) throws Exception {
        model.addAllAttributes(configSvc.updateConfigValue(paramMap));
        return "jsonView";
    }

    /**
     * 설정 일괄 저장 (JSON)
     */
    @PostMapping("/configSaveAll.do")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> configSaveAll(@RequestBody List<Map<String, Object>> configs) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> svcResult = configSvc.updateConfigs(configs);
            result.put("success", "Y".equals(svcResult.get("isUpd")));
            result.put("updatedCount", svcResult.get("updatedCount"));
            result.put("message", "저장되었습니다.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "저장 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 설정 삭제 (JSON)
     */
    @PostMapping("/configDel.do")
    public String configDel(HttpServletRequest req, HttpServletResponse res,
                            @RequestParam HashMap<String, Object> paramMap,
                            ModelMap model) throws Exception {
        model.addAllAttributes(configSvc.deleteConfig(paramMap));
        return "jsonView";
    }

    // ======================== 공개 API ========================

    /**
     * 공개 API: 설정 값 조회
     * GET /api/v1/config?key=SITE_NAME
     */
    @GetMapping("/api/v1/config")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPublicConfig(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            String value = configSvc.getConfigValue(key);
            result.put("success", true);
            result.put("key", key);
            result.put("value", value);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "설정 조회 실패");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 공개 API: 그룹별 설정 목록 조회
     * GET /api/v1/configs?group=CONTACT
     */
    @GetMapping("/api/v1/configs")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPublicConfigs(
            @RequestParam(required = false) String group) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> param = new HashMap<>();
            if (group != null && !group.isEmpty()) {
                param.put("configGroup", group);
            }
            Map<String, Object> data = configSvc.getConfigList(param);
            result.put("success", true);
            result.put("data", data.get("list"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "설정 조회 실패");
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
