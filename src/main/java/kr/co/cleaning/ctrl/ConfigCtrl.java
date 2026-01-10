package kr.co.cleaning.ctrl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.utils.FileUtil;
import kr.co.cleaning.svc.ConfigSvc;

@Controller
public class ConfigCtrl {

    @Autowired
    private ConfigSvc configSvc;

    @Autowired
    private FileUtil fileUtil;

    @Value("${kframe.filePath}")
    private String filePath;

    // 히어로 이미지 최대 파일 크기 (5MB)
    private static final long MAX_HERO_IMAGE_SIZE = 5 * 1024 * 1024;

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

    // ======================== 히어로 이미지 관리 ========================

    /**
     * 히어로 이미지 업로드
     * POST /heroImageUpload.do
     */
    @PostMapping("/heroImageUpload.do")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadHeroImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("configKey") String configKey) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 파일 크기 체크 (5MB 제한)
            if (file.getSize() > MAX_HERO_IMAGE_SIZE) {
                result.put("success", false);
                result.put("message", "파일 크기는 5MB 이하만 가능합니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 이미지 파일 체크
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("success", false);
                result.put("message", "이미지 파일만 업로드 가능합니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 기존 파일 삭제
            String oldValue = configSvc.getConfigValue(configKey);
            if (oldValue != null && !oldValue.isEmpty()) {
                try {
                    File oldFile = new File(filePath + "/hero", oldValue);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                } catch (Exception e) {
                    // 기존 파일 삭제 실패해도 계속 진행
                }
            }

            // 파일 저장
            List<MultipartFile> files = new ArrayList<>();
            files.add(file);
            List<HashMap<String, Object>> uploadResult = fileUtil.fileUpload(files, false);

            if (uploadResult.isEmpty()) {
                result.put("success", false);
                result.put("message", "파일 업로드에 실패했습니다.");
                return ResponseEntity.internalServerError().body(result);
            }

            HashMap<String, Object> fileInfo = uploadResult.get(0);
            String savedPath = fileInfo.get("filePathEdit") + "/" + fileInfo.get("fileFakeNm");

            // DB 설정 값 업데이트
            Map<String, Object> param = new HashMap<>();
            param.put("configKey", configKey);
            param.put("configValue", savedPath);
            configSvc.updateConfigValue(param);

            result.put("success", true);
            result.put("message", "이미지가 업로드되었습니다.");
            result.put("filePath", savedPath);
            result.put("configKey", configKey);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 히어로 이미지 삭제
     * POST /heroImageDelete.do
     */
    @PostMapping("/heroImageDelete.do")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteHeroImage(
            @RequestParam("configKey") String configKey) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 기존 파일 경로 조회
            String oldValue = configSvc.getConfigValue(configKey);

            // 파일 삭제
            if (oldValue != null && !oldValue.isEmpty()) {
                try {
                    File oldFile = new File(filePath, oldValue);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                } catch (Exception e) {
                    // 파일 삭제 실패해도 DB는 업데이트
                }
            }

            // DB 설정 값 비우기
            Map<String, Object> param = new HashMap<>();
            param.put("configKey", configKey);
            param.put("configValue", "");
            configSvc.updateConfigValue(param);

            result.put("success", true);
            result.put("message", "이미지가 삭제되었습니다.");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 공개 API: 히어로 이미지 목록 조회
     * GET /api/v1/hero-images
     */
    @GetMapping("/api/v1/hero-images")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getHeroImages() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> heroImages = configSvc.getHeroImages();

            // 이미지 URL 형식으로 변환
            List<Map<String, Object>> images = new ArrayList<>();
            for (Map<String, Object> item : heroImages) {
                Map<String, Object> img = new HashMap<>();
                img.put("key", item.get("configKey"));
                img.put("order", item.get("displayOrder"));
                String path = (String) item.get("configValue");
                if (path != null && !path.isEmpty()) {
                    img.put("url", "/fileDown.do?path=" + path);
                    img.put("path", path);
                }
                images.add(img);
            }

            result.put("success", true);
            result.put("images", images);
            result.put("count", images.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "히어로 이미지 조회 실패");
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
