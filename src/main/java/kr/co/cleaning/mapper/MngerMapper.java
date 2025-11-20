package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MngerMapper {

	int getMngerCnt(HashMap<String,Object> paramMap);

	List<HashMap<String,Object>> getMngerList(HashMap<String,Object> paramMap);

	HashMap<String,Object> getMngerView(HashMap<String,Object> paramMap);

	int setMngerUpd(HashMap<String,Object> paramMap);

	int setMngrSttusUpd(HashMap<String,Object> paramMap);

	int setMngrPwUpd(HashMap<String,Object> paramMap);

	int setMngrDel(HashMap<String,Object> paramMap);

	int setMngerReg(HashMap<String,Object> paramMap);

	int getMngerIdDupChk(HashMap<String,Object> paramMap);

}
