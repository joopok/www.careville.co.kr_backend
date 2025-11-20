package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewMapper {

	int getReviewCnt(HashMap<String,Object> paramMap);

	List<HashMap<String,Object>> getReviewList(HashMap<String,Object> paramMap);

	HashMap<String,Object> getReviewView(HashMap<String,Object> paramMap);

	HashMap<String,Object> getReviewViewWithPassword(HashMap<String,Object> paramMap);

	int setReviewReg(HashMap<String,Object> paramMap);

	int setReviewUpd(HashMap<String,Object> paramMap);

	int setReviewDispUpd(HashMap<String,Object> paramMap);

	int setReviewDel(HashMap<String,Object> paramMap);

}