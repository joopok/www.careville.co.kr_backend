package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

	int getBoardCnt(HashMap<String,Object> paramMap);

	List<HashMap<String,Object>> getBoardList(HashMap<String,Object> paramMap);

	HashMap<String,Object> getBoardView(HashMap<String,Object> paramMap);

	int setBoardUpd(HashMap<String,Object> paramMap);

	int setBoardReg(HashMap<String,Object> paramMap);

}
