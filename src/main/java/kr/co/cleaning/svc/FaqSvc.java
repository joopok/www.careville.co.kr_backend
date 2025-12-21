package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.mapper.FaqMapper;

@Service
public class FaqSvc {

    @Autowired
    private FaqMapper faqMapper;

    @Autowired
    private PageUtil pageUtil;

    public Map<String, Object> getFaqList(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();

        // 기본 페이지 설정
        pageUtil.setCurrPage((HashMap<String, Object>) param);
        int total = faqMapper.selectFaqListCnt(param);
        pageUtil.setTotalRowCnt(total);

        param.put("rowStrt", param.get("limitStartNum"));
        param.put("rowLimit", param.get("limitViewRowCnt"));

        List<Map<String, Object>> list = faqMapper.selectFaqList(param);

        rs.put("list", list);
        rs.put("pagination", pageUtil.getPaging("json"));
        rs.put("rowNum", pageUtil.getRowNum());
        rs.put("totalCount", total);
        return rs;
    }

    public Map<String, Object> getFaq(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        rs.put("view", faqMapper.selectFaq(param));
        return rs;
    }

    public Map<String, Object> insertFaq(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = faqMapper.insertFaq(param);
        rs.put("isReg", cnt > 0 ? "Y" : "N");
        rs.put("faqSeq", param.get("faqSeq"));
        return rs;
    }

    public Map<String, Object> updateFaq(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = faqMapper.updateFaq(param);
        rs.put("isUpd", cnt > 0 ? "Y" : "N");
        return rs;
    }

    public Map<String, Object> deleteFaq(Map<String, Object> param) {
        Map<String, Object> rs = new HashMap<>();
        int cnt = faqMapper.deleteFaq(param);
        rs.put("isDel", cnt > 0 ? "Y" : "N");
        return rs;
    }

    // 공개 API용
    public List<Map<String, Object>> getPublicFaqList(Map<String, Object> param) {
        param.put("displayYn", "Y");
        return faqMapper.selectFaqList(param);
    }
}

