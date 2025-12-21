package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cleaning.mapper.ConfigMapper;

@Service
public class ConfigSvc {

    @Autowired
    private ConfigMapper configMapper;

    /**
     * 설정 전체 목록 조회
     */
    public Map<String, Object> getConfigList(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        List<Map<String, Object>> list = configMapper.selectConfigList(param);
        rs.put("list", list);
        rs.put("totalCount", list.size());
        return rs;
    }

    /**
     * 그룹별 설정 목록 조회
     */
    public Map<String, Object> getConfigByGroup(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        List<Map<String, Object>> list = configMapper.selectConfigByGroup(param);
        rs.put("list", list);
        return rs;
    }

    /**
     * 모든 그룹의 설정을 그룹별로 묶어서 조회
     */
    public Map<String, Object> getAllConfigGrouped() {
        Map<String, Object> rs = new HashMap<>();
        Map<String, Object> param = new HashMap<>();

        List<Map<String, Object>> allConfigs = configMapper.selectConfigList(param);

        // 그룹별로 분류
        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        String[] groups = {"BASIC", "CONTACT", "BUSINESS", "SNS"};
        String[] groupNames = {"기본 정보", "연락처 정보", "운영 정보", "SNS 정보"};

        for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            String groupName = groupNames[i];
            param.put("configGroup", group);
            List<Map<String, Object>> groupList = configMapper.selectConfigByGroup(param);
            if (!groupList.isEmpty()) {
                rs.put(group, groupList);
                rs.put(group + "_NAME", groupName);
            }
        }

        rs.put("groups", groups);
        rs.put("groupNames", groupNames);
        rs.put("allConfigs", allConfigs);

        return rs;
    }

    /**
     * 설정 단건 조회
     */
    public Map<String, Object> getConfig(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        rs.put("view", configMapper.selectConfig(param));
        return rs;
    }

    /**
     * 설정 값만 조회
     */
    public String getConfigValue(String configKey) {
        return configMapper.selectConfigValue(configKey);
    }

    /**
     * 설정 등록
     */
    @Transactional
    public Map<String, Object> insertConfig(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = configMapper.insertConfig(param);
        rs.put("isReg", cnt > 0 ? "Y" : "N");
        return rs;
    }

    /**
     * 설정 수정
     */
    @Transactional
    public Map<String, Object> updateConfig(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = configMapper.updateConfig(param);
        rs.put("isUpd", cnt > 0 ? "Y" : "N");
        return rs;
    }

    /**
     * 설정 값만 수정
     */
    @Transactional
    public Map<String, Object> updateConfigValue(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = configMapper.updateConfigValue(param);
        rs.put("isUpd", cnt > 0 ? "Y" : "N");
        return rs;
    }

    /**
     * 여러 설정 일괄 수정
     */
    @Transactional
    public Map<String, Object> updateConfigs(List<Map<String, Object>> configs) {
        Map<String, Object> rs = new HashMap<>();
        int successCnt = 0;

        for (Map<String, Object> config : configs) {
            int cnt = configMapper.updateConfigValue(config);
            if (cnt > 0) successCnt++;
        }

        rs.put("isUpd", successCnt > 0 ? "Y" : "N");
        rs.put("updatedCount", successCnt);
        return rs;
    }

    /**
     * 설정 삭제
     */
    @Transactional
    public Map<String, Object> deleteConfig(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = configMapper.deleteConfig(param);
        rs.put("isDel", cnt > 0 ? "Y" : "N");
        return rs;
    }
}
