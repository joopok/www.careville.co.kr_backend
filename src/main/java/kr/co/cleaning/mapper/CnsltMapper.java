package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CnsltMapper {

	int getCnsltCnt(HashMap<String,Object> paramMap);

	List<HashMap<String,Object>> getCnsltList(HashMap<String,Object> paramMap);

	HashMap<String,Object> getCnsltView(HashMap<String,Object> paramMap);

	int setCnsltUpd(HashMap<String,Object> paramMap);

	int setCnsltReg(HashMap<String,Object> paramMap);

}
