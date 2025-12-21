package kr.co.cleaning.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FaqMapper {

    int selectFaqListCnt(Map<String, Object> param);

    List<Map<String, Object>> selectFaqList(Map<String, Object> param);

    Map<String, Object> selectFaq(Map<String, Object> param);

    int insertFaq(Map<String, Object> param);

    int updateFaq(Map<String, Object> param);

    int deleteFaq(Map<String, Object> param);
}

