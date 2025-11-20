package kr.co.cleaning.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/* *페이징*
pageUtil.setCurrPage(paramMap);	// res : currPage, req : currPage,startNum,endNum
List rsList			= mapper.list(paramMap);
int totalRowCnt		= mapper.totalCnt(paramMap);
pageUtil.setTotalRowCnt(totalRowCnt);

HashMap rsMap		= new HashMap();
rsMap.put("rsList"		, rsList);
rsMap.put("paramMap"	, paramMap);
rsMap.put("rowNum"		, pageUtil.getRowNum());
rsMap.put("paging"		, pageUtil.getPaging(1));
*/
@Component
public class PageUtil {

	@Value("${kframe.viewRowCnt}")
	private int viewRowCnt;		// 화면에 보여질 레코드 수

	@Value("${kframe.viewPageCnt}")
	private int viewPageCnt;	// 화면에 보여질 페이지 수

	private int totalRowCnt;	// 전체 레코드 수
	private int currPage;		// 현제 페이지
	private int rowNum;			// 리스트 인덱스(번호)에서 사용

	public int getTotalRowCnt() {
		return totalRowCnt;
	}

	public void setTotalRowCnt(int totalRowCnt) {
		this.rowNum			= totalRowCnt - ((this.currPage-1) * this.viewRowCnt);	// 리스트 인덱스(번호)에서 사용
		this.totalRowCnt 	= totalRowCnt;
	}

	public int getViewRowCnt() {
		return viewRowCnt;
	}

	public void setViewRowCnt(int viewRowCnt) {
		this.viewRowCnt = viewRowCnt;
	}

	public int getViewPageCnt() {
		return viewPageCnt;
	}

	public void setViewPageCnt(int viewPageCnt) {
		this.viewPageCnt = viewPageCnt;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(HashMap paramMap) {
		this.currPage 		= SUtils.strToInt(paramMap.get("currPage")		,1);			// 현제 페이지
		this.viewRowCnt		= SUtils.strToInt(paramMap.get("viewRowCnt")	,viewRowCnt);	// 화면에 보여질 레코드 수
		this.viewPageCnt	= SUtils.strToInt(paramMap.get("viewPageCnt")	,viewPageCnt);	// 화면에 보여질 페이지 수

		paramMap.put("startNum"		,(currPage-1) * this.viewRowCnt + 1);	// 리스트 시작 레코드
		paramMap.put("endNum"		,this.viewRowCnt * currPage);			// 리스트 마지락 레코드

		// H2DB, MYSQL, MARIA
		paramMap.put("limitStartNum"	,(currPage-1) * this.viewRowCnt);	// 시작위치
		paramMap.put("limitViewRowCnt"	,viewRowCnt);						// 보여질 레코드 수
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	private int totalPage;		// 페이징 계산(전체 페이징)
	private int pageGroupNum;	// 페이징 계산(페이징의 그룹)
	private int stPage;			// 페이징 계산(시작 페이징)
	private int edPage;			// 페이징 계산(마지막 페이징)

	public Object getPaging(String type){
		// totalRowCnt	- 전체 레코드 수
		// viewRowCnt	- 화면에 보여질 레코드 수
		// viewPageCnt	- 화면에 보여질 페이지 수
		// currPage		- 현제 페이지
		StringBuffer rsStr	= new StringBuffer();
		this.totalPage 		= (int)Math.ceil((double)this.totalRowCnt / this.viewRowCnt);
		this.pageGroupNum	= (int)Math.ceil((double)this.currPage / this.viewPageCnt);
		this.stPage			= (this.pageGroupNum - 1) * this.viewPageCnt + 1;
		this.edPage			= this.stPage + this.viewPageCnt - 1;

		if(this.edPage > this.totalPage) this.edPage = this.totalPage;

		if("json".equals(type)){
			return getPagingShapeJson();

		}else{	// html
			return getPagingShapeHtml();

		}

	}

	public String getPagingShapeHtml(){
		StringBuffer sb 	= new StringBuffer();
		sb.append("<ul class=\"pagination\">");
		sb.append("<li class=\"page-item "+(this.currPage <= this.viewPageCnt?"disabled":"")+"\"><a href=\"javascript:void(0);\" class=\"page-link\" data-call_idx=\""+(this.stPage-this.viewPageCnt)+"\">&lt;&lt;</a></li>");
		sb.append("<li class=\"page-item "+(this.currPage <= 1 ?"disabled":"")+"\"><a href=\"javascript:void(0);\" class=\"page-link\" data-call_idx=\""+(this.currPage-1)+"\">&lt;</a></li>");

		for(int i = this.stPage; i <= this.edPage; i++){
			if(this.totalPage < i) break;
			if(i == this.currPage){
				sb.append("<li class=\"page-item active\"><a href=\"javascript:void(0);\" class=\"page-link\" class=\"is-active\" data-call_idx=\""+i+"\">"+i+"</a></li>");
			}else{
				sb.append("<li class=\"page-item\"><a href=\"javascript:void(0);\" class=\"page-link\" data-call_idx=\""+i+"\">"+i+"</a></li>");
			}
		}

		sb.append("<li class=\"page-item "+(this.currPage >= this.totalPage?"disabled":"")+"\"><a href=\"javascript:void(0);\" class=\"page-link\" data-call_idx=\""+(this.currPage+1)+"\">&gt;</a></li>");
		sb.append("<li class=\"page-item "+(this.stPage+this.viewPageCnt>= this.totalPage?"disabled":"")+"\"><a href=\"javascript:void(0);\" class=\"page-link\" data-call_idx=\""+(this.stPage+this.viewPageCnt)+"\")>&gt;&gt;</a></li>");
		sb.append("</ul>");

		return sb.toString();
	}

	public HashMap<String,Object> getPagingShapeJson(){

		HashMap<String,Object> objMap	= new LinkedHashMap<>();
		List<Integer> btnList			= new ArrayList<>();

		int prev	= 0;
		int next	= 0;

		if(this.currPage > this.viewPageCnt){
			prev	= (this.stPage-this.viewPageCnt);
		}
		for(int i = this.stPage; i <= this.edPage; i++){
			if(this.totalPage < i) break;
			btnList.add(i);
		}
		if(this.stPage+this.viewPageCnt < this.totalPage) {
			next	= (this.stPage+this.viewPageCnt);
		}

		objMap.put("prev"		,prev);
		objMap.put("next"		,next);
		objMap.put("btnCnt"		,this.viewPageCnt);
		objMap.put("order"		,btnList);
		objMap.put("currPage"	,this.currPage);

		return objMap;
	}

}
