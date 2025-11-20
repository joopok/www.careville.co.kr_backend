package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CmmnMapper {

	/* 공통코드 테이블 */
	List<HashMap<String,Object>> getCodeList(String groupCd);

	/* 카테고리 목록 */
	List<HashMap<String,Object>> getServiceCdList();

	List<HashMap<String,Object>> getProductCdList();

	/* 파일 테이블 */
	List<HashMap<String,Object>> getFileList(HashMap<String,Object> paramMap);

	HashMap<String,Object> getFileView(HashMap<String,Object> paramMap);

	int setFileInsert(HashMap<String,Object> paramMap);

	int setFileDel(HashMap<String,Object> paramMap);

	/* 파일관계 테이블 */
	List<HashMap<String,Object>> getFileRelationList(HashMap<String,Object> paramMap);

	int setFileRelationInsert(HashMap<String,Object> paramMap);

	int setFileRelationUpdate(HashMap<String,Object> paramMap);

	int setFileRelationDelFirst(HashMap<String,Object> paramMap);

	int setFileRelationDelSecond(HashMap<String,Object> paramMap);

	HashMap<String,Object> getSignIn(HashMap<String,Object> paramMap);

}
