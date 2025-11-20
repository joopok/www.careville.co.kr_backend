package kr.co.cleaning.core.config;

import java.util.LinkedHashMap;

import kr.co.cleaning.core.utils.SUtils;

public class LinkedhashMapCamel extends LinkedHashMap {

	private static final long serialVersionUID = 6723434363565852261L;

	@Override
	public Object put(Object key, Object value) {

		String camelKey = toCamelCase(SUtils.nvl(key).toLowerCase());

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