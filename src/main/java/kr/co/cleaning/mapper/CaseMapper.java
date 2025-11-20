package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaseMapper {

	List<HashMap<String,Object>> getCaseServiceList();

	int getCaseCnt(HashMap<String,Object> paramMap);

	List<HashMap<String,Object>> getCaseList(HashMap<String,Object> paramMap);

	HashMap<String,Object> getCaseView(HashMap<String,Object> paramMap);

	int setCaseUpd(HashMap<String,Object> paramMap);

	int setCaseReg(HashMap<String,Object> paramMap);

	int setCaseDel(HashMap<String,Object> paramMap);

}
