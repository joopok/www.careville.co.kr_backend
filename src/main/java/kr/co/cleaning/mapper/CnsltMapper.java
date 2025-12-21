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

	/**
	 * 서비스명으로 서비스 코드 조회
	 */
	String getServiceCdByName(String serviceNm);

	/**
	 * 상품명으로 상품 번호 조회
	 */
	Integer getProductNoByName(String productNm);

}
