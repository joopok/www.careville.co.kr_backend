package kr.co.cleaning.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper {

    List<Map<String, Object>> selectConfigList(Map<String, Object> param);

    List<Map<String, Object>> selectConfigByGroup(Map<String, Object> param);

    Map<String, Object> selectConfig(Map<String, Object> param);

    String selectConfigValue(String configKey);

    int insertConfig(Map<String, Object> param);

    int updateConfig(Map<String, Object> param);

    int updateConfigValue(Map<String, Object> param);

    int deleteConfig(Map<String, Object> param);
}
