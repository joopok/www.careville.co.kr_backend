package kr.co.cleaning.core.config;

import java.util.LinkedHashMap;

import kr.co.cleaning.core.utils.SUtils;

public class LinkedhashMapCamel extends LinkedHashMap {

	private static final long serialVersionUID = 6723434363565852261L;

	@Override
	public Object put(Object key, Object value) {

		String strKey = SUtils.nvl(key);

		// 키 변환 로직:
		// 1. 언더스코어 포함 → camelCase 변환 (예: FILE_SEQ → fileSeq)
		// 2. 모두 대문자 → 소문자 변환 (예: HASHTAG → hashtag)
		// 3. 소문자 포함 → 그대로 유지 (예: viewFileSeq → viewFileSeq)
		String camelKey;
		if (strKey.contains("_")) {
			camelKey = toCamelCase(strKey.toLowerCase());
		} else if (strKey.equals(strKey.toUpperCase()) && strKey.length() > 0) {
			// 모두 대문자인 경우 (DB 컬럼명) → 소문자로 변환
			camelKey = strKey.toLowerCase();
		} else {
			// 이미 camelCase인 경우 (서비스에서 추가한 키) → 그대로 유지
			camelKey = strKey;
		}

		return super.put(camelKey, value);
	}

	private String toCamelCase(String str) {

		String strLower 	= SUtils.nvl(str);

		if(!strLower.equals("") || strLower.contains("_")){

			StringBuilder sb	= new StringBuilder();
			boolean nextUpper	= false;

			for(char c : strLower.toCharArray()){
				if(c == '_'){
					nextUpper	= true;

				}else{
					sb.append(nextUpper ? Character.toUpperCase(c) : c);
					nextUpper	= false;
				}
			}
			return sb.toString();

		}else{
			return str;
		}
    }
}